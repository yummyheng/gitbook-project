package com.example.gitbook.service;

import com.example.gitbook.entity.ArticleCategory;
import java.util.List;
import java.util.Map;

/**
 * 文章分类服务接口
 */
public interface ArticleCategoryService {
    
    /**
     * 获取文章分类树结构
     * @return 文章分类树
     */
    List<ArticleCategory> getArticleTree();
    
    /**
     * 获取文章分类树结构（支持标签过滤）
     * @param tagId 标签ID
     * @return 文章分类树
     */
    List<ArticleCategory> getArticleTree(Integer tagId);
    
    /**
     * 添加文章分类
     * @param category 文章分类信息
     * @return 添加结果
     */
    boolean addCategory(ArticleCategory category);
    
    /**
     * 更新文章分类
     * @param category 文章分类信息
     * @return 更新结果
     */
    boolean updateCategory(ArticleCategory category);
    
    /**
     * 删除文章分类
     * @param id 文章分类ID
     * @return 删除结果
     */
    boolean deleteCategory(Long id);
    
    /**
     * 为文章分类添加标签
     * @param categoryId 分类ID
     * @param tagId 标签ID
     * @return 添加结果
     */
    boolean addTagToCategory(Long categoryId, Integer tagId);
    
    /**
     * 移除文章分类的标签（恢复默认标签）
     * @param categoryId 分类ID
     * @return 移除结果
     */
    boolean removeTagFromCategory(Long categoryId);
    
    /**
     * 获取所有可用标签
     * @return 标签列表
     */
    List<Map<String, Object>> getAllTags();
}
