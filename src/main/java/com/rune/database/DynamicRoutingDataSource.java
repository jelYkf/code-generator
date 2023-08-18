package com.rune.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // 返回当前要使用的数据源的标识，例如租户ID、数据库名等
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }


}