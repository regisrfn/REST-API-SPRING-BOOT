package com.rufino.server.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class DatabaseConfig {
    
    Dotenv dotenv;
    String database_url;

    public DatabaseConfig() {
        dotenv = Dotenv.load();
        database_url = dotenv.get("DATABASE_API");
    }

    @Bean
    public DataSource postgresDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(database_url);
        dataSource.setUsername("postgres");
        dataSource.setPassword("mysecretpassword");

        return dataSource;
    }
}
