package com.rune.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rune.base.ApiOk;
import com.rune.base.ApiResponse;
import com.rune.domain.bo.ColumnInfo;
import com.rune.domain.bo.TableInfo;
import com.rune.domain.dto.TableDto;
import com.rune.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sedate
 * @date 2023/7/17 17:54
 * @description
 */
@RestController
@RequestMapping("table")
public class TableApi {

    @Resource
    private TableService tableService;

    @GetMapping
    public ResponseEntity<ApiOk> queryTable(TableDto tableDto) {
        Page<TableInfo> query = tableService.query(tableDto);
        return ApiResponse.page(query);
    }

    @GetMapping("column")
    public ResponseEntity<ApiOk> queryTableColumn(TableDto tableDto) {
        Page<ColumnInfo> columnInfoPage = tableService.queryTableColumn(tableDto);
        return ApiResponse.page(columnInfoPage);
    }

    @GetMapping("origin")
    public ResponseEntity<ApiOk> origin() {
        tableService.origin();
        return ApiResponse.ok();
    }

    @GetMapping("test")
    public ResponseEntity<ApiOk> test() {
        tableService.test();
        return ApiResponse.ok();
    }

}
