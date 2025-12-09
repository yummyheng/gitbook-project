package com.example.gitbook.service;

import com.example.gitbook.entity.ArticleCategory;
import java.util.List;

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
}
