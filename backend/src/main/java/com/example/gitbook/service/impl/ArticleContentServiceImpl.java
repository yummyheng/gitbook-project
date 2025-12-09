package com.example.gitbook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.gitbook.entity.ArticleContent;
import com.example.gitbook.mapper.ArticleContentMapper;
import com.example.gitbook.service.ArticleContentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 文章内容服务实现类
 */
@Service
public class ArticleContentServiceImpl implements ArticleContentService {

    @Resource
    private ArticleContentMapper articleContentMapper;

    @Override
    public ArticleContent getContentByCategoryId(Long categoryId) {
        LambdaQueryWrapper<ArticleContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleContent::getCategoryId, categoryId)
               .orderByDesc(ArticleContent::getUpdateTime)
               .last("LIMIT 1");
        return articleContentMapper.selectOne(wrapper);
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
}
