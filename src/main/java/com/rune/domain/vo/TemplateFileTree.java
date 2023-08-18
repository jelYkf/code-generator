package com.rune.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * @author sedate
 * @date 2023/7/20 10:56
 * @description
 */
@Getter
@Setter
public class TemplateFileTree {

    private Integer id;

    private Integer pid;

    private Integer key;

    private String title;

    private Boolean isLeaf = false;

    private String file;

    private Integer type;

    private List<TemplateFileTree> children;

}
