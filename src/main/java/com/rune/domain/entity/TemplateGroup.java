package com.rune.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sedate
 * @date 2023/7/17 14:50
 * @description
 */
@TableName("template_group")
@Getter
@Setter
public class TemplateGroup {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String remark;

    private String root;

}
