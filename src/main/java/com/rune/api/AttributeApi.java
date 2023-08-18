package com.rune.api;

import com.rune.base.ApiOk;
import com.rune.base.ApiResponse;
import com.rune.domain.dto.TemplateAttributeDto;
import com.rune.domain.entity.TemplateAttribute;
import com.rune.service.AttributeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author sedate
 * @date 2023/7/17 14:58
 * @description
 */
@RestController
@RequestMapping("attribute")
public class AttributeApi {

    @Resource
    private AttributeService attributeService;

    @GetMapping
    public ResponseEntity<ApiOk> query(Integer groupId) {
        return ApiResponse.ok(attributeService.query(groupId));
    }

    @PostMapping
    public void create(@RequestBody TemplateAttribute attribute) {
        attributeService.create(attribute);
    }

    @PutMapping
    public void update(@RequestBody TemplateAttribute attribute) {
        attributeService.update(attribute);
    }

    @PutMapping("/updateByGroup")
    public ResponseEntity<ApiOk> updateByGroup(@RequestBody Set<TemplateAttributeDto> templateAttributeDtos) {
        attributeService.updateByGroup(templateAttributeDtos);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        attributeService.delete(id);
    }


}
