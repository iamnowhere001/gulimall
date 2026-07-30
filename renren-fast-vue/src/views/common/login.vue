<template>
  <div class="site-wrapper site-page--login">
    <!-- 左侧品牌展示区 -->
    <aside class="login-aside">
      <div class="login-aside__bg"></div>
      <div class="login-aside__brand">
        <div class="login-aside__logo">谷</div>
        <h1 class="login-aside__name">谷粒商城</h1>
        <p class="login-aside__slogan">企业级电商运营中台</p>
        <ul class="login-aside__features">
          <li><span class="dot"></span>商品 / 订单 / 库存一站式管理</li>
          <li><span class="dot"></span>会员运营与精准营销</li>
          <li><span class="dot"></span>实时数据洞察与决策支持</li>
        </ul>
      </div>
      <p class="login-aside__copyright">© 2026 谷粒商城 · 后台管理系统</p>
    </aside>

    <!-- 右侧登录表单区 -->
    <main class="login-main">
      <div class="login-panel">
        <h2 class="login-panel__title">欢迎登录</h2>
        <p class="login-panel__subtitle">请输入管理员账号以进入控制台</p>

        <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" status-icon>
          <el-form-item prop="userName">
            <el-input v-model="dataForm.userName" placeholder="帐号" prefix-icon="el-icon-user"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="dataForm.password" type="password" placeholder="密码" prefix-icon="el-icon-lock"></el-input>
          </el-form-item>
          <el-form-item prop="captcha">
            <el-row :gutter="12">
              <el-col :span="14">
                <el-input v-model="dataForm.captcha" placeholder="验证码" prefix-icon="el-icon-key"></el-input>
              </el-col>
              <el-col :span="10">
                <img class="login-captcha" :src="captchaPath" @click="refreshCode" alt="验证码" />
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item>
            <el-button class="login-btn" type="primary" :loading="loading" @click="dataFormSubmit()">登 录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </main>
  </div>
</template>

<script>
  import { getUUID } from '@/utils'
  export default {
    data () {
      return {
        loading: false,
        dataForm: {
          userName: '',
          password: '',
          uuid: '',
          captcha: ''
        },
        dataRule: {
          userName: [{ required: true, message: '帐号不能为空', trigger: 'blur' }],
          password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
          captcha: [{ required: true, message: '验证码不能为空', trigger: 'blur' }]
        },
        captchaPath: '',
        // 当前验证码 blob 的 object URL, 用于释放避免内存泄漏
        captchaObjectUrl: ''
      }
    },
    created () {
      this.getCaptcha()
    },
    beforeDestroy () {
      if (this.captchaObjectUrl) {
        URL.revokeObjectURL(this.captchaObjectUrl)
      }
    },
    methods: {
      // 提交表单
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) {
            return false
          }
          this.loading = true
          this.$http({
            url: this.$http.adornUrl('/sys/login'),
            method: 'post',
            data: this.$http.adornData({
              'username': this.dataForm.userName,
              'password': this.dataForm.password,
              'uuid': this.dataForm.uuid,
              'captcha': this.dataForm.captcha
            })
          }).then(({ data }) => {
            this.loading = false
            if (data && data.code === 0) {
              this.$cookie.set('token', data.token)
              this.$router.replace({ name: 'home' })
            } else {
              this.$message.error(data && data.msg ? data.msg : '登录失败，请重试')
              this.refreshCode()
              this.dataForm.captcha = ''
            }
          }).catch((err) => {
            this.loading = false
            // 业务错误（code !== 0）已在拦截器中全局提示，此处仅刷新验证码
            if (err && err.isBusinessError) {
              this.refreshCode()
              this.dataForm.captcha = ''
            }
          })
        })
      },
      // 刷新验证码 uuid
      refreshCode () {
        this.dataForm.uuid = getUUID()
        this.getCaptcha()
      },
      // 获取验证码
      // 说明: 原先通过 <img :src> 直接加载验证码, 浏览器对跨域图片请求默认不携带 Cookie,
      // 导致验证码请求与登录请求(axios 带 withCredentials)处于不同会话, 后端基于 HttpSession
      // 存储验证码时会校验失败。这里改用 fetch 显式携带凭据并以 blob 渲染, 保证两者会话一致。
      getCaptcha () {
        this.dataForm.uuid = this.dataForm.uuid || getUUID()
        const url = this.$http.adornUrl(`/captcha.jpg?uuid=${this.dataForm.uuid}`)
        fetch(url, { credentials: 'include' })
          .then(res => res.blob())
          .then(blob => {
            if (this.captchaObjectUrl) {
              URL.revokeObjectURL(this.captchaObjectUrl)
            }
            this.captchaObjectUrl = URL.createObjectURL(blob)
            this.captchaPath = this.captchaObjectUrl
          })
          .catch(() => {
            // 网络异常时静默失败, 用户可点击图片重新获取
          })
      }
    }
  }
