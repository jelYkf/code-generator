package com.rune.database;

/**
 * @author one
 */
public class DynamicDataSourceContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static String getDataSourceKey() {
        return contextHolder.get();
    }

    public static void setDataSourceKey(String dataSourceKey) {
        contextHolder.set(dataSourceKey);
    }

    public static void clearDataSourceKey() {
        contextHolder.remove();
    }
}