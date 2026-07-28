<template>
  <nav class="site-navbar">
    <!-- 左侧：折叠按钮 + 面包屑 -->
    <div class="site-navbar__left">
      <button class="site-navbar__switch" @click="sidebarFold = !sidebarFold" title="折叠/展开菜单">
        <icon-svg name="zhedie"></icon-svg>
      </button>
      <div class="site-navbar__crumbs">
        <span class="site-navbar__crumb" @click="$router.push({ name: 'home' })">首页</span>
        <template v-if="crumbs.length">
          <span class="site-navbar__crumb-sep">/</span>
          <template v-for="(c, i) in crumbs">
            <span :key="'c' + i" class="site-navbar__crumb" :class="{ 'is-current': i === crumbs.length - 1 }">{{ c }}</span>
            <span :key="'s' + i" v-if="i < crumbs.length - 1" class="site-navbar__crumb-sep">/</span>
          </template>
        </template>
      </div>
    </div>

    <!-- 中部：搜索 -->
    <div class="site-navbar__search">
      <label class="site-navbar__search-box" :class="{ 'is-focus': searchFocus }">
        <i class="el-icon-search"></i>
        <input
          ref="searchInput"
          v-model="keyword"
          @focus="searchFocus = true"
          @blur="searchFocus = false"
          @keyup.enter="searchHandle"
          type="text"
          placeholder="搜索订单、商品、会员…" />
        <span class="site-navbar__kbd">⌘K</span>
      </label>
    </div>

    <!-- 右侧：通知 + 用户 -->
    <div class="site-navbar__right">
      <el-dropdown class="site-navbar__pop" trigger="click" placement="bottom-end">
        <button class="site-navbar__icon-btn" title="通知">
          <i class="el-icon-bell"></i>
          <span class="site-navbar__dot"></span>
        </button>
        <el-dropdown-menu slot="dropdown">
          <div class="site-navbar__notif-head">
            <span class="site-navbar__notif-title">通知中心</span>
            <span class="site-navbar__notif-tag">{{ notifs.length }} 条未读</span>
          </div>
          <div
            class="site-navbar__notif-item"
            v-for="n in notifs"
            :key="n.id"
            @click="notifHandle(n)">
            <span class="site-navbar__notif-dot" :style="{ background: n.color }"></span>
            <div>
              <div class="site-navbar__notif-text" v-html="n.text"></div>
              <div class="site-navbar__notif-time">{{ n.time }}</div>
            </div>
          </div>
        </el-dropdown-menu>
      </el-dropdown>

      <button class="site-navbar__icon-btn" title="主题" @click="themeHandle">
        <i class="el-icon-moon"></i>
      </button>

      <span class="site-navbar__vline"></span>

      <el-dropdown class="site-navbar__pop" trigger="click" placement="bottom-end">
        <div class="site-navbar__user">
          <div class="site-navbar__avatar">{{ avatarText }}</div>
          <div class="site-navbar__user-meta">
            <div class="site-navbar__user-name">{{ userName || '管理员' }}</div>
            <div class="site-navbar__user-role">超级管理员</div>
          </div>
          <i class="el-icon-arrow-down"></i>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="updatePasswordHandle">修改密码</el-dropdown-item>
          <el-dropdown-item @click.native="logoutHandle">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <!-- 弹窗, 修改密码 -->
    <update-password v-if="updatePassowrdVisible" ref="updatePassowrd"></update-password>
  </nav>
</template>

<script>
  import UpdatePassword from './main-navbar-update-password'
  import { clearLoginInfo } from '@/utils'
  export default {
    data () {
      return {
        updatePassowrdVisible: false,
        searchFocus: false,
        keyword: '',
        notifs: [
          { id: 1, color: '#fb7185', text: '订单 <b>#GM2024724-039</b> 申请退款，待审核', time: '2 分钟前' },
          { id: 2, color: '#fbbf24', text: '「小米 14 Pro」库存仅剩 12 件', time: '26 分钟前' },
          { id: 3, color: '#5eead4', text: '秒杀活动「夏日清凉」将于 18:00 开启', time: '1 小时前' }
        ]
      }
    },
    components: {
      UpdatePassword
    },
    computed: {
      sidebarFold: {
        get () { return this.$store.state.common.sidebarFold },
        set (val) { this.$store.commit('common/updateSidebarFold', val) }
      },
      userName: {
        get () { return this.$store.state.user.name }
      },
      // 头像首字母
      avatarText () {
        const n = this.userName || '管'
        return n.charAt(0).toUpperCase()
      },
      // 面包屑（取自路由 matched，过滤掉 home）
      crumbs () {
        return this.$route.matched
          .filter(r => r.meta && r.meta.title && r.name !== 'home')
          .map(r => r.meta.title)
      }
    },
    mounted () {
      // ⌘K / Ctrl+K 聚焦搜索
      window.addEventListener('keydown', this.keydownHandle)
    },
    beforeDestroy () {
      window.removeEventListener('keydown', this.keydownHandle)
    },
    methods: {
      // 搜索（占位）
      searchHandle () {
        const kw = this.keyword.trim()
        if (!kw) return
        this.$message({
          message: `搜索：${kw}`,
          type: 'info'
        })
      },
      // 通知点击（占位）
      notifHandle (n) {
        this.$message({ message: '已查看通知', type: 'success' })
      },
      // 主题切换（占位）
      themeHandle () {
        this.$message({ message: '主题切换功能即将开放', type: 'info' })
      },
      // ⌘K 聚焦
      keydownHandle (e) {
        if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
          e.preventDefault()
          this.$refs.searchInput && this.$refs.searchInput.focus()
        }
      },
      // 修改密码
      updatePasswordHandle () {
        this.updatePassowrdVisible = true
        this.$nextTick(() => {
          this.$refs.updatePassowrd.init()
        })
      },
      // 退出
      logoutHandle () {
        this.$confirm(`确定退出?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/sys/logout'),
            method: 'post',
            data: this.$http.adornData()
          }).then(({data}) => {
            if (data && data.code === 0) {
              clearLoginInfo()
              this.$router.push({ name: 'login' })
            }
          })
        }).catch(() => {})
      }
    }
  }
</script>