</script>

<style lang="scss">
  .site-wrapper.site-page--login {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    overflow: hidden;
    display: flex;
    background: #F4F6F9;

    // 左侧品牌区
    .login-aside {
      position: relative;
      flex: 0 0 46%;
      max-width: 640px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      padding: 56px 56px 36px;
      color: #fff;
      overflow: hidden;
    }
    .login-aside__bg {
      position: absolute;
      inset: 0;
      background:
        radial-gradient(720px 480px at 12% 8%, rgba(23, 179, 163, .42), transparent 60%),
        radial-gradient(560px 420px at 92% 96%, rgba(14, 148, 134, .55), transparent 60%),
        linear-gradient(135deg, #1B2330 0%, #123B38 55%, #0E6B60 100%);
      z-index: 0;
    }
    .login-aside__bg::after {
      content: "";
      position: absolute;
      right: -120px;
      top: -120px;
      width: 360px;
      height: 360px;
      border-radius: 50%;
      border: 1px dashed rgba(255, 255, 255, .16);
    }
    .login-aside__brand,
    .login-aside__copyright {
      position: relative;
      z-index: 1;
    }
    .login-aside__logo {
      width: 56px;
      height: 56px;
      border-radius: 14px;
      background: rgba(255, 255, 255, .14);
      border: 1px solid rgba(255, 255, 255, .25);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      font-weight: 700;
      backdrop-filter: blur(4px);
    }
    .login-aside__name {
      margin: 22px 0 6px;
      font-size: 34px;
      font-weight: 700;
      letter-spacing: 2px;
    }
    .login-aside__slogan {
      margin: 0;
      font-size: 16px;
      color: rgba(255, 255, 255, .82);
      letter-spacing: 1px;
    }
    .login-aside__features {
      list-style: none;
      margin: 40px 0 0;
      padding: 0;
      li {
        display: flex;
        align-items: center;
        margin-bottom: 16px;
        font-size: 15px;
        color: rgba(255, 255, 255, .9);
      }
      .dot {
        width: 7px;
        height: 7px;
        margin-right: 12px;
        border-radius: 50%;
        background: #17B3A3;
        box-shadow: 0 0 0 4px rgba(23, 179, 163, .22);
      }
    }
    .login-aside__copyright {
      font-size: 12px;
      color: rgba(255, 255, 255, .6);
    }

    // 右侧表单区
    .login-main {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 24px;
    }
    .login-panel {
      width: 100%;
      max-width: 380px;
      padding: 36px 34px;
      background: var(--c-surface);
      border-radius: var(--radius-lg);
      box-shadow: 0 12px 40px rgba(20, 30, 50, .10);
    }
    .login-panel__title {
      margin: 0 0 6px;
      font-size: 26px;
      font-weight: 700;
      color: #2B3340;
    }
    .login-panel__subtitle {
      margin: 0 0 28px;
      font-size: 14px;
      color: #8A93A2;
    }
    .login-captcha {
      width: 100%;
      height: 36px;
      border-radius: 6px;
      border: 1px solid #E2E7EF;
      cursor: pointer;
      object-fit: cover;
    }
    .login-btn {
      width: 100%;
      height: 42px;
      font-size: 15px;
      letter-spacing: 4px;
      border-radius: 8px;
    }

    // 窄屏: 隐藏品牌区, 仅展示表单
    @media (max-width: 900px) {
      .login-aside {
        display: none;
      }
      .login-main {
        background:
          radial-gradient(520px 360px at 50% -10%, rgba(23, 179, 163, .12), transparent 60%),
          #F4F6F9;
      }
    }
  }
</style>
