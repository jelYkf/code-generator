package com.rune.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rune.database.DynamicDataSourceContextHolder;
import com.rune.database.DynamicRoutingDataSource;
import com.rune.domain.bo.ColumnInfo;
import com.rune.domain.bo.TableInfo;
import com.rune.domain.dto.TableDto;
import com.rune.domain.entity.TemplateDatabaseUrl;
import com.rune.mapper.DatabaseUrlMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sedate
 * @date 2023/7/17 14:59
 * @description
 */
@Service
@RequiredArgsConstructor
public class TableService {

    private final DatabaseUrlMapper databaseUrlMapper;

    private final DynamicRoutingDataSource dynamicRoutingDataSource;

    private final SqlSessionFactory sqlSessionFactory;

    HashSet<String> dataSourceName = new HashSet<>();

    public Page<TableInfo> query(TableDto tableDto) {
        Page<TableInfo> tableInfoPage = new Page<>();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            TemplateDatabaseUrl templateDatabaseUrl = databaseUrlMapper.selectById(tableDto.getDatabaseId());
            DynamicDataSourceContextHolder.setDataSourceKey(templateDatabaseUrl.getName());
            HashMap<String, Integer> map = new HashMap<>();
            map.put("current", tableDto.getCurrent() == 1 ? 0 : tableDto.getPageSize() * tableDto.getCurrent());
            map.put("pageSize", tableDto.getPageSize());
            List<TableInfo> tableInfos = sqlSession.selectList("com.rune.mapper.TableMapper.selectTableAll", map);
            List<Integer> integers = sqlSession.selectList("com.rune.mapper.TableMapper.selectTableCount");
            Integer integer = integers.get(0);
            tableInfoPage.setCurrent(tableDto.getCurrent());
            tableInfoPage.setTotal(integer);
            tableInfoPage.setRecords(tableInfos);
            tableInfoPage.setSize(tableDto.getPageSize());
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
        return tableInfoPage;
    }

    public Page<ColumnInfo> queryTableColumn(TableDto tableDto) {
        Page<ColumnInfo> columnInfoPage = new Page<>();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            TemplateDatabaseUrl templateDatabaseUrl = databaseUrlMapper.selectById(tableDto.getDatabaseId());
            DynamicDataSourceContextHolder.setDataSourceKey(templateDatabaseUrl.getName());
            HashMap<String, Object> map = new HashMap<>(3);
            map.put("current", tableDto.getCurrent() == 1 ? 0 : tableDto.getPageSize() * tableDto.getCurrent());
            map.put("pageSize", tableDto.getPageSize());
            map.put("tableName", tableDto.getTableName());
            List<ColumnInfo> columnInfos = sqlSession.selectList("com.rune.mapper.TableMapper.selectColumnInfo", map);
            Integer integer = (Integer) sqlSession.selectList("com.rune.mapper.TableMapper.selectColumnInfoCount", tableDto.getTableName()).get(0);
            columnInfoPage.setCurrent(tableDto.getCurrent());
            columnInfoPage.setTotal(integer);
            columnInfoPage.setRecords(columnInfos);
            columnInfoPage.setSize(tableDto.getPageSize());
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
        return columnInfoPage;
    }


    public HashMap<String, List<ColumnInfo>> queryTableColumnInfo(List<String> tableNames) {
        HashMap<String, List<ColumnInfo>> map = new HashMap<>();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            for (String tableName : tableNames) {
                List<ColumnInfo> columnInfos = sqlSession.selectList("com.rune.mapper.TableMapper.selectColumnInfo", tableName);
                map.put(tableName, columnInfos);
            }
        }

        return map;
    }

    public Map<String, TableInfo> queryTableInfo(List<String> tableNames) {
        Map<String, TableInfo> map = new HashMap<>();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            for (String tableName : tableNames) {
                List<Object> objects = sqlSession.selectList("com.rune.mapper.TableMapper.selectTableInfo", tableName);
                TableInfo tableInfo = (TableInfo) objects.get(0);
                map.put(tableName, tableInfo);
            }
        }
        return map;
    }

    public void origin() {
        List<TemplateDatabaseUrl> templateDatabaseUrls = databaseUrlMapper.selectList(null);
        List<String> names = templateDatabaseUrls.stream().map(TemplateDatabaseUrl::getName).collect(Collectors.toList());
        boolean restart = false;
        for (String name : names) {
            if (!dataSourceName.contains(name)) {
                restart = true;
            }
        }
        if (restart) {
            Map<Object, Object> targetDataSources = new HashMap<>();
            for (TemplateDatabaseUrl templateDatabaseUrl : templateDatabaseUrls) {
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
                hikariConfig.setJdbcUrl(templateDatabaseUrl.getUrl());
                hikariConfig.setUsername(templateDatabaseUrl.getUsername());
                hikariConfig.setPassword(templateDatabaseUrl.getPassword());
                HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
                targetDataSources.put(templateDatabaseUrl.getName(), hikariDataSource);
                dataSourceName.add(templateDatabaseUrl.getName());
            }
            dynamicRoutingDataSource.setTargetDataSources(targetDataSources);
            dynamicRoutingDataSource.afterPropertiesSet();
        }
    }

    public void test() {
        String sql = "SELECT `table_name`,\n" +
                "               engine,\n" +
                "               table_comment,\n" +
                "               create_time\n" +
                "        FROM information_schema.TABLES where table_schema = (SELECT DATABASE ( ))";

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            DynamicDataSourceContextHolder.setDataSourceKey("db");
            List<TableInfo> tableInfos = sqlSession.selectList("com.rune.mapper.TableMapper.selectTableAll");
            for (TableInfo tableInfo : tableInfos) {
                System.out.println(tableInfo);
            }
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

}
