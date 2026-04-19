package com.fleet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing so created and modified timestamps can be populated automatically.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
