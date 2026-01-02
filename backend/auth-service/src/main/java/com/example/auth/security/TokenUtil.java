package com.example.auth.security;

import com.example.auth.config.SecurityConstants;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtil {

    private static final SecureRandom secureRandom = new SecureRandom();

    private TokenUtil() {
        // Private constructor to prevent instantiation
    }

    public static String generateRefreshToken() {
        byte[] randomBytes = new byte[SecurityConstants.REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SecurityConstants.HASH_ALGORITHM);
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to hash token: " + e.getMessage(), e);
        }
    }
}
