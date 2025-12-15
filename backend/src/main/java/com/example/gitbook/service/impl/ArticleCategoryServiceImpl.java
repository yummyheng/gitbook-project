package com.example.gitbook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.gitbook.entity.ArticleCategory;
import com.example.gitbook.entity.ArticleContent;
import com.example.gitbook.mapper.ArticleCategoryMapper;
import com.example.gitbook.service.ArticleCategoryService;
import com.example.gitbook.service.ArticleContentService;
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
        
        // 根据父分类ID计算level
        if (category.getParentId() != null) {
            if (category.getParentId() == 0L) {
                // 顶级分类
                category.setLevel(1);
            } else {
                // 非顶级分类，获取父分类的level
                ArticleCategory parentCategory = articleCategoryMapper.selectById(category.getParentId());
                if (parentCategory != null) {
                    // 子分类的level为父分类level+1
                    category.setLevel(parentCategory.getLevel() + 1);
                } else {
                    // 父分类不存在，默认设为顶级分类
                    category.setLevel(1);
                }
            }
        } else {
            // 默认为顶级分类
            category.setLevel(1);
        }
        
        // 保存分类
        return articleCategoryMapper.insert(category) > 0;
    }

    @Override
    public boolean updateCategory(ArticleCategory category) {
        // 设置更新时间
        category.setUpdateTime(new Date());
        
        // 如果父分类ID发生变化，更新level
        ArticleCategory oldCategory = articleCategoryMapper.selectById(category.getId());
        if (oldCategory != null && category.getParentId() != null && !category.getParentId().equals(oldCategory.getParentId())) {
            if (category.getParentId() == 0L) {
                // 改为顶级分类
                category.setLevel(1);
            } else {
                // 改为非顶级分类，获取新父分类的level
                ArticleCategory parentCategory = articleCategoryMapper.selectById(category.getParentId());
                if (parentCategory != null) {
                    // 新分类的level为父分类level+1
                    category.setLevel(parentCategory.getLevel() + 1);
                    
                    // 更新所有子分类的level
                    updateChildrenLevel(category.getId(), category.getLevel());
                } else {
                    // 父分类不存在，默认设为顶级分类
                    category.setLevel(1);
                }
            }
        }
        
        // 更新分类
        return articleCategoryMapper.updateById(category) > 0;
    }
    
    /**
     * 递归更新所有子分类的level
     * @param parentId 父分类ID
     * @param parentLevel 父分类level
     */
    private void updateChildrenLevel(Long parentId, Integer parentLevel) {
        LambdaQueryWrapper<ArticleCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleCategory::getParentId, parentId);
        List<ArticleCategory> children = articleCategoryMapper.selectList(wrapper);
        
        if (children != null && !children.isEmpty()) {
            for (ArticleCategory child : children) {
                // 子分类的level为父分类level+1
                child.setLevel(parentLevel + 1);
                child.setUpdateTime(new Date());
                articleCategoryMapper.updateById(child);
                
                // 递归更新子分类的子分类
                updateChildrenLevel(child.getId(), child.getLevel());
            }
        }
    }

    private ArticleContentService articleContentService;

    @Resource
    public void setArticleContentService(ArticleContentService articleContentService) {
        this.articleContentService = articleContentService;
    }

    @Override
    public boolean deleteCategory(Long id) {
        // 级联删除该分类及其所有子分类
        deleteCategoryRecursive(id);
        return true;
    }
    
    /**
     * 递归删除分类及其所有子分类
     * @param id 分类ID
     */
    private void deleteCategoryRecursive(Long id) {
        // 先获取该分类的所有子分类
        LambdaQueryWrapper<ArticleCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleCategory::getParentId, id);
        List<ArticleCategory> children = articleCategoryMapper.selectList(wrapper);
        
        // 递归删除所有子分类
        if (children != null && !children.isEmpty()) {
            for (ArticleCategory child : children) {
                deleteCategoryRecursive(child.getId());
            }
        }
        
        // 删除当前分类关联的文章内容
        List<ArticleContent> contents = articleContentService.getAllContentsByCategoryId(id);
        for (ArticleContent content : contents) {
            articleContentService.deleteContent(content.getId());
        }
        
        // 删除当前分类
        articleCategoryMapper.deleteById(id);
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
