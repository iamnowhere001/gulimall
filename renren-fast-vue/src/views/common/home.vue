<template>
  <div class="mod-home">
    <!-- 欢迎横幅 -->
    <el-card class="home-banner" shadow="never">
      <div class="home-banner__main">
        <div class="home-banner__avatar">谷</div>
        <div>
          <h2 class="home-banner__title">你好，管理员 · 欢迎回到谷粒商城运营中台</h2>
          <p class="home-banner__desc">在这里统一管理商品、订单、会员与营销，让商城运营更高效。</p>
        </div>
      </div>
      <div class="home-banner__date">
        <span class="home-banner__day">{{ today }}</span>
        <span class="home-banner__week">{{ weekday }}</span>
      </div>
    </el-card>

    <!-- 核心模块概览 -->
    <h3 class="home-section__title">核心模块</h3>
    <el-row :gutter="16">
      <el-col :span="8" v-for="m in modules" :key="m.title">
        <el-card class="home-module" shadow="hover">
          <div class="home-module__icon" :style="{ background: m.bg }">
            <i :class="m.icon"></i>
          </div>
          <div class="home-module__body">
            <div class="home-module__title">{{ m.title }}</div>
            <div class="home-module__desc">{{ m.desc }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作指引 -->
    <h3 class="home-section__title">快速开始</h3>
    <el-card class="home-guide" shadow="never">
      <el-steps :active="0" align-center finish-status="success">
        <el-step title="配置商品" description="维护分类、品牌与规格参数"></el-step>
        <el-step title="上架销售" description="发布商品并对接订单流程"></el-step>
        <el-step title="运营营销" description="策划活动、管理会员与优惠券"></el-step>
        <el-step title="数据复盘" description="查看经营数据，持续优化"></el-step>
      </el-steps>
    </el-card>
  </div>
</template>

<script>
  export default {
    data () {
      const now = new Date()
      const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      return {
        today: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`,
        weekday: weekdays[now.getDay()],
        modules: [
          { title: '商品管理', desc: '分类 · 品牌 · 规格 · 发布', icon: 'el-icon-goods', bg: 'rgba(23,179,163,.12)' },
          { title: '订单管理', desc: '下单 · 发货 · 售后', icon: 'el-icon-tickets', bg: 'rgba(64,158,255,.12)' },
          { title: '会员管理', desc: '用户 · 等级 · 积分', icon: 'el-icon-user', bg: 'rgba(245,166,35,.14)' },
          { title: '营销中心', desc: '优惠券 · 秒杀 · 活动', icon: 'el-icon-present', bg: 'rgba(230,162,60,.14)' },
          { title: '仓储库存', desc: '入库 · 出库 · 盘点', icon: 'el-icon-box', bg: 'rgba(103,194,58,.14)' },
          { title: '系统设置', desc: '菜单 · 角色 · 权限', icon: 'el-icon-setting', bg: 'rgba(144,147,153,.14)' }
        ]
      }
    }
  }
</script>

<style lang="scss">
  .mod-home {
    .home-banner {
      margin-bottom: 20px;
      border: 0;
      color: #fff;
      background:
        radial-gradient(600px 300px at 90% -40%, rgba(255, 255, 255, .12), transparent 60%),
        linear-gradient(120deg, #1B2330 0%, #123B38 55%, #0E6B60 100%);
      box-shadow: 0 10px 30px rgba(20, 30, 50, .12) !important;
    }
    .home-banner__main {
      display: flex;
      align-items: center;
    }
    .home-banner__avatar {
      flex: 0 0 auto;
      width: 56px;
      height: 56px;
      margin-right: 18px;
      border-radius: 14px;
      background: rgba(255, 255, 255, .16);
      border: 1px solid rgba(255, 255, 255, .28);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 26px;
      font-weight: 700;
    }
    .home-banner__title {
      margin: 0 0 6px;
      font-size: 19px;
      font-weight: 600;
    }
    .home-banner__desc {
      margin: 0;
      font-size: 13px;
      color: rgba(255, 255, 255, .78);
    }
    .home-banner__date {
      position: absolute;
      top: 22px;
      right: 24px;
      text-align: right;
    }
    .home-banner__day {
      display: block;
      font-size: 18px;
      font-weight: 600;
      letter-spacing: 1px;
    }
    .home-banner__week {
      font-size: 12px;
      color: rgba(255, 255, 255, .7);
    }

    .home-section__title {
      margin: 6px 0 14px;
      padding-left: 10px;
      border-left: 3px solid #17B3A3;
      font-size: 15px;
      font-weight: 600;
      color: #2B3340;
    }

    .home-module {
      margin-bottom: 16px;
      cursor: default;
    }
    .home-module .el-card__body {
      display: flex;
      align-items: center;
    }
    .home-module__icon {
      flex: 0 0 auto;
      width: 46px;
      height: 46px;
      margin-right: 14px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
      color: #2B3340;
    }
    .home-module__title {
      font-size: 15px;
      font-weight: 600;
      color: #2B3340;
    }
    .home-module__desc {
      font-size: 12px;
      color: #8A93A2;
    }

    .home-guide {
      margin-top: 4px;
    }
  }
</style>
