# renren-fast-vue

`renren-fast-vue` 是 [renren-fast](https://gitee.com/renrenio/renren-fast) 后台管理系统的前端项目，基于 Vue 2 + Element UI 实现，用于快速搭建中后台管理界面（本项目作为谷粒商城 gulimall 的管理后台前端）。

## 技术栈

- 核心框架：[Vue 2.6](https://cn.vuejs.org/)
- UI 组件库：[Element UI 2.15](https://element.eleme.cn/)
- 状态管理：[Vuex 3.6](https://vuex.vuejs.org/zh/)
- 路由：[Vue Router 3.6](https://router.vuejs.org/zh/)
- HTTP 客户端：[axios 0.27](https://axios-http.com/)
- 构建工具：[webpack 5.92](https://webpack.js.org/)
- 图标：SVG Sprite（iconfont）
- 其他：`vue-cookie`、`pubsub-js`、`lodash`、`mockjs`（本地模拟数据）

## 环境要求

| 依赖 | 版本要求 |
| --- | --- |
| Node.js | >= 14.0.0 |
| npm | >= 6.0.0 |

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器（默认端口 8001，自动打开浏览器）
npm run dev
# 或
npm start
```

启动后默认访问 `http://localhost:8001`。

## 构建

```bash
# 生产环境构建，输出到 dist/
npm run build

# QA 环境构建
npm run build:qa

# UAT 环境构建
npm run build:uat
```

构建产物输出到 `dist/` 目录，可部署到任意静态服务器。

## 配置说明

### 后端接口代理

开发环境的接口代理在 `config/index.js` 中配置，默认将 `/api` 请求代理到 `http://localhost:88`：

```js
proxyTable: {
  '/api': {
    target: 'http://localhost:88',
    changeOrigin: true,
    pathRewrite: { '^/api': '/api' }
  }
}
```

如需对接不同的后端地址，修改 `target` 即可。

### 全局配置

站点全局配置（如后端 baseUrl）位于 `src/utils/index.js` 中的 `window.SITE_CONFIG`，而 `NODE_ENV` 相关变量在 `config/dev.env.js`、`config/prod.env.js`、`config/test.env.js` 中定义。

### 本地模拟数据（Mock）

默认 `src/main.js` 中的 mock 拦截已被注释关闭（直接连接后端服务器）。若想在本地脱离后端调试，取消 `main.js` 中以下代码的注释即可启用 mock：

```js
if (process.env.NODE_ENV !== 'production') {
    require('@/mock')
}
```

mock 数据位于 `src/mock/` 目录。

## 目录结构

```
├── build/                  # webpack 构建配置（dev / prod / common）
├── config/                 # 开发 / 生产 / 测试环境配置、代理配置
├── src/
│   ├── assets/             # 静态资源（scss 主题样式等）
│   ├── components/         # 公共组件
│   ├── element-ui/         # element-ui 按需引入配置
│   ├── element-ui-theme/   # element-ui 主题
│   ├── icons/              # SVG 图标及自动注册脚本
│   ├── mock/               # 本地模拟数据
│   ├── router/             # 路由配置（含动态菜单路由）
│   ├── store/              # Vuex 状态管理
│   ├── utils/              # 工具类（http 请求、权限判断等）
│   ├── views/              # 业务页面（.vue）
│   ├── App.vue             # 根组件
│   └── main.js             # 入口文件
├── static/                 # 无需编译的静态资源
└── index.html              # 页面模板
```

## 功能特性

- 登录 / 登出、基于 token 的鉴权
- 动态菜单路由（菜单由后端配置，前端动态注册）
- 多标签页（tabs）导航，支持关闭当前 / 其它 / 全部、刷新
- 按钮级权限控制（`isAuth` 方法）
- SVG 矢量图标
- 多套 Element UI 主题 / 皮肤定制
- 统一的异常与网络状态处理

## 相关项目

- 后端服务：[renren-fast](https://gitee.com/renrenio/renren-fast)
- 配套代码生成器：[renren-generator](https://gitee.com/renrenio/renren-generator)

## 常见问题

- **端口被占用**：修改 `config/index.js` 中的 `dev.port`。
- **接口 404 / 无法连接后端**：确认后端已启动，并检查 `config/index.js` 中 `proxyTable` 的 `target` 地址是否正确。
- **依赖安装缓慢或失败**：建议使用国内镜像，如 `npm config set registry https://registry.npmmirror.com`。
