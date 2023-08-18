package com.rune.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rune.domain.dto.DatabaseUrlDto;
import com.rune.domain.dto.DatabaseUrlRequest;
import com.rune.domain.entity.TemplateDatabaseUrl;
import com.rune.domain.vo.DatabaseUrlView;
import com.rune.exception.BadRequestException;
import com.rune.mapper.DatabaseUrlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author sedate
 * @date 2023/7/17 14:59
 * @description
 */
@Service
@RequiredArgsConstructor
public class DatabaseUrlService {

    private final DatabaseUrlMapper mapper;

    private String jdbcUrl = "jdbc:mysql://{address}:{port}/{database}?serverTimezone=Asia/Shanghai";

    public void create(DatabaseUrlRequest attribute) {
        QueryWrapper<TemplateDatabaseUrl> queryWrapper = new QueryWrapper<>();
        String name = attribute.getName();
        queryWrapper.eq("name", name);
        Long selectCount = mapper.selectCount(queryWrapper);
        if (selectCount != null && selectCount > 0) {
            throw new BadRequestException("库中有相同的唯一标识");
        }
        TemplateDatabaseUrl templateDatabaseUrl = new TemplateDatabaseUrl();
        templateDatabaseUrl.setUsername(attribute.getUsername());
        templateDatabaseUrl.setPassword(attribute.getPassword());
        templateDatabaseUrl.setName(attribute.getName());
        String url = jdbcUrl.replace("{address}", attribute.getAddress()).replace("{port}", attribute.getPort()).replace("{database}", attribute.getDatabase());
        templateDatabaseUrl.setUrl(url);
        // 检验数据源
        checkOrigin(url, attribute.getUsername(), attribute.getPassword());
        mapper.insert(templateDatabaseUrl);
    }

    private void checkOrigin(String url, String username, String password) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.close();
        } catch (SQLException e) {
            throw new BadRequestException("数据源无效");
        }
    }

    public Page<DatabaseUrlView> query(DatabaseUrlDto databaseUrlDto) {
        QueryWrapper<TemplateDatabaseUrl> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(databaseUrlDto.getName())) {
            queryWrapper.like("name", databaseUrlDto.getName());
        }
        Page<TemplateDatabaseUrl> page = mapper.selectPage(new Page<>(databaseUrlDto.getCurrent(), databaseUrlDto.getPageSize()), queryWrapper);
        Page<DatabaseUrlView> databaseUrlViewPage = new Page<>();
        databaseUrlViewPage.setTotal(page.getTotal());
        databaseUrlViewPage.setSize(page.getSize());
        List<TemplateDatabaseUrl> records = page.getRecords();
        List<DatabaseUrlView> collect = records.stream().map(templateDatabaseUrl -> {
            DatabaseUrlView databaseUrlView = new DatabaseUrlView();
            BeanUtils.copyProperties(templateDatabaseUrl, databaseUrlView);
            replaceMatch(databaseUrlView, templateDatabaseUrl);
            return databaseUrlView;
        }).collect(Collectors.toList());
        databaseUrlViewPage.setRecords(collect);
        return databaseUrlViewPage;
    }

    private void replaceMatch(DatabaseUrlView databaseUrlView, TemplateDatabaseUrl templateDatabaseUrl) {
        String pattern = "://(.*?)\\?";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(templateDatabaseUrl.getUrl());
        if (matcher.find()) {
            try {
                String match = matcher.group(1);
                String[] split = match.split(":");
                databaseUrlView.setAddress(split[0]);
                String[] strings = split[1].split("/");
                databaseUrlView.setPort(strings[0]);
                databaseUrlView.setDatabase(strings[1]);
            } catch (Exception e) {
                throw new BadRequestException("数据库连接地址有误");
            }
        } else {
            throw new BadRequestException("数据库连接地址有误");
        }
    }

    public void update(DatabaseUrlRequest attribute) {
        QueryWrapper<TemplateDatabaseUrl> queryWrapper = new QueryWrapper<>();
        String name = attribute.getName();
        queryWrapper.ne("id", attribute.getId());
        queryWrapper.eq("name", name);
        Long selectCount = mapper.selectCount(queryWrapper);
        if (selectCount != null && selectCount > 0) {
            throw new BadRequestException("库中有相同的唯一标识");
        }
        TemplateDatabaseUrl templateDatabaseUrl = new TemplateDatabaseUrl();
        templateDatabaseUrl.setId(attribute.getId());
        templateDatabaseUrl.setUsername(attribute.getUsername());
        templateDatabaseUrl.setPassword(attribute.getPassword());
        templateDatabaseUrl.setName(name);
        String url = jdbcUrl.replace("{address}", attribute.getAddress()).replace("{port}", attribute.getPort()).replace("{database}", attribute.getDatabase());
        templateDatabaseUrl.setUrl(url);
        // 检验数据源
        checkOrigin(url, attribute.getUsername(), attribute.getPassword());
        mapper.updateById(templateDatabaseUrl);
    }

    public void delete(Integer id) {
        mapper.deleteById(id);
    }

}
