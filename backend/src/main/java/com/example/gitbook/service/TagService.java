package com.example.gitbook.service;

import com.example.gitbook.entity.Tag;

import java.util.List;

/**
 * 标签管理接口
 */
public interface TagService {
    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<Tag> getAllTags();
    
    /**
     * 根据ID获取标签
     * @param tagId 标签ID
     * @return 标签对象
     */
    Tag getTagById(Integer tagId);
    
    /**
     * 添加标签
     * @param tag 标签对象
     * @return 添加结果
     */
    boolean addTag(Tag tag);
    
    /**
     * 更新标签
     * @param tag 标签对象
     * @return 更新结果
     */
    boolean updateTag(Tag tag);
    
    /**
     * 删除标签
     * @param tagId 标签ID
     * @return 删除结果
     */
    boolean deleteTag(Integer tagId);
}