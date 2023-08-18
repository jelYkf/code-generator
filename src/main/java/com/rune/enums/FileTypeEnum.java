package com.rune.enums;

import lombok.Getter;

/**
 * @author one
 */

@Getter
public enum FileTypeEnum {
    /*
        文件夹
     */
    FOLDER(1),

    /*
        文件
     */
    FILE(2);

    private final int type;

    FileTypeEnum(int type) {
        this.type = type;
    }
}
