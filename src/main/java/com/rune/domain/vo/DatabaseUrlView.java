package com.rune.domain.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sedate
 * @date 2023/7/17 14:50
 * @description
 */
@Getter
@Setter
public class DatabaseUrlView {

    private Integer id;

    private String name;

    private String address;

    private String port;

    private String database;

    private String username;

    private String password;

}
