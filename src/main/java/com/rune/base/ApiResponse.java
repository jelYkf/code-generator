package com.rune.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author avalon
 * @date 22/7/26 14:14
 * @description Api 返回封装
 */
public class ApiResponse {

    public static ResponseEntity<ApiOk> ok() {
        return ResponseEntity.ok().body(new ApiOk());
    }

    public static ResponseEntity<ApiOk> ok(HttpStatus status) {
        return ResponseEntity.status(status).body(new ApiOk());
    }

    public static ResponseEntity<ApiOk> ok(Object data) {
        return ResponseEntity.ok().body(new ApiOk(data));
    }

    public static ResponseEntity<ApiOk> ok(HttpStatus status, Object data) {
        return ResponseEntity.status(status).body(new ApiOk(data));
    }

    public static ResponseEntity<ApiOk> page(Page<?> page) {
        ApiOk apiOk = new ApiOk();
        apiOk.setData(page.getRecords());
        apiOk.setCurrent(Integer.valueOf(String.valueOf(page.getCurrent())));
        apiOk.setPageSize(Integer.valueOf(String.valueOf(page.getSize())));
        apiOk.setTotal(page.getTotal());
        return ResponseEntity.ok().body(apiOk);
    }

    public static ResponseEntity<ApiOk> page(List<?> data, Integer current, Integer pageSize, Long total) {
        ApiOk apiOk = new ApiOk();
        apiOk.setData(data);
        apiOk.setCurrent(current + 1);
        apiOk.setPageSize(pageSize);
        apiOk.setTotal(total);
        return ResponseEntity.ok().body(apiOk);
    }

    public static ResponseEntity<ApiError> error(String message) {
        return ResponseEntity.badRequest().body(new ApiError(message));
    }

    public static ResponseEntity<ApiError> error(HttpStatus status, String message) {
        ApiError apiError = new ApiError();
        apiError.setErrorMessage(message);
        return ResponseEntity.status(status).body(apiError);
    }

    public static ResponseEntity<ApiError> error(HttpStatus status, String message, Integer showType) {
        ApiError apiError = new ApiError();
        apiError.setErrorMessage(message);
        apiError.setShowType(showType);
        return ResponseEntity.status(status).body(apiError);
    }
}
