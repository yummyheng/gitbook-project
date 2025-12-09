<template>
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
    ></el-tree>
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
      filterText: '' // 搜索关键字
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
    getTree() {
      return this.$refs.articleTree
    },
    // 过滤节点的方法 - 基础实现
    filterNode(value, data) {
      if (!value) return true;
      return data.label.toLowerCase().indexOf(value.toLowerCase()) !== -1;
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
</style>
