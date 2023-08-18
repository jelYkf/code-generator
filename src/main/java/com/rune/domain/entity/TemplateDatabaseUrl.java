package com.rune.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sedate
 * @date 2023/7/17 14:50
 * @description
 */
@TableName("template_database_url")
@Getter
@Setter
public class TemplateDatabaseUrl {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String url;

    private String username;

    private String password;

}
