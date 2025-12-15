package com.example.gitbook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.gitbook.dto.ApiResponseDTO;
import com.example.gitbook.entity.ArticleCategory;
import com.example.gitbook.entity.ArticleContent;
import com.example.gitbook.mapper.ArticleCategoryMapper;
import com.example.gitbook.mapper.ArticleContentMapper;
import com.example.gitbook.service.ArticleCategoryService;
import com.example.gitbook.service.ArticleContentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章内容服务实现类
 */
@Service
public class ArticleContentServiceImpl implements ArticleContentService {

    @Resource
    private ArticleContentMapper articleContentMapper;
    
    @Resource
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public ArticleContent getContentByCategoryId(Long categoryId) {
        LambdaQueryWrapper<ArticleContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleContent::getCategoryId, categoryId)
               .orderByDesc(ArticleContent::getUpdateTime)
               .last("LIMIT 1");
        return articleContentMapper.selectOne(wrapper);
    }

    @Override
    public List<ArticleContent> getAllContentsByCategoryId(Long categoryId) {
        LambdaQueryWrapper<ArticleContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleContent::getCategoryId, categoryId);
        return articleContentMapper.selectList(wrapper);
    }

    @Override
    public ArticleContent getContentById(Long id) {
        return articleContentMapper.selectById(id);
    }

    @Override
    public boolean saveOrUpdateContent(ArticleContent content) {
        System.out.println("ArticleContentServiceImpl.saveOrUpdateContent - Received content with id: " + content.getId());
        Date now = new Date();
        
        if (content.getId() == null) {
            // 新增操作
            content.setCreateTime(now);
            content.setUpdateTime(now);
            boolean result = articleContentMapper.insert(content) > 0;
            System.out.println("ArticleContentServiceImpl.saveOrUpdateContent - Save result: " + result + ", Generated id: " + content.getId());
            return result;
        } else {
            // 更新操作
            content.setUpdateTime(now);
            boolean result = articleContentMapper.updateById(content) > 0;
            System.out.println("ArticleContentServiceImpl.saveOrUpdateContent - Update result: " + result);
            return result;
        }
    }

    @Override
    public boolean deleteContent(Long id) {
        return articleContentMapper.deleteById(id) > 0;
    }

    @Override
    public ApiResponseDTO<Boolean> syncArticles(Long categoryId, String filePath) {
        System.out.println("ArticleContentServiceImpl.syncArticles - categoryId: " + categoryId + ", filePath: " + filePath);
        
        try {
            // 遍历指定路径下的所有Markdown文件（包括子目录）
            File dir = new File(filePath);
            if (!dir.exists() || !dir.isDirectory()) {
                String message = "目录不存在: " + filePath;
                System.out.println("ArticleContentServiceImpl.syncArticles - " + message);
                return ApiResponseDTO.fail(message);
            }
            
            // 递归遍历目录
            int[] count = new int[1]; // 使用数组来传递修改后的值
            processDirectory(dir, filePath, categoryId, count);
            
            if (count[0] == 0) {
                String message = "未找到Markdown文件: " + filePath;
                System.out.println("ArticleContentServiceImpl.syncArticles - " + message);
                return ApiResponseDTO.fail(message);
            }
            
            System.out.println("ArticleContentServiceImpl.syncArticles - Sync completed successfully. Processed " + count[0] + " files.");
            return ApiResponseDTO.success(true);
        } catch (Exception e) {
            String message = "同步失败: " + e.getMessage();
            System.err.println("ArticleContentServiceImpl.syncArticles - Error: " + message);
            e.printStackTrace();
            return ApiResponseDTO.fail(message);
        }
    }
    
    /**
     * 递归处理目录及其子目录
     */
    private void processDirectory(File directory, String rootPath, Long parentCategoryId, int[] count) throws IOException {
        System.out.println("ArticleContentServiceImpl.processDirectory - Processing directory: " + directory.getAbsolutePath());
        System.out.println("ArticleContentServiceImpl.processDirectory - parentCategoryId: " + parentCategoryId);
        
        // 处理当前目录下的所有文件
        File[] files = directory.listFiles((d, name) -> {
            boolean isMd = name.toLowerCase().endsWith(".md");
            if (isMd) {
                System.out.println("ArticleContentServiceImpl.processDirectory - Found Markdown file: " + name + " in " + directory.getName());
            }
            return isMd;
        });
        if (files != null && files.length > 0) {
            System.out.println("ArticleContentServiceImpl.processDirectory - Found " + files.length + " Markdown files in directory: " + directory.getAbsolutePath());
            for (File file : files) {
                System.out.println("ArticleContentServiceImpl.processDirectory - Processing file: " + file.getName() + " with categoryId: " + parentCategoryId);
                processMarkdownFile(file, rootPath, parentCategoryId);
                count[0]++;
            }
        } else {
            System.out.println("ArticleContentServiceImpl.processDirectory - No Markdown files found in directory: " + directory.getAbsolutePath());
        }
        
        // 处理当前目录下的所有子目录
        File[] subDirs = directory.listFiles(File::isDirectory);
        if (subDirs != null && subDirs.length > 0) {
            System.out.println("ArticleContentServiceImpl.processDirectory - Found " + subDirs.length + " subdirectories in: " + directory.getAbsolutePath());
            for (File subDir : subDirs) {
                System.out.println("ArticleContentServiceImpl.processDirectory - Processing subdirectory: " + subDir.getName());
                // 为子目录创建对应的分类节点
                String dirName = subDir.getName();
                System.out.println("ArticleContentServiceImpl.processDirectory - Creating category for dir: " + dirName + " with parentId: " + parentCategoryId);
                
                // 检查是否已存在同名分类
                LambdaQueryWrapper<ArticleCategory> categoryWrapper = new LambdaQueryWrapper<>();
                categoryWrapper.eq(ArticleCategory::getLabel, dirName)
                               .eq(ArticleCategory::getParentId, parentCategoryId);
                System.out.println("ArticleContentServiceImpl.processDirectory - Querying category: label=" + dirName + ", parentId=" + parentCategoryId);
                ArticleCategory existingCategory = articleCategoryMapper.selectOne(categoryWrapper);
                System.out.println("ArticleContentServiceImpl.processDirectory - Existing category: " + (existingCategory != null ? existingCategory.getId() : "null"));
                
                Long subCategoryId;
                if (existingCategory != null) {
                    // 使用现有分类
                    subCategoryId = existingCategory.getId();
                    System.out.println("ArticleContentServiceImpl.processDirectory - Using existing category: " + dirName + " (id: " + subCategoryId + ")");
                } else {
                    // 创建新分类
                    ArticleCategory newCategory = new ArticleCategory();
                    newCategory.setLabel(dirName);
                    newCategory.setParentId(parentCategoryId);
                    newCategory.setSort(0); // 默认排序
                    
                    // 根据父分类ID计算level
                    if (parentCategoryId != null) {
                        if (parentCategoryId == 0L) {
                            // 顶级分类
                            newCategory.setLevel(1);
                        } else {
                            // 非顶级分类，获取父分类的level
                            System.out.println("ArticleContentServiceImpl.processDirectory - Getting parent category for id: " + parentCategoryId);
                            ArticleCategory parentCategory = articleCategoryMapper.selectById(parentCategoryId);
                            if (parentCategory != null) {
                                System.out.println("ArticleContentServiceImpl.processDirectory - Parent category level: " + parentCategory.getLevel());
                                // 子分类的level为父分类level+1
                                newCategory.setLevel(parentCategory.getLevel() + 1);
                            } else {
                                // 父分类不存在，默认设为顶级分类
                                System.out.println("ArticleContentServiceImpl.processDirectory - Parent category not found, setting level to 1");
                                newCategory.setLevel(1);
                            }
                        }
                    } else {
                        // 默认为顶级分类
                        newCategory.setLevel(1);
                    }
                    
                    // 设置创建时间和更新时间
                    Date now = new Date();
                    newCategory.setCreateTime(now);
                    newCategory.setUpdateTime(now);
                    
                    System.out.println("ArticleContentServiceImpl.processDirectory - Inserting new category: " + dirName + ", parentId: " + parentCategoryId + ", level: " + newCategory.getLevel());
                    int insertResult = articleCategoryMapper.insert(newCategory);
                    System.out.println("ArticleContentServiceImpl.processDirectory - Insert result: " + insertResult);
                    if (insertResult > 0) {
                        subCategoryId = newCategory.getId();
                        System.out.println("ArticleContentServiceImpl.processDirectory - Created new category: " + dirName + " (id: " + subCategoryId + ")");
                    } else {
                        System.err.println("ArticleContentServiceImpl.processDirectory - Failed to create category: " + dirName);
                        subCategoryId = parentCategoryId; // 创建失败，使用父分类ID
                    }
                }
                
                // 递归处理子目录
                System.out.println("ArticleContentServiceImpl.processDirectory - Recursively processing subdir: " + subDir.getName() + " with categoryId: " + subCategoryId);
                processDirectory(subDir, rootPath, subCategoryId, count);
            }
        } else {
            System.out.println("ArticleContentServiceImpl.processDirectory - No subdirectories found in: " + directory.getAbsolutePath());
        }
    }
    
    /**
     * 处理单个Markdown文件
     */
    private void processMarkdownFile(File file, String rootPath, Long categoryId) throws IOException {
        System.out.println("ArticleContentServiceImpl.processMarkdownFile - Processing file: " + file.getAbsolutePath());
        
        // 获取文件修改时间
        Date fileModifiedTime = new Date(file.lastModified());
        System.out.println("ArticleContentServiceImpl.processMarkdownFile - File modified time: " + fileModifiedTime);
        
        // 读取文件内容
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        String content = contentBuilder.toString();
        
        // 提取标题
        String title = extractTitle(content);
        if (title == null || title.isEmpty()) {
            title = file.getName().replace(".md", "");
        }
        
        // 为markdown文件创建分类节点
        System.out.println("ArticleContentServiceImpl.processMarkdownFile - Creating category for file: " + title + " with parentId: " + categoryId);
        
        // 检查是否已存在同名分类
        LambdaQueryWrapper<ArticleCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(ArticleCategory::getLabel, title)
                       .eq(ArticleCategory::getParentId, categoryId);
        ArticleCategory existingCategory = articleCategoryMapper.selectOne(categoryWrapper);
        
        Long articleCategoryId;
        if (existingCategory != null) {
            // 使用现有分类
            articleCategoryId = existingCategory.getId();
            System.out.println("ArticleContentServiceImpl.processMarkdownFile - Using existing category: " + title + " (id: " + articleCategoryId + ")");
        } else {
            // 创建新分类
            ArticleCategory newCategory = new ArticleCategory();
            newCategory.setLabel(title);
            newCategory.setParentId(categoryId);
            newCategory.setSort(0); // 默认排序
            
            // 根据父分类ID计算level
            if (categoryId != null) {
                if (categoryId == 0L) {
                    // 顶级分类
                    newCategory.setLevel(1);
                } else {
                    // 非顶级分类，获取父分类的level
                    ArticleCategory parentCategory = articleCategoryMapper.selectById(categoryId);
                    if (parentCategory != null) {
                        // 子分类的level为父分类level+1
                        newCategory.setLevel(parentCategory.getLevel() + 1);
                    } else {
                        // 父分类不存在，默认设为顶级分类
                        newCategory.setLevel(1);
                    }
                }
            } else {
                // 默认为顶级分类
                newCategory.setLevel(1);
            }
            
            // 设置创建时间和更新时间
            Date now = new Date();
            newCategory.setCreateTime(now);
            newCategory.setUpdateTime(now);
            
            System.out.println("ArticleContentServiceImpl.processMarkdownFile - Inserting new category: " + title + ", parentId: " + categoryId + ", level: " + newCategory.getLevel());
            int insertResult = articleCategoryMapper.insert(newCategory);
            if (insertResult > 0) {
                articleCategoryId = newCategory.getId();
                System.out.println("ArticleContentServiceImpl.processMarkdownFile - Created new category: " + title + " (id: " + articleCategoryId + ")");
            } else {
                System.err.println("ArticleContentServiceImpl.processMarkdownFile - Failed to create category: " + title);
                return; // 创建失败，跳过此文件
            }
        }
        
        // 检查是否已存在相同标题的文章
        LambdaQueryWrapper<ArticleContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleContent::getTitle, title)
               .eq(ArticleContent::getCategoryId, articleCategoryId);
        ArticleContent existingContent = articleContentMapper.selectOne(wrapper);
        
        // 如果文章已存在，检查修改时间是否相同
        if (existingContent != null) {
            // 获取数据库中文章的修改时间
            Date dbModifiedTime = existingContent.getUpdateTime();
            System.out.println("ArticleContentServiceImpl.processMarkdownFile - Database article modified time: " + dbModifiedTime);
            
            // 比较文件修改时间和数据库修改时间
            if (dbModifiedTime != null && fileModifiedTime.compareTo(dbModifiedTime) <= 0) {
                System.out.println("ArticleContentServiceImpl.processMarkdownFile - Skipping article: " + title + " (no changes)");
                return; // 文件未修改，跳过更新
            }
        }
        
        Date now = new Date();
        ArticleContent articleContent = new ArticleContent();
        articleContent.setTitle(title);
        articleContent.setContent(content);
        articleContent.setCategoryId(articleCategoryId); // 使用文章分类ID而不是父分类ID
        articleContent.setUpdateTime(fileModifiedTime); // 使用文件修改时间作为文章更新时间
        
        // 同时更新分类节点的修改时间
        LambdaQueryWrapper<ArticleCategory> updateCategoryWrapper = new LambdaQueryWrapper<>();
        updateCategoryWrapper.eq(ArticleCategory::getId, articleCategoryId);
        ArticleCategory updateCategory = new ArticleCategory();
        updateCategory.setUpdateTime(fileModifiedTime);
        articleCategoryMapper.update(updateCategory, updateCategoryWrapper);
        
        if (existingContent != null) {
            // 更新现有文章
            articleContent.setId(existingContent.getId());
            articleContent.setCreateTime(existingContent.getCreateTime());
            articleContentMapper.updateById(articleContent);
            System.out.println("ArticleContentServiceImpl.processMarkdownFile - Updated article: " + title);
        } else {
            // 创建新文章
            articleContent.setCreateTime(now);
            articleContentMapper.insert(articleContent);
            System.out.println("ArticleContentServiceImpl.processMarkdownFile - Created new article: " + title);
        }
    }
    
    /**
     * 从Markdown内容中提取标题
     */
    private String extractTitle(String content) {
        // 匹配第一个#标题
        Pattern pattern = Pattern.compile("^#\\s+(.+)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // 如果没有#标题，尝试匹配文件名
        return null;
    }
}
