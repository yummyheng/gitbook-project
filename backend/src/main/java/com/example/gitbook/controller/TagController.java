package com.example.gitbook.controller;

import com.example.gitbook.dto.ApiResponseDTO;
import com.example.gitbook.entity.Tag;
import com.example.gitbook.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理控制器
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签
     * @return 标签列表
     */
    @GetMapping
    public ApiResponseDTO<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return ApiResponseDTO.success(tags);
    }

    /**
     * 根据ID获取标签
     * @param id 标签ID
     * @return 标签对象
     */
    @GetMapping("/{id}")
    public ApiResponseDTO<Tag> getTagById(@PathVariable Integer id) {
        Tag tag = tagService.getTagById(id);
        if (tag != null) {
            return ApiResponseDTO.success(tag);
        } else {
            return ApiResponseDTO.fail("标签不存在");
        }
    }

    /**
     * 添加标签
     * @param tag 标签对象
     * @return 添加结果
     */
    @PostMapping
    public ApiResponseDTO<Void> addTag(@RequestBody Tag tag) {
        boolean result = tagService.addTag(tag);
        if (result) {
            return ApiResponseDTO.success(null);
        } else {
            return ApiResponseDTO.fail("添加标签失败");
        }
    }

    /**
     * 更新标签
     * @param tag 标签对象
     * @return 更新结果
     */
    @PutMapping
    public ApiResponseDTO<Void> updateTag(@RequestBody Tag tag) {
        boolean result = tagService.updateTag(tag);
        if (result) {
            return ApiResponseDTO.success(null);
        } else {
            return ApiResponseDTO.fail("更新标签失败");
        }
    }

    /**
     * 删除标签
     * @param id 标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponseDTO<Void> deleteTag(@PathVariable Integer id) {
        boolean result = tagService.deleteTag(id);
        if (result) {
            return ApiResponseDTO.success(null);
        } else {
            return ApiResponseDTO.fail("删除标签失败");
        }
    }
}