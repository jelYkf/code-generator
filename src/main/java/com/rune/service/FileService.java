package com.rune.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rune.database.DynamicDataSourceContextHolder;
import com.rune.domain.bo.ColumnInfo;
import com.rune.domain.bo.TableInfo;
import com.rune.domain.entity.TemplateAttribute;
import com.rune.domain.entity.TemplateDatabaseUrl;
import com.rune.domain.entity.TemplateFile;
import com.rune.domain.vo.TemplateFileTree;
import com.rune.domain.vo.TemplateFileView;
import com.rune.enums.FileTypeEnum;
import com.rune.mapper.DatabaseUrlMapper;
import com.rune.mapper.FileMapper;
import com.rune.unitls.CaseUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author sedate
 * @date 2023/7/17 14:59
 * @description
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    public static final String tablePrefix = "tablePrefix";

    private final FileMapper mapper;

    private final TableService tableService;

    private final DatabaseUrlMapper databaseUrlMapper;

    private final AttributeService attributeService;

    private final Configuration configuration;

    public List<TemplateFileView> select(Integer groupId) {
        QueryWrapper<TemplateFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        // 查询模板
        List<TemplateFile> templateFiles = mapper.selectList(queryWrapper);
        return templateFiles.stream().map(templateFile -> {
            TemplateFileView templateFileView = new TemplateFileView();
            BeanUtils.copyProperties(templateFile, templateFileView);
            byte[] fileContent = templateFile.getFileContent();
            templateFileView.setFile(fileContent == null ? null : new String(fileContent));
            return templateFileView;
        }).collect(Collectors.toList());
    }

    public List<TemplateFileTree> tree(Integer groupId) {
        QueryWrapper<TemplateFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        // 查询模板
        List<TemplateFile> templateFiles = mapper.selectList(queryWrapper);
        List<TemplateFileTree> templateFileTrees = templateFiles.stream().map(templateFile -> {
            TemplateFileTree templateFileTree = new TemplateFileTree();
            BeanUtils.copyProperties(templateFile, templateFileTree);
            byte[] fileContent = templateFile.getFileContent();
            templateFileTree.setFile(fileContent == null ? null : new String(fileContent));
            templateFileTree.setKey(templateFile.getId());
            templateFileTree.setTitle(templateFile.getTitle());
            return templateFileTree;
        }).collect(Collectors.toList());
        ArrayList<TemplateFileTree> fileTrees = new ArrayList<>();
        for (TemplateFileTree templateFileTree : templateFileTrees) {
            if (templateFileTree.getPid() == 0) {
                fileTrees.add(templateFileTree);
            }
        }
        for (TemplateFileTree fileTree : fileTrees) {
            makeTree(fileTree, templateFileTrees);
        }
        return fileTrees;
    }

    public void create(TemplateFile templateFile) throws IOException {
        mapper.insert(templateFile);
    }

    public void update(String file, TemplateFile templateFile) throws IOException {
        if (file != null) {
            templateFile.setFileContent(file.getBytes());
        }
        templateFile.setType(null);
        mapper.updateById(templateFile);
    }

    public void delete(Integer id) {
        QueryWrapper<TemplateFile> queryWrapper = new QueryWrapper<TemplateFile>().eq("pid", id);
        List<TemplateFile> templateFiles = mapper.selectList(queryWrapper);
        if (templateFiles != null && templateFiles.size() != 0) {
            deleteLoop(templateFiles);
        }
        mapper.deleteById(id);
    }

    private void deleteLoop(List<TemplateFile> templateFiles) {
        for (TemplateFile templateFile : templateFiles) {
            Integer id = templateFile.getId();
            QueryWrapper<TemplateFile> queryWrapper = new QueryWrapper<TemplateFile>().eq("pid", id);
            List<TemplateFile> templateFiless = mapper.selectList(queryWrapper);
            if (templateFiless != null && templateFiless.size() != 0) {
                deleteLoop(templateFiless);
            }
            mapper.deleteById(id);
        }
    }

    public ArrayList<TemplateFileTree> preview(Integer databaseId, Integer groupId, List<String> tableNames) throws Exception {
        TemplateDatabaseUrl templateDatabaseUrl = databaseUrlMapper.selectById(databaseId);
        Map<String, Object> dataModel = new HashMap<>();
        List<TemplateFileView> generateFile = getGenerateFile(templateDatabaseUrl, groupId, tableNames, dataModel);
        DynamicDataSourceContextHolder.clearDataSourceKey();
        List<TemplateFileTree> templateFileTrees = generateFile.stream().map(templateFileView -> {
            TemplateFileTree templateFileTree = new TemplateFileTree();
            BeanUtils.copyProperties(templateFileView, templateFileTree);
            templateFileTree.setKey(templateFileView.getId());
            templateFileTree.setTitle(templateFileView.getTitle());
            return templateFileTree;
        }).collect(Collectors.toList());
        ArrayList<TemplateFileTree> fileTrees = new ArrayList<>();
        for (TemplateFileTree templateFileTree : templateFileTrees) {
            if (templateFileTree.getPid() == 0) {
                fileTrees.add(templateFileTree);
            }
        }
        for (TemplateFileTree fileTree : fileTrees) {
            makeTree(fileTree, templateFileTrees);
        }
        return fileTrees;
    }

    public byte[] download(Integer databaseId, Integer groupId, List<String> tableNames) throws Exception {
        Map<String, Object> dataModel = new HashMap<>();
        TemplateDatabaseUrl templateDatabaseUrl = databaseUrlMapper.selectById(databaseId);
        List<TemplateFileView> generateFiles = getGenerateFile(templateDatabaseUrl, groupId, tableNames, dataModel);
        DynamicDataSourceContextHolder.clearDataSourceKey();
        // 添加文件到内存zip
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(zipOutputStream)) {
            for (TemplateFileView templateFile : generateFiles) {
                String joinPath = joinPath("", templateFile, generateFiles, dataModel);
                joinPath = joinPath.endsWith("/") ? joinPath.substring(0, joinPath.length() - 1) : joinPath;
                if (templateFile.getType().equals(FileTypeEnum.FOLDER.getType())) {
                    joinPath = joinPath + "/";
                }
                ZipEntry zipEntry = new ZipEntry(joinPath);
                zip.putNextEntry(zipEntry);
                if (templateFile.getType().equals(FileTypeEnum.FILE.getType())) {
                    zip.write(templateFile.getFile().getBytes(StandardCharsets.UTF_8));
                }
                zip.closeEntry();
            }
            zip.finish();
        }
        return zipOutputStream.toByteArray();
    }

    private List<TemplateFileView> getGenerateFile(TemplateDatabaseUrl templateDatabaseUrl, Integer groupId, List<String> tableNames, Map<String, Object> dataModel) throws Exception {
        // 查询模板
        QueryWrapper<TemplateFile> queryWrapper = new QueryWrapper<TemplateFile>().eq("group_id", groupId);
        List<TemplateFile> templateFiles = mapper.selectList(queryWrapper);
        List<TemplateFileView> collect = templateFiles.stream().map(templateFile -> {
            TemplateFileView templateFileView = new TemplateFileView();
            BeanUtils.copyProperties(templateFile, templateFileView);
            byte[] fileContent = templateFile.getFileContent();
            templateFileView.setFile(new String(fileContent == null ? new byte[1] : fileContent));
            return templateFileView;
        }).collect(Collectors.toList());

        ArrayList<TemplateFileView> templateFileViews = new ArrayList<>();

        // 查询需要生成的表结构
        DynamicDataSourceContextHolder.setDataSourceKey(templateDatabaseUrl.getName());
        Map<String, TableInfo> tableInfo = tableService.queryTableInfo(tableNames);
        Map<String, List<ColumnInfo>> columnInfo = tableService.queryTableColumnInfo(tableNames);
        DynamicDataSourceContextHolder.clearDataSourceKey();

        // 获取通用注释
        commonComment(groupId, dataModel);

        HashMap<String, TemplateFileView> hashMap = new HashMap<>();


        int asInt = collect.stream().mapToInt(TemplateFileView::getId).max().getAsInt();
        int virtualId = asInt + 1;
        for (int i = 0; i < tableNames.size(); i++) {
            String tableName = tableNames.get(i);
            //获取当前表注释和列注释
            tableColumnComment(tableName, tableInfo, columnInfo, dataModel);
            for (TemplateFileView templateFileView : collect) {
                Integer type = templateFileView.getType();
                String title = templateFileView.getTitle();
                TemplateFileView templateFileViewBack = ObjectUtil.clone(templateFileView);
                if (type == FileTypeEnum.FILE.getType()) {
                    if (i != 0) {
                        templateFileViewBack.setId(virtualId);
                        virtualId++;
                    }
                    Template template = new Template("code", new StringReader(templateFileView.getFile()), configuration);
                    StringWriter writer = new StringWriter();
                    try {
                        template.process(dataModel, writer);
                    } catch (Exception e) {
                        log.warn(" {} 文件渲染模板出错", templateFileView.getTitle());
                        throw new RuntimeException(e);
                    }
                    if (i == 0) {
                        templateFileViewBack.setFile(writer.toString());
                    } else {
                        templateFileViewBack.setFile(writer.toString());
                    }
                }
                // 替换名称
                if (title.contains("{") && title.contains("}")) {
                    String join = title.substring(title.indexOf("{") + 1, title.indexOf("}"));
                    String joinName = dataModel.get(join).toString();
                    title = title.replace("{" + join + "}", joinName);
                }
                templateFileViewBack.setTitle(title);
                Optional<TemplateFileView> first = collect.stream().filter(fileView -> fileView.getId().equals(templateFileViewBack.getPid())).findFirst();
                boolean present = first.isPresent();
                String titleBack = "";
                if (present) {
                    titleBack = first.get().getTitle();
                }
                hashMap.put(templateFileViewBack.getTitle() + titleBack, templateFileViewBack);
            }
        }

        for (Map.Entry<String, TemplateFileView> stringTemplateFileViewEntry : hashMap.entrySet()) {
            templateFileViews.add(stringTemplateFileViewEntry.getValue());
        }
        return templateFileViews;
    }

    private void commonComment(int groupId, Map<String, Object> dataModel) {
        List<TemplateAttribute> query = attributeService.query(groupId);
        for (TemplateAttribute templateAttribute : query) {
            String name = templateAttribute.getName();
            String value = templateAttribute.getValue();
            dataModel.put(name, value);
        }
        dataModel.put("currentTime", LocalDate.now());
    }

    private void tableColumnComment(String s, Map<String, TableInfo> tableInfos, Map<String, List<ColumnInfo>> columnInfos, Map<String, Object> dataModel) {
        String needReplace = dataModel.get(tablePrefix).toString();
        //表注释
        TableInfo info = tableInfos.get(s);
        dataModel.put("tableComment", info.getTableComment());
        dataModel.put("realTableName", info.getTableName());
        dataModel.put("upperTableName", info.getTableName().replace(needReplace, "").toUpperCase(Locale.ROOT));
        //小驼峰
        dataModel.put("tablename", CaseUtil.toCamelCase(info.getTableName().replace(needReplace, "")));
        //大驼峰
        dataModel.put("tableName", CaseUtil.toPascalCase(info.getTableName().replace(needReplace, "")));
        //将列名转换为小驼峰
        List<ColumnInfo> columnInfosc = columnInfos.get(s);
        for (ColumnInfo columnInfo : columnInfosc) {
            columnInfo.setColumnName(CaseUtil.toCamelCase(columnInfo.getColumnName()));
            columnInfo.setJavaDataType(CaseUtil.toSqlToJava(columnInfo.getDataType()));
            columnInfo.setJsDataType(CaseUtil.toSqlToJs(columnInfo.getDataType()));
        }
        dataModel.put("columns", columnInfosc);
    }


    private String joinPath(String path, TemplateFileView templateFile, List<TemplateFileView> collect, Map<String, Object> dataModel) {
        String title = templateFile.getTitle();
        Integer pid = templateFile.getPid();
        if (pid == 0) {
            if ("".equals(path)) {
                path = title;
            } else {
                path = title + "/" + path;
            }
            return path;
        } else {
            TemplateFileView fileView = collect.stream().filter(templateFileView -> templateFileView.getId().equals(pid)).collect(Collectors.toList()).get(0);
            path = title + "/" + path;
            return joinPath(path, fileView, collect, dataModel);
        }
    }

    private String joinPathExport(String path, TemplateFileView templateFile, List<TemplateFileView> collect) {
        String title = templateFile.getTitle();
        Integer pid = templateFile.getPid();
        if (pid == 0) {
            if ("".equals(path)) {
                path = title;
            } else {
                path = title + "/" + path;
            }
            return path;
        } else {
            TemplateFileView fileView = collect.stream().filter(templateFileView -> templateFileView.getId().equals(pid)).collect(Collectors.toList()).get(0);
            path = title + "/" + path;
            return joinPathExport(path, fileView, collect);
        }
    }

    private void makeTree(TemplateFileTree fileTree, List<TemplateFileTree> templateFileTrees) {
        ArrayList<TemplateFileTree> fileTrees = new ArrayList<>();
        for (TemplateFileTree templateFileTree : templateFileTrees) {
            if (templateFileTree.getPid().equals(fileTree.getId())) {
                fileTrees.add(templateFileTree);
            }
        }
        for (TemplateFileTree tree : fileTrees) {
            makeTree(tree, templateFileTrees);
        }
        if (fileTrees.size() == 0) {
            fileTree.setIsLeaf(true);
        }
        fileTree.setChildren(fileTrees);
    }

    public void importFile(Integer groupId, ZipInputStream zipInputStream) throws IOException {
        ZipEntry zipEntry;
        HashMap<Integer, HashMap<String, String>> levelParentMap = new HashMap<>();
        HashMap<Integer, ArrayList<TemplateFile>> levelMap = new HashMap<>();
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            TemplateFile templateFile = new TemplateFile();
            templateFile.setGroupId(groupId);
            String path = zipEntry.getName();
            byte[] buffer = new byte[1024];
            int bytesRead;
            boolean directory = zipEntry.isDirectory();
            if (path.contains("\\")) {
                path = path.replaceAll("\\\\", "/");
            }
            String[] split = path.split("/");
            String name = split[split.length - 1];
            if (split.length == 1) {
                templateFile.setPid(0);
                HashMap<String, String> levelMapOrDefault = levelParentMap.getOrDefault(split.length, new HashMap<>());
                levelMapOrDefault.put(name, name);
                levelParentMap.put(split.length, levelMapOrDefault);
            } else {
                HashMap<String, String> levelMapOrDefault = levelParentMap.getOrDefault(split.length, new HashMap<>());
                levelMapOrDefault.put(name, split[split.length - 2]);
                levelParentMap.put(split.length, levelMapOrDefault);
            }

            // 文件夹
            if (directory) {
                templateFile.setType(FileTypeEnum.FOLDER.getType());
                templateFile.setTitle(name);
                templateFile.setFileContent(null);
            }//文件
            else {
                templateFile.setType(FileTypeEnum.FOLDER.getType());
                templateFile.setTitle(name);
                ByteArrayOutputStream entryContent = new ByteArrayOutputStream();
                while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                    entryContent.write(buffer, 0, bytesRead);
                }
                byte[] byteArray = entryContent.toByteArray();
                templateFile.setFileContent(byteArray);
            }
            mapper.insert(templateFile);

            ArrayList<TemplateFile> orDefault = levelMap.getOrDefault(split.length, new ArrayList<>());
            orDefault.add(templateFile);
            levelMap.put(split.length, orDefault);
        }
        zipInputStream.closeEntry();
        Set<Integer> integers = levelMap.keySet();
        for (Integer integer : integers) {
            ArrayList<TemplateFile> templateFiles = levelMap.get(integer);
            if (integer == 1) {
                for (TemplateFile templateFile : templateFiles) {
                    templateFile.setPid(0);
                    mapper.updateById(templateFile);
                }
            } else {
                for (TemplateFile templateFile : templateFiles) {
                    // 获得父级别
                    Integer parentLevel = integer - 1;
                    HashMap<String, String> currentFolder = levelParentMap.get(integer);
                    String parentName = currentFolder.get(templateFile.getTitle());
                    ArrayList<TemplateFile> parents = levelMap.get(parentLevel);
                    TemplateFile parent = parents.stream().filter(file -> file.getTitle().equals(parentName)).findFirst().get();
                    templateFile.setPid(parent.getId());
                    mapper.updateById(templateFile);
                }
            }
        }
    }

    public byte[] export(Integer groupId) {
        QueryWrapper<TemplateFile> queryWrapper = new QueryWrapper<TemplateFile>().eq("group_id", groupId);
        List<TemplateFile> templateFiles = mapper.selectList(queryWrapper);
        List<TemplateFileView> templateFileViews = templateFiles.stream().map(templateFile -> {
            TemplateFileView templateFileView = new TemplateFileView();
            BeanUtils.copyProperties(templateFile, templateFileView);
            byte[] fileContent = templateFile.getFileContent();
            templateFileView.setFile(fileContent == null ? null : new String(fileContent));
            return templateFileView;
        }).collect(Collectors.toList());
        // 添加文件到内存zip
        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(zipOutputStream)) {
            for (TemplateFileView templateFile : templateFileViews) {
                String joinPath = joinPathExport("", templateFile, templateFileViews);
                joinPath = joinPath.endsWith("/") ? joinPath.substring(0, joinPath.length() - 1) : joinPath;
                if (templateFile.getType().equals(FileTypeEnum.FOLDER.getType())) {
                    joinPath = joinPath + "/";
                }
                ZipEntry zipEntry = new ZipEntry(joinPath);
                zip.putNextEntry(zipEntry);
                if (templateFile.getType().equals(FileTypeEnum.FILE.getType())) {
                    zip.write(templateFile.getFile().getBytes(StandardCharsets.UTF_8));
                }
                zip.closeEntry();
            }
            zip.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return zipOutputStream.toByteArray();
    }
}