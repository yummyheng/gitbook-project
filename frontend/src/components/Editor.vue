<template>
  <div class="content">
    <div class="editor-container">
      <div id="vditor"></div>
    </div>
  </div>
</template>

<script>
import Vditor from 'vditor'
import 'vditor/dist/index.css'

export default {
  name: 'Editor',
  data() {
    return {
      vditor: null
    }
  },
  mounted() {
    this.initEditor()
  },
  methods: {
    initEditor() {
      // 初始化vditor编辑器
      this.vditor = new Vditor('vditor', {
        height: window.innerHeight - 100,
        mode: 'sv', // 同时显示编辑和预览
        preview: {
          container: '#vditor-preview',
          delay: 100
        },
        // 配置本地cdn路径，指向node_modules中的vditor资源
        cdn: '/node_modules/vditor',
        value: '',
        on: {
          change: (value) => {
            // 编辑器内容变化时的处理
            this.$emit('content-change', value)
          }
        }
      })
    },
    setValue(content) {
      // 设置编辑器内容
      if (this.vditor) {
        this.vditor.setValue(content)
      }
    },
    getValue() {
      // 获取编辑器内容
      if (this.vditor) {
        return this.vditor.getValue()
      }
      return ''
    },
    clear() {
      // 清空编辑器内容
      if (this.vditor) {
        this.vditor.setValue('')
      }
    }
  }
}
</script>

<style scoped>
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
</style>