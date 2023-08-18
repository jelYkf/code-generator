package com.rune.domain.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author one
 */
@Getter
@Setter
@ToString
public class ColumnInfo {

    /**
     * 列表
     */
    private String columnName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 数据类型
     */
    private String javaDataType;

    /**
     * 数据类型
     */
    private String jsDataType;

    /**
     * 备注
     */
    private String columnComment;

    /**
     * 其他信息
     */
    private String extra;

    /**
     * 是否可以为空
     */
    private String isNullable;

    /**
     * 字段类型
     */
    private String columnType;

    /**
     * 索引类型
     */
    private String columnKey;

}
