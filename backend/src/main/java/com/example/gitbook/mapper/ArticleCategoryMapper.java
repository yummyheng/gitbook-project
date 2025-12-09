package com.example.gitbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gitbook.entity.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章分类Mapper接口
 */
@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategory> {
}
