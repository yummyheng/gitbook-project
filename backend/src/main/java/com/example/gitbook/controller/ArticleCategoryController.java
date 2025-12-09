package com.example.gitbook.controller;

import com.example.gitbook.entity.ArticleCategory;
import com.example.gitbook.service.ArticleCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文章分类Controller
 */
@RestController
@RequestMapping("/api/categories")
public class ArticleCategoryController {

    @Resource
    private ArticleCategoryService articleCategoryService;

    /**
     * 获取文章分类树
     * @return 分类树结构
     */
    @GetMapping("/tree")
    public ResponseEntity<List<ArticleCategory>> getArticleTree() {
        List<ArticleCategory> tree = articleCategoryService.getArticleTree();
        return ResponseEntity.ok(tree);
    }

    /**
     * 添加文章分类
     * @param category 分类信息
     * @return 操作结果
     */
    @PostMapping
    public ResponseEntity<Boolean> addCategory(@RequestBody ArticleCategory category) {
        boolean result = articleCategoryService.addCategory(category);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新文章分类
     * @param id 分类ID
     * @param category 分类信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateCategory(@PathVariable Long id, @RequestBody ArticleCategory category) {
        category.setId(id);
        boolean result = articleCategoryService.updateCategory(category);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除文章分类
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCategory(@PathVariable Long id) {
        boolean result = articleCategoryService.deleteCategory(id);
        return ResponseEntity.ok(result);
    }
}
