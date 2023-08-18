package com.rune.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author avalon
 * @date 22/4/7 16:52
 * @description 请求错误异常抛出
 */
@Getter
public class BadRequestException extends RuntimeException {

    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public BadRequestException(String msg) {
        super(msg);
    }

    public BadRequestException(HttpStatus status, String msg) {
        super(msg);
        this.status = status;
    }
}
