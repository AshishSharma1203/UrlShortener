package com.example.analytics.repository;

import com.example.analytics.model.UrlAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlAnalyticsRepository
        extends JpaRepository<UrlAnalytics, String> {
}
