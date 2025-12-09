-- 插入文章分类数据
-- 一级分类
INSERT INTO article_category (label, parent_id, level, sort) VALUES ('第一章', 0, 1, 1);
INSERT INTO article_category (label, parent_id, level, sort) VALUES ('第二章', 0, 1, 2);
INSERT INTO article_category (label, parent_id, level, sort) VALUES ('第三章', 0, 1, 3);

-- 二级分类
INSERT INTO article_category (label, parent_id, level, sort) VALUES ('1.1 第一节', 1, 2, 1);
INSERT INTO article_category (label, parent_id, level, sort) VALUES ('1.2 第二节', 1, 2, 2);
INSERT INTO article_category (label, parent_id, level, sort) VALUES ('1.3 第三节', 1, 2, 3);

INSERT INTO article_category (label, parent_id, level, sort) VALUES ('2.1 第一节', 2, 2, 1);
INSERT INTO article_category (label, parent_id, level, sort) VALUES ('2.2 第二节', 2, 2, 2);

INSERT INTO article_category (label, parent_id, level, sort) VALUES ('3.1 第一节', 3, 2, 1);
INSERT INTO article_category (label, parent_id, level, sort) VALUES ('3.2 第二节', 3, 2, 2);
INSERT INTO article_category (label, parent_id, level, sort) VALUES ('3.3 第三节', 3, 2, 3);

-- 插入文章内容数据
INSERT INTO article_content (title, content, category_id) VALUES 
('第一章介绍', '# 第一章

这是第一章的介绍内容。

## 主要内容

- 第一节：介绍基本概念
- 第二节：详细说明
- 第三节：总结', 1),

('1.1 第一节', '# 1.1 第一节

这是第一章第一节的内容。

## 基本概念

这里介绍一些基本概念和术语。', 4),

('1.2 第二节', '# 1.2 第二节

这是第一章第二节的内容。

## 详细说明

这里详细说明一些操作和流程。', 5),

('1.3 第三节', '# 1.3 第三节

这是第一章第三节的内容。

## 总结

这里对第一章内容进行总结。', 6),

('第二章介绍', '# 第二章

这是第二章的介绍内容。

## 主要内容

- 第一节：高级功能
- 第二节：应用示例', 2),

('2.1 第一节', '# 2.1 第一节

这是第二章第一节的内容。

## 高级功能

这里介绍一些高级功能的使用方法。', 7),

('2.2 第二节', '# 2.2 第二节

这是第二章第二节的内容。

## 应用示例

这里提供一些实际应用的示例。', 8),

('第三章介绍', '# 第三章

这是第三章的介绍内容。

## 主要内容

- 第一节：最佳实践
- 第二节：常见问题
- 第三节：扩展阅读', 3),

('3.1 第一节', '# 3.1 第一节

这是第三章第一节的内容。

## 最佳实践

这里介绍一些最佳实践和建议。', 9),

('3.2 第二节', '# 3.2 第二节

这是第三章第二节的内容。

## 常见问题

这里解答一些常见的问题。', 10),

('3.3 第三节', '# 3.3 第三节

这是第三章第三节的内容。

## 扩展阅读

这里提供一些扩展阅读的资源。', 11);
