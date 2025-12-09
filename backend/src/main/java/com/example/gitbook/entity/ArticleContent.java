package com.example.gitbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章内容实体类
 */
@Data
@TableName("article_content")
public class ArticleContent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章正文（Markdown格式）
     */
    private String content;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
