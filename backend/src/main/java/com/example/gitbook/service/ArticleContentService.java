package com.example.gitbook.service;

import com.example.gitbook.entity.ArticleContent;

/**
 * 文章内容服务接口
 */
public interface ArticleContentService {
    
    /**
     * 根据分类ID获取文章内容
     * @param categoryId 分类ID
     * @return 文章内容
     */
    ArticleContent getContentByCategoryId(Long categoryId);
    
    /**
     * 根据ID获取文章内容
     * @param id 文章ID
     * @return 文章内容
     */
    ArticleContent getContentById(Long id);
    
    /**
     * 添加或更新文章内容
     * @param content 文章内容
     * @return 操作结果
     */
    boolean saveOrUpdateContent(ArticleContent content);
    
    /**
     * 删除文章内容
     * @param id 文章ID
     * @return 删除结果
     */
    boolean deleteContent(Long id);
}
