<template>
  <footer class="app-footer">
    <p>谷粒商城 · 微服务电商前台（Vue 3 + Vite 独立门户）</p>
    <p class="app-footer__links">
      <a href="javascript:;">关于我们</a> |
      <a href="javascript:;">联系客服</a> |
      <a href="javascript:;">商家入驻</a> |
      <a href="javascript:;">隐私政策</a>
    </p>
  </footer>
</template>

<script setup></script>

<style scoped>
.app-footer {
  border-top: 1px solid #eee;
  margin-top: 32px;
  padding: 24px 16px;
  text-align: center;
  color: #999;
  font-size: 12px;
}
.app-footer__links a {
  color: #999;
  text-decoration: none;
  margin: 0 4px;
}
.app-footer__links a:hover {
  color: #e1251b;
}
</style>
