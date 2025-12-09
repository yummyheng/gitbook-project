<template>
  <div class="app-container">
    <div class="header">
      <div class="header-title">
        <h1>GitBook</h1>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="saveArticle">保存</el-button>
        <el-button @click="togglePreview">
          {{ vditor && vditor.options && vditor.options.mode === 'preview' ? '编辑' : '预览' }}
        </el-button>
      </div>
    </div>
    
    <div class="main-content">
        <!-- 左侧文章树面板 -->
        <div class="sidebar">
          <ArticleTree
            ref="articleTree"
            :data="articleTree"
            :expanded-keys="defaultExpandedKeys"
            :default-checked-keys="defaultCheckedKeys"
            @node-click="handleNodeClick"
          ></ArticleTree>
        </div>
      
      <!-- 右侧markdown编辑器区域 -->
      <div class="content">
        <div class="editor-container">
          <div id="vditor"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import ArticleTree from './components/ArticleTree.vue'

export default {
  name: 'App',
  components: {
    ArticleTree
  },
  data() {
    return {
      articleTree: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      defaultExpandedKeys: [],
      defaultCheckedKeys: [],
      selectedNode: null,
      currentArticleId: null,
      vditor: null
    }
  },
  mounted() {
    this.initArticleTree()
    this.initEditor()
  },
  methods: {
    initArticleTree() {
      // 从后端API获取文章树数据
      this.$http.get('/categories/tree')
        .then(response => {
          this.articleTree = response.data
          
          // 默认展开所有节点
          this.defaultExpandedKeys = []
          const collectKeys = (nodes) => {
            nodes.forEach(node => {
              this.defaultExpandedKeys.push(node.id)
              if (node.children && node.children.length > 0) {
                collectKeys(node.children)
              }
            })
          }
          collectKeys(this.articleTree)
          
          // 如果有文章，默认加载第一篇
          if (this.articleTree.length > 0 && this.articleTree[0].children && this.articleTree[0].children.length > 0) {
            this.handleNodeClick(this.articleTree[0].children[0])
          } else if (this.articleTree.length > 0) {
            this.handleNodeClick(this.articleTree[0])
          }
        })
        .catch(error => {
          console.error('获取文章树失败:', error)
          this.$message.error('获取文章树失败')
        })
    },
    
    initEditor() {
      // 初始化vditor编辑器
      this.vditor = new Vditor('vditor', {
        height: window.innerHeight - 100,
        mode: 'sv', // 同时显示编辑和预览
        preview: {
          container: '#vditor-preview',
          delay: 100
        },
        value: '',
        on: {
          change: (value) => {
            // 编辑器内容变化时的处理
            console.log('Editor content changed:', value)
          }
        }
      })
    },
    
    handleNodeClick(data) {
      // 处理节点点击事件
      this.selectedNode = data
      this.currentArticleId = null // 重置当前文章ID
      console.log('Selected node:', data)
      
      // 从后端API获取对应文章的内容
      this.$http.get(`/articles/category/${data.id}`)
        .then(response => {
          const article = response.data
          console.log('Fetched article:', article)
          if (article) {
            this.vditor.setValue(article.content)
            this.currentArticleId = article.id // 保存当前文章ID
            console.log('Set currentArticleId to:', this.currentArticleId)
          } else {
            // 如果没有文章内容，设置默认值
            this.vditor.setValue(`# ${data.label}\n\n请开始编写内容...`)
            this.currentArticleId = null // 没有文章ID
            console.log('No article found, set currentArticleId to null')
          }
        })
        .catch(error => {
          console.error('获取文章内容失败:', error)
          this.$message.error('获取文章内容失败')
        })
    },
    
    saveArticle() {
      // 保存文章内容
      if (!this.selectedNode) {
        this.$message.warning('请先选择一篇文章')
        return
      }
      
      const content = this.vditor.getValue()
      const article = {
        id: this.currentArticleId,
        title: this.selectedNode.label,
        content: content,
        categoryId: this.selectedNode.id
      }
      
      console.log('Saving article:', article)
      console.log('Current article ID:', this.currentArticleId)
      
      this.$http.post('/articles', article)
        .then(response => {
          console.log('Save response:', response)
          if (response.data) {
            this.$message.success('文章已保存')
            
            // 保存成功后重新获取当前分类下的文章内容，确保显示最新数据
            this.$http.get(`/articles/category/${this.selectedNode.id}`)
              .then(res => {
                const updatedArticle = res.data
                if (updatedArticle) {
                  this.currentArticleId = updatedArticle.id // 更新当前文章ID
                  console.log('Updated currentArticleId to:', this.currentArticleId)
                }
              })
              .catch(err => {
                console.error('刷新文章内容失败:', err)
              })
          } else {
            this.$message.error('保存失败')
          }
        })
        .catch(error => {
          console.error('保存文章失败:', error)
          this.$message.error('保存文章失败')
        })
    },
    
    togglePreview() {
      // 切换编辑/预览模式
      if (this.vditor) {
        const currentMode = this.vditor.options.mode
        this.vditor.setOptions({
          mode: currentMode === 'sv' ? 'preview' : 'sv'
        })
      }
    }
  }
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

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

.header-actions {
  display: flex;
  gap: 10px;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar {
  width: 300px;
  background-color: #fff;
  border-right: 1px solid #e6e9ed;
  overflow-y: auto;
}

.article-tree {
  padding: 10px;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.editor-container {
  height: 100%;
  border: 1px solid #e6e9ed;
  border-radius: 4px;
  overflow: hidden;
}

.preview-container {
  height: 100%;
  background-color: #fff;
  border: 1px solid #e6e9ed;
  border-radius: 4px;
  padding: 20px;
  overflow-y: auto;
}
</style>