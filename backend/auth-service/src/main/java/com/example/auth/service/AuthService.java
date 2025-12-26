package com.example.auth.service;

import com.example.auth.dto.*;
import com.example.auth.exception.AuthException;
import com.example.auth.exception.ErrorCode;
import com.example.auth.model.RefreshToken;
import com.example.auth.model.Role;
import com.example.auth.model.User;
import com.example.auth.repository.RefreshTokenRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtService;
import com.example.auth.security.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "Email already registered",
                    HttpStatus.CONFLICT
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(
                        ErrorCode.INVALID_CREDENTIALS,
                        "Invalid credentials",
                        HttpStatus.UNAUTHORIZED
                ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Invalid credentials",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String accessToken = jwtService.generateToken(user.getId(), user.getRole());

        // 🔐 Refresh token
        String rawRefreshToken = TokenUtil.generateRefreshToken();
        String hashedRefreshToken = TokenUtil.hashToken(rawRefreshToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashedRefreshToken)
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                rawRefreshToken,
                user.getRole().name()
        );
    }

    public AuthResponse refresh(RefreshRequest request) {

        String hashed = TokenUtil.hashToken(request.getRefreshToken());

        RefreshToken token = refreshTokenRepository.findByTokenHash(hashed)
                .orElseThrow(() -> new AuthException(
                        ErrorCode.UNAUTHORIZED,
                        "Invalid refresh token",
                        HttpStatus.UNAUTHORIZED
                ));

        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException(
                    ErrorCode.UNAUTHORIZED,
                    "Refresh token expired or revoked",
                    HttpStatus.UNAUTHORIZED
            );
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new AuthException(
                        ErrorCode.UNAUTHORIZED,
                        "User not found",
                        HttpStatus.UNAUTHORIZED
                ));

        String newAccessToken = jwtService.generateToken(user.getId(), user.getRole());

        return new AuthResponse(
                newAccessToken,
                request.getRefreshToken(), // reuse (rotation optional)
                user.getRole().name()
        );
    }

    public void logout(LogoutRequest request) {
        String hashed = TokenUtil.hashToken(request.getRefreshToken());

        RefreshToken token = refreshTokenRepository.findByTokenHash(hashed)
                .orElseThrow(() -> new AuthException(
                        ErrorCode.UNAUTHORIZED,
                        "Invalid refresh token",
                        HttpStatus.UNAUTHORIZED
                ));

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

}
