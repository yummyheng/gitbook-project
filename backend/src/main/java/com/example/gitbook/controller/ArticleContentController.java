package com.example.gitbook.controller;

import com.example.gitbook.dto.ApiResponseDTO;
import com.example.gitbook.entity.ArticleContent;
import com.example.gitbook.service.ArticleContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 文章内容Controller
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleContentController {

    @Resource
    private ArticleContentService articleContentService;

    /**
     * 根据分类ID获取文章内容
     * @param categoryId 分类ID
     * @return 文章内容
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ArticleContent> getContentByCategoryId(@PathVariable Long categoryId) {
        ArticleContent content = articleContentService.getContentByCategoryId(categoryId);
        return ResponseEntity.ok(content);
    }

    /**
     * 根据ID获取文章内容
     * @param id 文章ID
     * @return 文章内容
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArticleContent> getContentById(@PathVariable Long id) {
        ArticleContent content = articleContentService.getContentById(id);
        return ResponseEntity.ok(content);
    }

    /**
     * 保存或更新文章内容
     * @param content 文章内容
     * @return 操作结果
     */
    @PostMapping
    public ResponseEntity<Boolean> saveOrUpdateContent(@RequestBody ArticleContent content) {
        boolean result = articleContentService.saveOrUpdateContent(content);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除文章内容
     * @param id 文章ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteContent(@PathVariable Long id) {
        boolean result = articleContentService.deleteContent(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 同步文章内容
     * @param request 包含categoryId和filePath的请求体
     * @return 同步结果
     */
    @PostMapping("/sync")
    public ResponseEntity<ApiResponseDTO<Boolean>> syncArticles(@RequestBody SyncRequest request) {
        ApiResponseDTO<Boolean> result = articleContentService.syncArticles(request.getCategoryId(), request.getFilePath());
        return ResponseEntity.ok(result);
    }
    
    /**
     * 同步请求参数
     */
    static class SyncRequest {
        private Long categoryId;
        private String filePath;
        
        public Long getCategoryId() {
            return categoryId;
        }
        
        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }
        
        public String getFilePath() {
            return filePath;
        }
        
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
    
    /**
     * 导出文章内容
     * @param categoryId 分类ID
     * @return 导出的zip文件
     */
    @GetMapping("/export/{categoryId}")
    public ResponseEntity<byte[]> exportArticles(@PathVariable Long categoryId) {
        ApiResponseDTO<byte[]> result = articleContentService.exportArticles(categoryId);
        if (result.isSuccess() && result.getData() != null) {
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=gitbook-export.zip")
                    .header("Content-Type", "application/zip")
                    .body(result.getData());
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
