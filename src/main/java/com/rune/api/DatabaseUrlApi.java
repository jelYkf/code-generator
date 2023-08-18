package com.rune.api;

import com.rune.base.ApiOk;
import com.rune.base.ApiResponse;
import com.rune.domain.dto.DatabaseUrlDto;
import com.rune.domain.dto.DatabaseUrlRequest;
import com.rune.service.DatabaseUrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author sedate
 * @date 2023/7/17 14:58
 * @description
 */
@RestController
@RequestMapping("database")
public class DatabaseUrlApi {

    @Resource
    private DatabaseUrlService databaseUrlService;

    @GetMapping
    public ResponseEntity<ApiOk> query(DatabaseUrlDto databaseUrlDto) {
        return ApiResponse.page(databaseUrlService.query(databaseUrlDto));
    }

    @PostMapping
    public void create(@RequestBody DatabaseUrlRequest request) {
        databaseUrlService.create(request);
    }

    @PutMapping
    public void update(@RequestBody DatabaseUrlRequest attribute) {
        databaseUrlService.update(attribute);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        databaseUrlService.delete(id);
    }


}
