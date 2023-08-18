package com.rune.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * @author avalon
 * @date 22/4/22 16:29
 * @description Api 请求成功返回封装
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiOk {

    private boolean success;

    private Object data;

    private Integer current;

    private Integer pageSize;

    private Long total;

    public ApiOk() {
        success = true;
    }

    public ApiOk(Object data) {
        success = true;
        this.data = data;
    }
}
