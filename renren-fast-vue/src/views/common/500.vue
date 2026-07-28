<template>
  <div class="site-wrapper site-page--error">
    <div class="error-content">
      <div class="error-illustration">
        <div class="error-icon">
          <svg viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="60" cy="60" r="48" stroke="#F56C6C" stroke-width="5" fill="#FFF0F0"/>
            <line x1="60" y1="40" x2="60" y2="66" stroke="#F56C6C" stroke-width="5" stroke-linecap="round"/>
            <circle cx="60" cy="78" r="4" fill="#F56C6C"/>
          </svg>
        </div>
        <div class="error-code">500</div>
      </div>
      <h2 class="error-title">服务器内部错误</h2>
      <p class="error-desc">抱歉，服务器暂时无法处理您的请求。请稍后重试，如问题持续请联系技术支持。</p>
      <div class="error-actions">
        <el-button type="primary" @click="refresh">刷新重试</el-button>
        <el-button @click="$router.push({ name: 'home' })">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Error500',
  methods: {
    refresh () {
      this.$router.go(0)
    }
  }
}
</script>

<style lang="scss" scoped>
.site-wrapper.site-page--error {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F4F6F9;
}
.error-content {
  text-align: center;
  padding: 24px;
}
.error-illustration {
  position: relative;
  display: inline-block;
  margin-bottom: 24px;
}
.error-code {
  position: absolute;
  top: 60%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 36px;
  font-weight: 800;
  color: #F56C6C;
  opacity: 0.15;
}
.error-icon {
  svg { width: 120px; height: 120px; }
}
.error-title {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}
.error-desc {
  margin: 0 0 32px;
  font-size: 15px;
  color: #909399;
  max-width: 380px;
  line-height: 1.7;
}
.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}
</style>
