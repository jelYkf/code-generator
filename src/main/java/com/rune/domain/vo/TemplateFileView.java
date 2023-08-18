package com.rune.domain.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sedate
 * @date 2023/7/17 15:27
 * @description
 */
@Getter
@Setter
public class TemplateFileView implements Cloneable {

    private Integer id;

    private Integer groupId;

    private Integer pid;

    private String title;

    private String file;

    private Integer type;

}
