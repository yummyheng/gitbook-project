<template>
  <div class="header">
    <div class="header-title">
      <h1>GitBook</h1>
    </div>
    <div class="header-tags">
      <el-select
        v-model="selectedTag"
        placeholder="选择标签"
        size="small"
        @change="handleTagChange"
      >
        <el-option
          v-for="tag in tags"
          :key="tag.id"
          :value="tag.id"
        >
          <span class="tag-option-content">
            <!-- 显示标签颜色圆点 -->
            <span 
              v-if="tag.id !== 0" 
              class="tag-color-dot"
              :style="{ backgroundColor: tag.color }"
            ></span>
            {{ tag.name }}
          </span>
        </el-option>
      </el-select>
    </div>
    <div class="header-actions">
      <el-button type="primary" @click="$emit('save-article')">保存</el-button>
      <el-button type="success" @click="$emit('sync-articles')">一键同步</el-button>
      <el-button type="info" @click="$emit('export-articles')">一键导出</el-button>
      <el-button type="danger" @click="$emit('delete-article')">删除</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Header',
  emits: ['save-article', 'sync-articles', 'export-articles', 'delete-article', 'tag-change'],
  data() {
    return {
      tags: [ // 写死5个标签，包含固定颜色
        { id: 0, name: '默认', color: 'transparent' }, // 默认标签透明
        { id: 1, name: '标签1', color: 'hsl(0, 80%, 50%)' }, // 红色
        { id: 2, name: '标签2', color: 'hsl(120, 80%, 50%)' }, // 绿色
        { id: 3, name: '标签3', color: 'hsl(240, 80%, 50%)' }, // 蓝色
        { id: 4, name: '标签4', color: 'hsl(300, 80%, 50%)' } // 紫色
      ],
      selectedTag: 0 // 当前选中的标签ID，默认值为0（默认标签）
    }
  },
  created() {
    // 标签已在data中写死，无需从后端获取
  },
  methods: {
    handleTagChange() {
      // 处理标签变化事件，向父组件发送事件
      this.$emit('tag-change', this.selectedTag)
    }
  }
}
</script>

<style scoped>
.header {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e6e9ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-title h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.header-tags {
  margin-right: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 标签选项内容样式 */
.tag-option-content {
  display: flex;
  align-items: center;
}

/* 标签颜色圆点样式 */
.tag-color-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}
</style>