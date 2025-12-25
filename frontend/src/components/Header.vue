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
          :label="tag.name"
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
      <!-- 标签管理按钮 -->
      <div class="tag-buttons">
        <el-button 
          type="primary" 
          size="small" 
          icon="el-icon-plus" 
          @click="showAddTagDialog"
          title="新增标签"
        ></el-button>
        <el-button 
          type="danger" 
          size="small" 
          icon="el-icon-minus" 
          @click="showDeleteTagDialog"
          title="删除标签"
          :disabled="selectedTag === 0"
        ></el-button>
      </div>
    </div>

    <!-- 新增标签对话框 -->
    <el-dialog
      title="新增标签"
      :visible.sync="addTagDialogVisible"
      width="400px"
    >
      <el-form :model="newTag" :rules="tagRules" ref="tagForm">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="newTag.name" placeholder="请输入标签名称"></el-input>
        </el-form-item>
        <el-form-item label="标签颜色" prop="color">
          <el-color-picker v-model="newTag.color" show-alpha></el-color-picker>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addTagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddTag">确定</el-button>
      </span>
    </el-dialog>
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
      selectedTag: 0, // 当前选中的标签ID，默认值为0（默认标签）
      addTagDialogVisible: false, // 新增标签对话框可见性
      newTag: { // 新增标签数据
        name: '',
        color: '#409EFF'
      },
      tagRules: { // 标签表单验证规则
        name: [
          { required: true, message: '请输入标签名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    // 标签已在data中写死，无需从后端获取
  },
  methods: {
    handleTagChange() {
      // 处理标签变化事件，向父组件发送事件
      this.$emit('tag-change', this.selectedTag)
    },
    // 显示新增标签对话框
    showAddTagDialog() {
      this.addTagDialogVisible = true
      // 重置表单
      this.newTag = {
        name: '',
        color: '#409EFF'
      }
      if (this.$refs.tagForm) {
        this.$refs.tagForm.resetFields()
      }
    },
    // 确认新增标签
    confirmAddTag() {
      // 表单验证
      this.$refs.tagForm.validate((valid) => {
        if (valid) {
          // 生成新标签ID
          const maxId = Math.max(...this.tags.map(tag => tag.id))
          const newTagId = maxId + 1
          
          // 添加新标签到tags数组
          this.tags.push({
            id: newTagId,
            name: this.newTag.name,
            color: this.newTag.color
          })
          
          // 关闭对话框
          this.addTagDialogVisible = false
          
          // 选中新添加的标签
          this.selectedTag = newTagId
          this.handleTagChange()
          
          this.$message.success('标签新增成功')
        }
      })
    },
    // 显示删除标签确认对话框
    showDeleteTagDialog() {
      if (this.selectedTag === 0) {
        this.$message.warning('默认标签不能删除')
        return
      }
      
      this.$confirm('确定要删除当前标签吗？删除后会清除所有使用该标签的文章节点的标签ID。', '删除标签', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.confirmDeleteTag()
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    // 确认删除标签
    confirmDeleteTag() {
      // 从tags数组中移除该标签
      const tagIndex = this.tags.findIndex(tag => tag.id === this.selectedTag)
      if (tagIndex !== -1) {
        this.tags.splice(tagIndex, 1)
        
        // 切换到默认标签
        this.selectedTag = 0
        this.handleTagChange()
        
        // 这里应该调用后端API清除所有使用该标签的文章节点的标签ID
        // 由于当前是前端模拟，仅做前端处理
        
        this.$message.success('标签删除成功')
      }
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
  display: flex;
  align-items: center;
  gap: 8px;
}

.tag-buttons {
  display: flex;
  gap: 4px;
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