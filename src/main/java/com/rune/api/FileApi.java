package com.rune.api;

import com.rune.base.ApiOk;
import com.rune.base.ApiResponse;
import com.rune.domain.entity.TemplateFile;
import com.rune.domain.vo.TemplateFileTree;
import com.rune.domain.vo.TemplateFileView;
import com.rune.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @author sedate
 * @date 2023/7/17 14:58
 * @description
 */
@RestController
@RequestMapping("file")
public class FileApi {

    @Resource
    private FileService fileService;

    @GetMapping
    public ResponseEntity<ApiOk> select(Integer groupId) {
        List<TemplateFileView> select = fileService.select(groupId);
        return ApiResponse.ok(select);
    }

    @GetMapping("tree")
    public ResponseEntity<ApiOk> tree(Integer groupId) {
        List<TemplateFileTree> tree = fileService.tree(groupId);
        return ApiResponse.ok(tree);
    }

    @PostMapping
    public ResponseEntity<ApiOk> create(@RequestBody TemplateFile templateFile) throws IOException {
        fileService.create(templateFile);
        return ApiResponse.ok();
    }

    @PutMapping
    public ResponseEntity<ApiOk> update(String file, @ModelAttribute TemplateFile templateFile) throws IOException {
        fileService.update(file, templateFile);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiOk> delete(@PathVariable Integer id) throws IOException {
        fileService.delete(id);
        return ApiResponse.ok();
    }

    @GetMapping("/preview")
    public ResponseEntity<ApiOk> preview(Integer databaseId, Integer groupId, @RequestParam("tableNames") List<String> tableNames) throws Exception {
        ArrayList<TemplateFileTree> preview = fileService.preview(databaseId, groupId, tableNames);
        return ApiResponse.ok(preview);
    }

    @GetMapping("/download")
    public void download(Integer databaseId, Integer groupId, @RequestParam("tableNames") List<String> tableNames, HttpServletResponse response) throws Exception {
        byte[] select = fileService.download(databaseId, groupId, tableNames);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"code.zip\"");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.getOutputStream().write(select);
    }

    @PostMapping("/importFile")
    public ResponseEntity<ApiOk> importFile(Integer groupId, @ModelAttribute MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        fileService.importFile(groupId, zipInputStream);
        return ApiResponse.ok();
    }

    @GetMapping("/export")
    public ResponseEntity<ApiOk> export(Integer groupId, HttpServletResponse response) throws Exception {
        byte[] export = fileService.export(groupId);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"code.zip\"");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.getOutputStream().write(export);
        return ApiResponse.ok();
    }

}
