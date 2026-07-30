<template>
  <transition name="network-fade">
    <div v-if="visible" class="network-overlay">
      <div class="network-card">
        <div class="network-icon">
          <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="32" cy="48" r="4" fill="#F56C6C"/>
            <path d="M44 38a16.97 16.97 0 00-24 0" stroke="#F56C6C" stroke-width="3" stroke-linecap="round"/>
            <path d="M52 30a28.28 28.28 0 00-40 0" stroke="#F56C6C" stroke-width="3" stroke-linecap="round"/>
            <path d="M60 22a39.6 39.6 0 00-56 0" stroke="#E6A23C" stroke-width="3" stroke-linecap="round"/>
          </svg>
        </div>
        <h3 class="network-title">网络连接已断开</h3>
        <p class="network-desc">请检查您的网络设置，系统将在网络恢复后自动重连</p>
        <div class="network-status-bar">
          <span class="status-dot" :class="{ 'is-active': reconnecting }"></span>
          <span class="status-text">{{ reconnecting ? '正在尝试重新连接...' : '等待连接' }}</span>
        </div>
        <div class="network-actions">
          <el-button type="primary" :loading="reconnecting" @click="retryConnect" round>
            {{ reconnecting ? '连接中...' : '重新连接' }}
          </el-button>
          <el-button @click="dismiss" round>我知道了</el-button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
import { registerNetworkStatus } from '@/utils/error-handler'
import { getUUID } from '@/utils'

export default {
  name: 'NetworkStatus',
  data () {
    return {
      visible: false,
      reconnecting: false,
      /** 是否已注册到 handler */
      registered: false,
      /** window.online 事件绑定引用 */
      _onlineHandler: null
    }
  },
  mounted () {
    // 注册到 error-handler，供 triggerFeedback 调用
    if (!this.registered) {
      registerNetworkStatus(this)
      this.registered = true
    }
    // 监听浏览器在线状态变化，自动恢复
    this._onlineHandler = () => {
      if (this.visible && navigator.onLine) {
        this.onOnline()
      }
    }
    window.addEventListener('online', this._onlineHandler)
  },
  beforeDestroy () {
    if (this._onlineHandler) {
      window.removeEventListener('online', this._onlineHandler)
    }
  },
  methods: {
    /** 由 error-handler 的 triggerFeedback 触发 */
    onOffline () {
      this.visible = true
      this.reconnecting = false
    },
    /** 网络自动恢复 */
    onOnline () {
      if (this.reconnecting) {
        this.reconnecting = false
      }
      this.visible = false
      this.$message({
        message: '网络已恢复连接',
        type: 'success',
        duration: 2000
      })
    },
    /** 手动点击重连 */
    retryConnect () {
      this.reconnecting = true
      // 使用 fetch 探测网络是否恢复(避免 axios 业务拦截器把图片响应误判为错误)
      const url = this.$http.adornUrl(`/captcha.jpg?uuid=${getUUID()}`)
      fetch(url, { credentials: 'include' })
        .then(res => {
          if (res.ok) {
            this.onOnline()
          } else {
            throw new Error('unexpected status: ' + res.status)
          }
        })
        .catch(() => {
          // 仍然断连
          this.reconnecting = false
          this.$message({
            message: '网络仍未恢复，请检查连接后重试',
            type: 'warning',
            duration: 3000
          })
        })
    },
    /** 用户手动关闭 */
    dismiss () {
      this.visible = false
      this.reconnecting = false
    }
  }
}
</script>

<style lang="scss" scoped>
.network-overlay {
  position: fixed;
  inset: 0;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(6px);
}
.network-card {
  width: 380px;
  padding: 40px 36px 32px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  text-align: center;
}
.network-icon {
  margin-bottom: 20px;
  svg { width: 64px; height: 64px; }
}
.network-title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}
.network-desc {
  margin: 0 0 20px;
  font-size: 14px;
  color: #909399;
  line-height: 1.6;
}
.network-status-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  gap: 8px;
}
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #F56C6C;
  transition: background 0.3s;
  &.is-active {
    background: #E6A23C;
    animation: pulse 1.2s ease-in-out infinite;
  }
}
.status-text {
  font-size: 13px;
  color: #606266;
}
.network-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.3); }
}
.network-fade-enter-active,
.network-fade-leave-active {
  transition: opacity 0.35s ease;
}
.network-fade-enter,
.network-fade-leave-to {
  opacity: 0;
}
</style>
