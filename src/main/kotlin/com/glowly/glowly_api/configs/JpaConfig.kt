package com.glowly.glowly_api.configs

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaRepositories(basePackages = ["com.glowly.glowly_api.repositories"])
@EnableTransactionManagement
class JpaConfig

