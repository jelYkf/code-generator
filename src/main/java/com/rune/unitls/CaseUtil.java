package com.rune.unitls;


/**
 * @author sedate
 * @date 2023/7/18 10:40
 * @description
 */
public class CaseUtil {

    // 转换为大驼峰（PascalCase）
    public static String toPascalCase(String input) {
        if (input == null || input.trim().length() == 0) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpper = true;

        for (char c : input.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else if (nextUpper) {
                result.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    // 转换为小驼峰（camelCase）
    public static String toCamelCase(String input) {
        if (input == null || input.trim().length() == 0) {
            return input;
        }
        String pascalCase = toPascalCase(input);
        return Character.toLowerCase(pascalCase.charAt(0)) + pascalCase.substring(1);
    }

    /**
     * 数据类型转化JAVA
     *
     * @param sqlType：类型名称
     * @return
     */
    public static String toSqlToJava(String sqlType) {
        if (sqlType == null || sqlType.trim().length() == 0) {
            return sqlType;
        }
        sqlType = sqlType.toLowerCase();
        switch (sqlType) {
            case "nvarchar":
                return "String";
            case "char":
                return "String";
            case "varchar":
                return "String";
            case "text":
                return "String";
            case "nchar":
                return "String";
            case "blob":
                return "byte[]";
            case "int":
                return "Integer";
            case "integer":
                return "Integer";
            case "tinyint":
                return "Integer";
            case "smallint":
                return "Integer";
            case "mediumint":
                return "Integer";
            case "bit":
                return "Boolean";
            case "bigint":
                return "Long";
            case "float":
                return "Fload";
            case "double":
                return "Double";
            case "decimal":
                return "java.math.BigDecimal";
            case "boolean":
                return "Boolean";
            case "date":
                return "LocalDateTime";
            case "datetime":
                return "LocalDateTime";
            case "year":
                return "LocalDateTime";
            case "time":
                return "java.sql.Time";
            case "timestamp":
                return "java.sql.Timestamp";
            case "numeric":
                return "BigDecimal";
            case "real":
                return "BigDecimal";
            case "money":
                return "Double";
            case "smallmoney":
                return "Double";
            case "image":
                return "byte[]";
            default:
                System.out.println("-----------------》转化失败：未发现的类型" + sqlType);
                break;
        }
        return sqlType;
    }

    /**
     * 数据类型转化Js
     *
     * @param sqlType：类型名称
     * @return
     */
    public static String toSqlToJs(String sqlType) {
        if (sqlType == null || sqlType.trim().length() == 0) {
            return sqlType;
        }
        sqlType = sqlType.toLowerCase();
        switch (sqlType) {
            case "nvarchar":
                return "string";
            case "char":
                return "string";
            case "varchar":
                return "string";
            case "text":
                return "string";
            case "nchar":
                return "string";
            case "blob":
                return "byte[]";
            case "int":
                return "number";
            case "integer":
                return "number";
            case "tinyint":
                return "boolean";
            case "smallint":
                return "number";
            case "mediumint":
                return "number";
            case "bit":
                return "boolean";
            case "bigint":
                return "number";
            case "float":
                return "Fload";
            case "double":
                return "Double";
            case "decimal":
                return "java.math.BigDecimal";
            case "boolean":
                return "Boolean";
            case "date":
                return "string";
            case "datetime":
                return "string";
            case "year":
                return "string";
            case "time":
                return "string";
            case "timestamp":
                return "string";
            case "numeric":
                return "java.math.BigDecimal";
            case "real":
                return "java.math.BigDecimal";
            case "money":
                return "Double";
            case "smallmoney":
                return "Double";
            case "image":
                return "byte[]";
            default:
                System.out.println("-----------------》转化失败：未发现的类型" + sqlType);
                break;
        }
        return sqlType;
    }

}
