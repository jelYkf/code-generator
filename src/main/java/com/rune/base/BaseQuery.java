package com.rune.base;

import java.io.Serializable;

/**
 * @author avalon
 * @date 22/4/24 16:53
 * @description /
 */
public class BaseQuery implements Serializable {

    private Integer current;

    private Integer pageSize;

    public BaseQuery() {
        current = 1;
        pageSize = 20;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
