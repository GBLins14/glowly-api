package com.authenticator.authenticator_api.configs

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaRepositories(basePackages = ["com.authenticator.authenticator_api.repositories"])
@EnableTransactionManagement
class JpaConfig

