package com.rune.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rune.domain.dto.TemplateAttributeDto;
import com.rune.domain.entity.TemplateAttribute;
import com.rune.mapper.AttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author sedate
 * @date 2023/7/17 14:59
 * @description
 */
@Service
@RequiredArgsConstructor
public class AttributeService {

    private final AttributeMapper mapper;

    public void create(TemplateAttribute attribute) {
        mapper.insert(attribute);
    }

    public List<TemplateAttribute> query(Integer groupId) {
        QueryWrapper<TemplateAttribute> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        return mapper.selectList(queryWrapper);
    }

    public void update(TemplateAttribute attribute) {
        mapper.updateById(attribute);
    }

    public void delete(Integer id) {
        mapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateByGroup(Set<TemplateAttributeDto> templateAttributeDtos) {
        for (TemplateAttributeDto templateAttributeDto : templateAttributeDtos) {
            Integer groupId = templateAttributeDto.getGroupId();
            String name = templateAttributeDto.getName();
            QueryWrapper<TemplateAttribute> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("group_id", groupId);
            queryWrapper.eq("name", name);
            TemplateAttribute templateAttribute = new TemplateAttribute();
            templateAttribute.setValue(templateAttributeDto.getValue());
            mapper.update(templateAttribute, queryWrapper);
        }
    }
}
