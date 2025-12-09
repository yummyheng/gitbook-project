package com.example.gitbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gitbook.entity.ArticleContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章内容Mapper接口
 */
@Mapper
public interface ArticleContentMapper extends BaseMapper<ArticleContent> {
}
