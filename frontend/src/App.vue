<template>
  <div class="app-container">
    <div class="header">
      <div class="header-title">
        <h1>GitBook</h1>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="saveArticle">保存</el-button>
        <el-button type="success" @click="syncArticles">一键同步</el-button>
        <el-button type="info" @click="exportArticles">一键导出</el-button>
        <el-button type="danger" @click="deleteArticle">删除</el-button>
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
            @add-child="handleAddChild"
            @add-sibling="handleAddSibling"
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
    this.initEditor()
  },
  methods: {
    initArticleTree() {
      // 从后端API获取文章树数据
      this.$http.get('/categories/tree')
        .then(response => {
          // 后端已返回label字段，直接使用
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
      
      // 编辑器初始化后延迟加载文章树，不依赖ready事件
      setTimeout(() => {
        this.initArticleTree()
      }, 500)
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
    

    
    syncArticles() {
      // 一键同步文章内容
      if (!this.selectedNode) {
        this.$message.warning('请先选择一个分类节点')
        return
      }
      
      // 显示对话框让用户输入文件路径
      this.$prompt('请输入要同步的文件路径', '文件同步', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^[^\s]+$/, // 不允许输入空白字符
        inputErrorMessage: '文件路径不能为空'
      }).then(({ value }) => {
        // 调用后端API进行同步
        return this.$http.post('/articles/sync', {
          categoryId: this.selectedNode.id,
          filePath: value
        })
      })
      .then(response => {
        console.log('同步结果:', response.data)
        if (response.data.success) {
          this.$message.success('同步成功')
          // 重新加载文章树和当前文章内容
          this.initArticleTree()
          this.handleNodeClick(this.selectedNode)
        } else {
          this.$message.error('同步失败: ' + response.data.message)
        }
      })
      .catch(error => {
        // 处理用户取消操作
        if (error?.message !== 'cancel') {
          console.error('同步失败:', error)
          this.$message.error('同步失败: ' + (error.response?.data?.message || error.message))
        }
      })
    },
    
    deleteArticle() {
      // 删除文章或分类
      if (!this.selectedNode) {
        this.$message.warning('请先选择一个节点')
        return
      }
      
      // 所有树节点都是分类节点，执行分类删除
      const isCategory = true
      const deleteUrl = `/categories/${this.selectedNode.id}`
      const deleteType = '分类'
      
      // 直接执行删除操作，无需确认对话框
      this.$http.delete(deleteUrl)
      .then(response => {
        console.log(`${deleteType}删除成功:`, response.data)
        this.$message.success(`${deleteType}删除成功`)
        
        // 重新加载文章树
        this.initArticleTree()
        
        // 清空编辑器内容
        this.vditor.setValue('')
        this.selectedNode = null
        this.currentArticleId = null
      })
      .catch(error => {
        console.error(`${deleteType}删除失败:`, error)
        this.$message.error(`${deleteType}删除失败`)
      })
    },
    
    exportArticles() {
      // 导出文章内容
      if (!this.selectedNode) {
        this.$message.warning('请先选择一个分类节点')
        return
      }
      
      // 调用后端API进行导出
      this.$http.get(`/articles/export/${this.selectedNode.id}`, {
        responseType: 'blob' // 设置响应类型为blob，用于下载文件
      })
      .then(response => {
        console.log('导出结果:', response)
        
        // 创建下载链接并触发下载
        const blob = new Blob([response.data], { type: 'application/zip' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = 'gitbook-export.zip'
        document.body.appendChild(link)
        link.click()
        
        // 清理资源
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        this.$message.success('导出成功')
      })
      .catch(error => {
        console.error('导出失败:', error)
        this.$message.error('导出失败')
      })
    },
    
    handleAddChild(data) {
      // 处理新增子节点
      this.$prompt('请输入新子节点名称', '新增子节点', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^[^\s]+$/, // 不允许输入空白字符
        inputErrorMessage: '节点名称不能为空'
      }).then(({ value }) => {
        // 构建新分类数据
        const newCategory = {
          label: value,
          parentId: data.id,
          level: data.level + 1 // 新节点级别为父节点级别+1
        }
        
        // 调用后端API添加新分类
      this.$http.post('/categories', newCategory)
        .then(response => {
          console.log('添加子节点成功:', response.data)
          this.$message.success('添加子节点成功')
          
          // 重新加载文章树
          this.initArticleTree()
        })
        .catch(error => {
          console.error('添加子节点失败:', error)
          this.$message.error('添加子节点失败')
        })
      }).catch(() => {
        // 取消添加
        console.log('取消添加子节点')
      })
    },
    
    handleAddSibling(data) {
      // 处理新增同级节点
      this.$prompt('请输入新同级节点名称', '新增同级节点', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^[^\s]+$/, // 不允许输入空白字符
        inputErrorMessage: '节点名称不能为空'
      }).then(({ value }) => {
        // 构建新分类数据
        const newCategory = {
          label: value,
          parentId: data.parentId,
          level: data.level // 新节点与当前节点级别相同
        }
        
        // 调用后端API添加新分类
        this.$http.post('/categories', newCategory)
        .then(response => {
          console.log('添加同级节点成功:', response.data)
          this.$message.success('添加同级节点成功')
          
          // 重新加载文章树
          this.initArticleTree()
        })
        .catch(error => {
          console.error('添加同级节点失败:', error)
          this.$message.error('添加同级节点失败')
        })
      }).catch(() => {
        // 取消添加
        console.log('取消添加同级节点')
      })
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