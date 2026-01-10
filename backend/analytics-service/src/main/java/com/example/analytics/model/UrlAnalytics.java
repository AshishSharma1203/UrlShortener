package com.example.analytics.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "url_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlAnalytics {

    @Id
    @Column(nullable = false, unique = true)
    private String alias;

    @Column(nullable = false)
    private long clickCount;
}
