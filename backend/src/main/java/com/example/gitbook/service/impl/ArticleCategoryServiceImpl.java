package com.example.gitbook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.gitbook.entity.ArticleCategory;
import com.example.gitbook.mapper.ArticleCategoryMapper;
import com.example.gitbook.service.ArticleCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章分类服务实现类
 */
@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Resource
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public List<ArticleCategory> getArticleTree() {
        // 获取所有分类
        List<ArticleCategory> allCategories = articleCategoryMapper.selectList(null);
        
        // 转换为树形结构
        return buildTree(allCategories, 0L);
    }

    @Override
    public boolean addCategory(ArticleCategory category) {
        // 设置创建时间和更新时间
        Date now = new Date();
        category.setCreateTime(now);
        category.setUpdateTime(now);
        
        // 保存分类
        return articleCategoryMapper.insert(category) > 0;
    }

    @Override
    public boolean updateCategory(ArticleCategory category) {
        // 设置更新时间
        category.setUpdateTime(new Date());
        
        // 更新分类
        return articleCategoryMapper.updateById(category) > 0;
    }

    @Override
    public boolean deleteCategory(Long id) {
        // 检查是否有子分类
        LambdaQueryWrapper<ArticleCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleCategory::getParentId, id);
        List<ArticleCategory> children = articleCategoryMapper.selectList(wrapper);
        
        if (children != null && !children.isEmpty()) {
            // 有子分类，不能删除
            return false;
        }
        
        // 删除分类
        return articleCategoryMapper.deleteById(id) > 0;
    }

    /**
     * 将扁平的分类列表转换为树形结构
     * @param categories 所有分类列表
     * @param parentId 父分类ID
     * @return 树形结构的分类列表
     */
    private List<ArticleCategory> buildTree(List<ArticleCategory> categories, Long parentId) {
        return categories.stream()
                .filter(category -> {
                    // 处理顶级分类（parentId为0）
                    if (parentId == 0L) {
                        return category.getParentId() != null && category.getParentId().equals(0L);
                    } else {
                        return parentId.equals(category.getParentId());
                    }
                })
                .map(category -> {
                    // 递归获取子分类
                    List<ArticleCategory> children = buildTree(categories, category.getId());
                    category.setChildren(children);
                    return category;
                })
                .sorted((c1, c2) -> {
                    // 处理sort为null的情况
                    if (c1.getSort() == null && c2.getSort() == null) {
                        return 0;
                    } else if (c1.getSort() == null) {
                        return 1;
                    } else if (c2.getSort() == null) {
                        return -1;
                    } else {
                        return c1.getSort().compareTo(c2.getSort());
                    }
                })
                .collect(Collectors.toList());
    }
}
