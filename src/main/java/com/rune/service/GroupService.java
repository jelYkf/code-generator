package com.rune.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rune.domain.dto.TemplateGroupDto;
import com.rune.domain.entity.TemplateAttribute;
import com.rune.domain.entity.TemplateFile;
import com.rune.domain.entity.TemplateGroup;
import com.rune.enums.FileTypeEnum;
import com.rune.mapper.AttributeMapper;
import com.rune.mapper.FileMapper;
import com.rune.mapper.GroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sedate
 * @date 2023/7/17 14:59
 * @description
 */
@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupMapper mapper;

    private final AttributeMapper attributeMapper;

    private final FileMapper fileMapper;

    public List<TemplateGroup> query(Integer id) {
        return mapper.selectList(null);
    }

    public Page<TemplateGroup> page(TemplateGroupDto groupDto) {
        QueryWrapper<TemplateGroup> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(groupDto.getName())) {
            wrapper.like("name", groupDto.getName());
        }
        Page<TemplateGroup> page = new Page<>(groupDto.getCurrent(), groupDto.getPageSize());
        return mapper.selectPage(page, wrapper);
    }


    public void create(TemplateGroup group) {
        mapper.insert(group);
        TemplateAttribute templateAttribute = new TemplateAttribute();
        templateAttribute.setGroupId(group.getId());
        templateAttribute.setName(FileService.tablePrefix);
        templateAttribute.setValue("");
        templateAttribute.setRemark("表前缀截取");
        attributeMapper.insert(templateAttribute);
        TemplateFile templateFile = new TemplateFile();
        templateFile.setPid(0);
        templateFile.setType(FileTypeEnum.FOLDER.getType());
        templateFile.setFileContent(null);
        templateFile.setGroupId(group.getId());
        templateFile.setTitle(group.getRoot());
        fileMapper.insert(templateFile);
    }

    public void update(TemplateGroup group) {
        mapper.updateById(group);
    }

    public void delete(Integer id) {
        mapper.deleteById(id);
    }
}
