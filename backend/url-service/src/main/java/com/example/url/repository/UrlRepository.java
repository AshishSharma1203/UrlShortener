package com.example.url.repository;

import com.example.url.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UrlRepository extends JpaRepository<Url, UUID> {

    // Case-insensitive alias lookup (for redirect)
    @Query("""
        SELECT u FROM Url u
        WHERE LOWER(u.alias) = LOWER(:alias)
          AND u.active = true
    """)
    Optional<Url> findActiveByAliasIgnoreCase(String alias);

    // Check alias uniqueness
    boolean existsByAliasIgnoreCase(String alias);

    // Dashboard: all URLs for user
    List<Url> findByUserIdAndActiveTrue(UUID userId);
}
