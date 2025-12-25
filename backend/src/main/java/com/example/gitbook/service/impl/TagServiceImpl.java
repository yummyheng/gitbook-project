package com.example.gitbook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gitbook.entity.Tag;
import com.example.gitbook.mapper.TagMapper;
import com.example.gitbook.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签管理实现类
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Override
    public List<Tag> getAllTags() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Tag::getSort);
        return list(queryWrapper);
    }

    @Override
    public Tag getTagById(Integer tagId) {
        return getById(tagId);
    }

    @Override
    public boolean addTag(Tag tag) {
        return save(tag);
    }

    @Override
    public boolean updateTag(Tag tag) {
        return updateById(tag);
    }

    @Override
    public boolean deleteTag(Integer tagId) {
        return removeById(tagId);
    }
}