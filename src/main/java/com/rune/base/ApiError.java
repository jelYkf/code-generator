package com.rune.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author avalon
 * @date 22/4/7 17:39
 * @description Api 请求失败返回封装
 */
@Getter
@Setter
public class ApiError {

    private boolean success;

    private String errorMessage;

    private Integer showType;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime timestamp;

    public ApiError() {
        showType = 3;
        timestamp = LocalDateTime.now();
    }

    public ApiError(String errorMessage) {
        showType = 3;
        this.errorMessage = errorMessage;
        timestamp = LocalDateTime.now();
    }
}
