package com.indra.jpa.orders.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class SeedRunner {

	@Bean
	ApplicationRunner seedOnStartup(DataSeeder seeder) {
		return args -> seeder.seed(100);
	}
}