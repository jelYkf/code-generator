package com.rune.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rune.base.ApiOk;
import com.rune.base.ApiResponse;
import com.rune.domain.dto.TemplateGroupDto;
import com.rune.domain.entity.TemplateGroup;
import com.rune.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author sedate
 * @date 2023/7/17 14:58
 * @description
 */
@RestController
@RequestMapping("group")
public class GroupApi {

    @Resource
    private GroupService groupService;

    @GetMapping
    public ResponseEntity<ApiOk> query(TemplateGroupDto groupDto) {
        Page<TemplateGroup> page = groupService.page(groupDto);
        return ApiResponse.page(page);
    }

//    @GetMapping("/page")
//    public ResponseEntity<ApiOk> page(TemplateGroupDto groupDto) {
//        List<TemplateGroup> query = groupService.query(null);
//        return ApiResponse.ok(query);
//    }

    @PostMapping
    public ResponseEntity<ApiOk> create(@RequestBody TemplateGroup group) {
        groupService.create(group);
        return ApiResponse.ok();
    }

    @PutMapping
    public ResponseEntity<ApiOk> update(@RequestBody TemplateGroup group) {
        groupService.update(group);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiOk> delete(@PathVariable Integer id) {
        groupService.delete(id);
        return ApiResponse.ok();
    }


}
