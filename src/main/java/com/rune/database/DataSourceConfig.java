package com.rune.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.master")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DynamicRoutingDataSource dynamicRoutingDataSource() {
        DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(primaryDataSource());
        return dataSource;
    }

    @Primary
    @Bean
    public DataSource dataSource() {
        return dynamicRoutingDataSource();
    }

}
