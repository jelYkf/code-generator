package com.rune.exception;

import com.rune.base.ApiError;
import com.rune.base.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author avalon
 * @date 22/3/30 19:38
 * @description 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//    /**
//     * 处理所有不可知的异常
//     *
//     * @param e /
//     * @return /
//     */
//    @ExceptionHandler(Throwable.class)
//    public ResponseEntity<ApiError> handleException(Throwable e) {
//        log.error(e.getMessage(), e);
//        return ApiResponse.error(e.getMessage());
//    }

    /**
     * 处理自定义异常
     *
     * @param e /
     * @return /
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> badRequestException(BadRequestException e) {
        log.error(e.getMessage(), e);
        return ApiResponse.error(e.getMessage());
    }


//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<ApiError> badCredentialsException(BadCredentialsException e) {
//        log.error(e.getMessage(), e);
//        return ApiResponse.error(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
//    }
//
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ApiError> dataIntegrityViolationException(DataIntegrityViolationException e) {
//        log.error(e.getMessage(), e);
//        return ApiResponse.error(HttpStatus.BAD_REQUEST, "数据完整性错误");
//    }
//
//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<ApiError> noHandlerFoundException(NoHandlerFoundException e) {
//        log.error(e.getMessage(), e);
//        return ApiResponse.error(HttpStatus.NOT_FOUND, "请求资源不存在");
//    }
}
