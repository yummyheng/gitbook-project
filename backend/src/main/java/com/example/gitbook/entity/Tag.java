package com.example.gitbook.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签实体类
 */
@Data
@TableName("tag")
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Integer id;
    
    /**
     * 标签名称
     */
    private String name;
    
    /**
     * 是否默认标签（0:否，1:是）
     */
    @TableField("default_tag")
    private Boolean defaultTag;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}