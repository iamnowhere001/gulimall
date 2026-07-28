<template>
  <div class="site-wrapper site-page--error">
    <div class="error-content">
      <div class="error-illustration">
        <div class="error-code">403</div>
        <div class="error-icon">
          <svg viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="20" y="40" width="80" height="60" rx="6" stroke="#E6A23C" stroke-width="5" fill="#FFF7E6"/>
            <rect x="35" y="55" width="8" height="20" rx="4" fill="#E6A23C"/>
            <rect x="56" y="55" width="8" height="20" rx="4" fill="#E6A23C"/>
            <rect x="77" y="55" width="8" height="20" rx="4" fill="#E6A23C"/>
            <circle cx="60" cy="30" r="16" stroke="#F56C6C" stroke-width="4" fill="#FFF0F0"/>
            <line x1="55" y1="25" x2="65" y2="35" stroke="#F56C6C" stroke-width="3" stroke-linecap="round"/>
            <line x1="65" y1="25" x2="55" y2="35" stroke="#F56C6C" stroke-width="3" stroke-linecap="round"/>
          </svg>
        </div>
      </div>
      <h2 class="error-title">权限不足</h2>
      <p class="error-desc">抱歉，您没有访问该页面的权限。请联系管理员开通相应权限。</p>
      <div class="error-actions">
        <el-button type="primary" @click="$router.push({ name: 'home' })">返回首页</el-button>
        <el-button @click="$router.go(-1)">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Error403'
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
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 32px;
  font-weight: 800;
  color: #E6A23C;
  opacity: 0.12;
  z-index: 0;
}
.error-icon {
  position: relative;
  z-index: 1;
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
