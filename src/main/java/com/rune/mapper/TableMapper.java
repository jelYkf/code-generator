package com.rune.mapper;

import com.rune.domain.bo.ColumnInfo;
import com.rune.domain.bo.TableInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author one
 */
public interface TableMapper {

    TableInfo selectTableInfo(@Param("tableName") String tableName);

    List<TableInfo> selectTableAll(@Param("current") Integer current, @Param("pageSize") Integer pageSize);

    List<TableInfo> selectTableAllTest();

    Integer selectTableCount();

    List<ColumnInfo> selectColumnInfo(@Param("tableName") String tableName);

}
