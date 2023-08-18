package com.rune.domain.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author sedate
 * @date 2023/7/17 16:14
 * @description
 */
@Getter
@Setter
@ToString
public class TableInfo {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 存储引擎
     */
    private String engine;

    /**
     * 表备注
     */
    private String tableComment;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
