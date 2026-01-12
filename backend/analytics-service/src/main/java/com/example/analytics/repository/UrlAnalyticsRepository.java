package com.example.analytics.repository;

import com.example.analytics.model.UrlAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UrlAnalyticsRepository
        extends JpaRepository<UrlAnalytics, String> {
        @Modifying
        @Query("""
        UPDATE UrlAnalytics u
        SET u.clickCount = u.clickCount + 1
        WHERE u.alias = :alias
    """)
        int incrementClick(@Param("alias") String alias);
}

