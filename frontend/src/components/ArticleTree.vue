<template>
  <div>
    <div class="article-tree-container">
      <!-- 搜索框 -->
      <div class="search-box">
        <el-input
          v-model="filterText"
          placeholder="搜索文章"
          clearable
          size="small"
          prefix-icon="el-icon-search"
        ></el-input>
      </div>
      <!-- 树结构 -->
      <el-tree
        ref="articleTree"
        :data="data"
        :props="defaultProps"
        :expanded-keys="expandedKeys"
        :default-checked-keys="defaultCheckedKeys"
        @node-click="handleNodeClick"
        class="article-tree"
        default-expand-all
        :filter-node-method="filterNode"
      >
        <!-- 自定义节点内容，添加操作按钮和标签颜色圆点 -->
        <div slot-scope="{ node, data }" class="custom-tree-node">
          <span>
            <!-- 显示标签颜色圆点 -->
            <span 
              v-if="data.tagId && data.tagId !== 0" 
              class="tag-color-dot"
              :style="{ backgroundColor: getTagColor(data.tagId) }"
              :title="getTagName(data.tagId)"
              @click.stop="handleClearTag(data)"
            ></span>
            {{ node.label }}
          </span>
          <span class="tree-node-actions">
            <el-dropdown @command="(command) => handleAddOperation(command, data)">
              <el-button
                type="text"
                size="mini"
                title="添加节点"
              >
                <i class="el-icon-plus"></i>
                <i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="child">新增子节点</el-dropdown-item>
                <el-dropdown-item command="sibling">新增同级节点</el-dropdown-item>
                <el-dropdown-item command="tag">添加文章到标签</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </span>
        </div>
      </el-tree>
    </div>
    
    <!-- 标签选择对话框 -->
    <el-dialog
      title="选择标签"
      :visible.sync="tagDialogVisible"
      width="30%"
      @close="tagDialogVisible = false"
    >
      <div class="tag-selection">
        <el-select
          v-model="selectedTagId"
          placeholder="选择标签"
          style="width: 100%"
        >
          <el-option
            v-for="tag in tags"
            :key="tag.id"
            :label="tag.name"
            :value="tag.id"
          ></el-option>
        </el-select>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddTag">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ArticleTree',
  props: {
    data: {
      type: Array,
      default: () => []
    },
    expandedKeys: {
      type: Array,
      default: () => []
    },
    defaultCheckedKeys: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      filterText: '', // 搜索关键字
      tags: [ // 写死5个标签，包含固定颜色
        { id: 0, name: '默认', color: 'transparent' }, // 默认标签透明
        { id: 1, name: '标签1', color: 'hsl(0, 80%, 50%)' }, // 红色
        { id: 2, name: '标签2', color: 'hsl(120, 80%, 50%)' }, // 绿色
        { id: 3, name: '标签3', color: 'hsl(240, 80%, 50%)' }, // 蓝色
        { id: 4, name: '标签4', color: 'hsl(300, 80%, 50%)' } // 紫色
      ],
      tagDialogVisible: false, // 标签选择对话框可见性
      selectedCategory: null, // 当前选中的分类
      selectedTagId: 0 // 当前选择的标签ID
    }
  },
  
  watch: {
    // 监听搜索关键字变化，触发树节点过滤
    filterText(val) {
      // 确保DOM更新后再调用filter方法
      this.$nextTick(() => {
        if (this.$refs.articleTree) {
          this.$refs.articleTree.filter(val);
        }
      });
    }
  },
  
  methods: {
    handleNodeClick(data) {
      // 处理节点点击事件
      this.$emit('node-click', data)
    },
    handleAddOperation(command, data) {
      // 处理添加操作下拉菜单选择
      if (command === 'child') {
        this.handleAddChild(data)
      } else if (command === 'sibling') {
        this.handleAddSibling(data)
      } else if (command === 'tag') {
        this.handleAddTag(data)
      }
    },
    handleAddTag(data) {
      // 处理添加标签操作
      this.selectedCategory = data
      // 打开标签选择对话框
      this.tagDialogVisible = true
    },
    confirmAddTag() {
      // 确认添加标签
      // 仅为当前选中的节点添加标签
      this.$http.post(`/categories/${this.selectedCategory.id}/tags`, null, {
        params: { tagId: this.selectedTagId }
      })
        .then(response => {
          this.$message.success('标签添加成功')
          // 向父组件发送事件，通知更新文章树
          this.$emit('tag-updated')
        })
        .catch(error => {
          console.error('添加标签失败:', error)
          this.$message.error('添加标签失败')
        })
        .finally(() => {
          // 关闭对话框
          this.tagDialogVisible = false
        })
    },
    handleAddChild(data) {
      // 处理新增子节点事件
      this.$emit('add-child', data)
    },
    handleAddSibling(data) {
      // 处理新增同级节点事件
      this.$emit('add-sibling', data)
    },
    getTree() {
      return this.$refs.articleTree
    },
    // 过滤节点的方法 - 基础实现
    filterNode(value, data) {
      if (!value) return true;
      return data.label.toLowerCase().indexOf(value.toLowerCase()) !== -1;
    },
    
    // 根据标签ID获取标签颜色
    getTagColor(tagId) {
      const tag = this.tags.find(t => t.id === tagId);
      return tag ? tag.color : '#ccc';
    },
    
    // 根据标签ID获取标签名称
    getTagName(tagId) {
      const tag = this.tags.find(t => t.id === tagId);
      return tag ? tag.name : '未知标签';
    },
    
    // 递归获取节点的所有子节点
    getAllChildren(nodeData) {
      let children = [];
      
      // 如果节点有子节点
      if (nodeData.children && nodeData.children.length > 0) {
        // 遍历所有子节点
        nodeData.children.forEach(child => {
          // 添加当前子节点到结果数组
          children.push(child);
          
          // 递归获取子节点的子节点
          const grandchildren = this.getAllChildren(child);
          children = children.concat(grandchildren);
        });
      }
      
      return children;
    },
    
    // 处理点击标签圆点清除标签的事件
    handleClearTag(data) {
      // 获取当前节点的tagId
      const currentTagId = data.tagId;
      
      // 获取所有子节点
      const allChildren = this.getAllChildren(data);
      
      // 筛选出与当前节点tagId相同的子节点
      const matchingChildren = allChildren.filter(child => child.tagId === currentTagId);
      
      // 计算需要清除标签的节点总数
      const totalNodes = 1 + matchingChildren.length;
      
      // 显示确认对话框
      this.$confirm(`确定要清除当前节点及其${matchingChildren.length}个子节点的标签吗？共${totalNodes}个节点。`, '确认清除标签', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 用户确认，执行清除标签操作
        // 创建一个数组，包含当前节点和所有需要清除标签的子节点
        const nodesToClear = [data];
        nodesToClear.push(...matchingChildren);
        
        // 调用清除标签的方法
        this.clearNodesTag(nodesToClear);
      }).catch(() => {
        // 用户取消，不执行任何操作
        this.$message.info('已取消清除标签操作');
      });
    },
    
    // 清除多个节点的标签
    clearNodesTag(nodes) {
      // 创建一个Promise数组，用于存储所有的API请求
      const promises = nodes.map(node => {
        // 调用后端API移除标签
        return this.$http.delete(`/categories/${node.id}/tags`);
      });
      
      // 使用Promise.all处理所有请求
      Promise.all(promises)
        .then(responses => {
          // 所有请求成功完成
          this.$message.success(`成功清除${nodes.length}个节点的标签`);
          // 向父组件发送事件，通知更新文章树
          this.$emit('tag-updated');
        })
        .catch(error => {
          // 处理错误
          console.error('清除标签失败:', error);
          this.$message.error('清除标签失败');
        });
    }
  }
}
</script>

<style scoped>
.article-tree-container {
  height: 100%;
  overflow-y: auto;
  border-right: 1px solid #e4e7ed;
}

.search-box {
  padding: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.article-tree {
  padding: 10px;
  margin-top: 10px;
}

/* 自定义树节点样式 */
.el-tree-node {
  padding: 4px 0;
}

.el-tree-node__content {
  height: 32px;
  line-height: 32px;
}

.el-tree-node__label {
  font-size: 14px;
  color: #333;
}

/* 选中状态样式 */
.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
  background-color: #ecf5ff;
  color: #409eff;
}

/* 鼠标悬停样式 */
.el-tree-node__content:hover {
  background-color: #f5f7fa;
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
