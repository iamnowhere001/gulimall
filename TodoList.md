# 谷粒商城（guli-mall）版本化迭代 TodoList

> **生成日期**：2026-07-28
> **项目定位**：这是一个学习项目 —— 通过实现 B2C 电商完整业务，系统化掌握 Spring Cloud 微服务技术栈，锻炼 Java 服务端架构与编码能力。
>
> **状态标记**：
> - ✅ 已交付（代码实现 + 可运行）
> - 🔲 规划中（待进入迭代）
> - ⚠️ 部分实现但未闭环
>
> **命名约定**：文档中 `mall-xxx` 为模块简称，Maven 实际模块名 `gulimall-xxx`（如 `mall-seckill` ↔ `gulimall-seckill`）。
>
> ---
> **版本总览**
>
> | 版本 | 主题 | 核心学习目标 | 业务交付 |
> | :--- | :--- | :--- | :--- |
> | **v1.0.0（当前）** | 基础链路跑通：商品浏览 → 搜索 → 加购 → 登录 → 结算 → 秒杀基础 | Nacos/Feign/Gateway/Session共享/RabbitMQ延时队列/Redisson | 电商主流程可手动点击跑通 |
> | **v1.1.0** | 交易闭环 + 分布式事务最终一致性 | Alipay SDK/本地消息表/可靠消息最终一致性/优惠券引擎/Sentinel控制台 | 下单→支付→回调→关单/我的订单全链路自动化可验证 |
> | **v1.2.0** | 工程化可部署 + 可观测 | Sleuth+Zipkin/Sentinel规则Nacos持久化/Docker多阶段构建/CI思路 | Docker Compose 一键起整个系统 + 调用链可视化 + 限流规则持久化 |
> ---

---

## 一、v1.0.0（当前基线 · 基础链路跑通）✅

> **发布日期**：2026-07-28
> **目标**：所有核心微服务能启动并互相调用，电商主流程可手动点击跑通。
> **技术学习重点**：Nacos 注册与配置、OpenFeign 跨服务调用、Spring Cloud Gateway 域名路由、Spring Session Redis 子域共享、RabbitMQ 延时队列、Redisson 分布式锁、Thymeleaf SSR 前后端混合工程。

### 1.1 基础设施与中间件 ✅

| 组件 | 版本 | 状态 | 备注 |
| :--- | :--- | :--- | :--- |
| Nacos | 2.2.0 / 1.2.0（本地开发） | ✅ | 各模块 bootstrap.yml 已配置命名空间；注册/配置中心均生效 |
| MySQL | 8.0 | ✅ | 已创建 mall_pms/oms/wms/sms/ums/admin 6 库 |
| Redis | latest | ✅ | 缓存、Session 共享、购物车存储、分布式锁、秒杀信号量 |
| Elasticsearch | 7.17.0 | ✅ | 商品索引已存在；IK 分词器按文档规划 |
| Kibana | 7.17.0 | ✅ | 与 ES 版本对齐 |
| RabbitMQ | 3.8 | ✅ | 延时关单/库存解锁/秒杀削峰 3 条 MQ 链路声明 |
| Docker Compose | docker-compose.yml | ✅ | 覆盖全部中间件；本地开发环境（Mac M4 / JDK8）已验证可启动 |

### 1.2 微服务模块交付矩阵 ✅

> 13 个模块 + renren-fast 后台 + renren-fast-vue 前端，共 **10 个业务模块 + 2 个公共/网关 + 2 个后台**。

| # | 模块 | 端口 | 数据库 | 已交付功能 | 关键文件入口 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | mall-common | - | - | 统一 R、PageUtils、BizCodeEnum、常量、TO/VO、工具类 | `BizCodeEnum.java`、`MemberResponseVo.java`、`SkuEsModel.java` |
| 2 | mall-gateway | 88 | - | 域名路由（gulimall.com / search / item / auth / cart / order / member / seckill / thirdparty）+ RewritePath `/api/**` → 内部路径 | `application.yml`（网关路由） |
| 3 | mall-product | 10000 | mall_pms | 三级分类树形/拖拽、品牌 CRUD、属性&分组、SPU/SKU 全链路 CRUD、商品评价、**商品上架（Feign→ES）**、前台首页 `index.html`、SKU 详情 `item.html` | `SpuInfoController#/spuinfo/{spuId}/up`、`IndexController`、`ItemController` |
| 4 | mall-search | 12000 | ES | 商品上架写入 ES（批量 save）、检索接口（关键词+分类+品牌+属性+价格区间+销量排序+分页+品牌/属性聚合）、前台搜索页 `list.html` | `ElasticSaveController`、`MallSearchController#listPage` |
| 5 | mall-cart | 40000 | Redis | 加购、改数量、切换选中、删除、取已勾选结算项；临时购物车（user-key Cookie）+ 登录购物车（memberId）；`cartList.html` 加购成功 `success.html` | `CartController`、`CartServiceImpl#addToCart` |
| 6 | mall-auth-server | 20000 | - | 短信验证码（Redis 60s 防刷 + 10min TTL）、账号注册（JSR303 校验 + BCrypt 密文入库）、账号密码登录、微博 OAuth2.0 登录、微信扫码登录（预留）、登录/注册页面、登录拦截、退出登录、Session 子域共享 | `LoginController`、`OAuth2Controller`、`WxApiController`、`GulimallSessionConfig` |
| 7 | mall-member | 8000 | mall_ums | 会员 CRUD、注册/登录/社交登录/微信登录接口（供 auth 服务 Feign）、会员等级、成长值/积分历史、收货地址（按 memberId 查询供结算使用）、收藏 SPU/专题、登录日志、统计信息；会员中心入口（`MemberWebController`→查订单） | `MemberController#/register` `#/login` `#/oauth2/login` `#/weixin/login`、`MemberServiceImpl`、`MemberReceiveAddressController` |
| 8 | mall-order | 9000 | mall_oms | 结算页 `/toTrade`（地址 Feign→member、购物项 Feign→cart、库存校验 Feign→ware、防重令牌 Redis）；提交订单 `/submitOrder`（Lua 原子防重 + 服务端价格校验 + 订单/订单项落库）；**RabbitMQ 延时关单**（`MyRabbitMQConfig` + `OrderCloseListener`）；秒杀订单异步监听（`OrderSeckillListener`）；`confirm.html` 结算页；`pay.html` 支付占位页 | `OrderWebController`、`OrderServiceImpl#confirmOrder` `#submitOrder`、`OrderCloseListener` |
| 9 | mall-ware | 11000 | mall_wms | 仓库 CRUD、库存 SKU CRUD、采购流程（合并/分配/领取/完成）、库存工作单；**StockReleaseListener** 监听关单事件解锁库存；Fare 运费查询、按 skuIds 锁定库存 | `WareSkuController#/ware/waresku/lock/order`、`StockReleaseListener`、`FareController` |
| 10 | mall-coupon | 7000 | mall_sms | 优惠券、满减、阶梯价、会员价、秒杀活动/场次/商品关联、积分、首页广告、专题 CRUD（表结构与后台 CRUD 齐全，实际业务引擎待 v1.1.0 接入） | `CouponController`、`SeckillSessionController`、`SeckillSkuRelationController` |
| 11 | mall-seckill | 25000 | Redis | SecKillSkuListener 每日凌晨定时拉取秒杀场次，Redis 存「场次 + 商品信号量 + 随机码」；`SeckillController` 提供当前秒杀列表、SKU 秒杀信息、/kill 执行；**Redis 信号量原子扣减防超卖**；随机码接口防刷；投递 MQ → order 服务异步建单 | `SecKillSkuListener`、`SeckillController#kill`、`MyRabbitMQConfig` |
| 12 | mall-third-party | 30000 | - | 阿里云 OSS 服务端签名直传（Policy/Signature）、短信发送接口（供 auth→验证码 Feign） | `OSSController`、`SmsController` |
| 13 | renren-fast / renren-fast-vue | 8080 | mall_admin | 后台管理（Shiro + JWT），商品/订单/营销/会员/库存/系统菜单可视化 CRUD 页面 | `SysUserController` 等 renren 模块 |

### 1.3 商城前台页面矩阵 ✅

| 页面 | URL / 入口 | 所在模块 | 说明 |
| :--- | :--- | :--- | :--- |
| 商城首页 | http://gulimall.com:88/ | mall-product | 分类导航 + 商品展示 |
| 商品详情 | http://item.gulimall.com:88/{skuId}.html | mall-product | SKU 详情、销售属性切换 |
| 搜索列表 | http://search.gulimall.com:88/list.html?keyword=xxx | mall-search | 关键词 + 筛选、排序、分页、品牌/属性聚合 |
| 加购流程 | mall-cart `/addToCart` → `success.html` → `cartList.html` | mall-cart | 加购、改数量、删除、选中 |
| 结算页 | http://order.gulimall.com:88/toTrade | mall-order | 地址 + 购物项 + 金额 + 令牌；需登录（重定向→auth） |
| 登录页 | http://auth.gulimall.com:88/login.html | mall-auth-server | 账号密码 / 微博 / 微信扫码 |
| 注册页 | http://auth.gulimall.com:88/reg.html | mall-auth-server | 短信验证码 + 用户名/密码/手机 |
| 支付占位页 | mall-order `/pay.html` | mall-order | 提交订单成功跳转，**显示订单号 + 金额**，未接实际支付 SDK |

### 1.4 已打通的业务链路 ✅

1. **商品浏览链路**：gulimall.com 首页 → 分类/品牌 → item.html 详情 → 加购
2. **注册登录链路**：reg.html 发验证码 → 校验 → 入库；login.html 账号密码登录 / 微博授权 → Session → 首页已登录态
3. **加购购物车链路**：未登录 Cookie user-key → 临时购物车；登录后 memberId 购物车
4. **结算下单链路**：order.gulimall.com/toTrade → 地址（Feign→member）+ 购物项（Feign→cart）+ 库存校验 + 令牌 → 提交 → 防重校验 → 订单/订单项落库 → 跳转 pay.html
5. **秒杀链路**（基础版）：SecKillSkuListener 上架 → Redis 存信号量 → seckill.gulimall.com 取 → /kill → 防超卖 → 发 MQ → OrderSeckillListener 建单
6. **消息驱动链路**：下单后发送延时消息 → `order.release.order.queue` 到达时 OrderCloseListener 关单 → 发 `stock.release.stock.queue` → StockReleaseListener 解锁库存

### 1.5 v1.0.0 明确未闭环的缺口（为 v1.1.0 伏笔）⚠️

| # | 缺口 | 影响 | 交付版本 |
| :--- | :--- | :--- | :--- |
| 1 | pay.html 未接真实支付宝/微信支付 SDK | 支付链路停在占位页，无法真实验证 | v1.1.0 |
| 2 | 下单提交订单 **未实际锁定库存**（ware#/lock 接口存在，但 `submitOrder` 中未调用 ware Feign 锁定） | 最终一致性的「锁定→关单释放」无闭环源头，库存未真正被占住 | v1.1.0 |
| 3 | 优惠券金额未参与结算计算（`confirmVo.integration=0`、未减额），仅占位 Feign 拉取展示 | 价格计算不真实 | v1.1.0 |
| 4 | 我的订单 / 订单详情 / 订单支付 前台页面缺失 | 会员中心「查订单」能力空缺 | v1.1.0 |
| 5 | 秒杀会场页（`seckill.html`）缺失 | 用户无入口浏览秒杀列表并下单 | v1.1.0 |
| 6 | 登录后临时购物车 → 会员购物车 **未合并** | 切换登录态后购物车商品丢失 | v1.1.0 |
| 7 | Sentinel 控制台、Sleuth/Zipkin 均未接入，限流规则 & 链路追踪不可观测 | 生产级可观测能力缺失 | v1.2.0 |
| 8 | 各服务无 Dockerfile，无法镜像化部署；仅中间件 Docker Compose | 一键部署不完整 | v1.2.0 |

---

## 二、v1.1.0（交易闭环 + 分布式事务最终一致性）🔲 规划中

> **目标版本**：下单 → 支付 → 回调 → 我的订单 + 秒杀会场 + 库存锁定/优惠券真实扣减，**交易闭环可全自动验证**。
> **核心学习目标**：
> - 支付宝沙箱 SDK 接入（验签、同步/异步回调、退款）
> - 分布式事务最终一致性（本地消息表 + 可靠消息 + 幂等消费）
> - 优惠券核销引擎（优惠券选择 + 预占 + 核销 + 退款回退）
> - 秒杀会场前后端联动（随机码、限流、MQ 削峰完整 Demo）
> - Sentinel 控制台接入、热点参数限流（秒杀接口）
>
> **预计迭代周期**：3–4 周
> **验收标准**：可录制「商品搜索 → 加购 → 结算 → 优惠券抵扣 → 锁定库存 → 支付宝支付 → 回调 → 我的订单 → 超时订单自动关闭并释放库存」完整视频 Demo。

### 2.1 P0 · 支付集成 🔲

| 任务 | 交付物 | 技术/模块 |
| :--- | :--- | :--- |
| 2.1.1 支付宝沙箱对接 | `mall-order` 新建 `AlipayTemplate` + 支付 Controller：生成支付表单/二维码、同步 returnUrl 回调、异步 notifyUrl 回调（验签、幂等、订单状态流转已支付） | Alipay SDK、RabbitMQ（支付成功通知→扣库存/优惠券二次确认） |
| 2.1.2 支付流水落库 & 退款 | `PaymentInfoEntity` 补全字段 + 状态流转；退款接口 `OrderRefundController` 调支付宝退款（用于后续售后） | mall-order oms_payment_info、oms_refund_info |
| 2.1.3 pay.html 收银台页面 | 接入支付宝按钮 + 支付二维码渲染、订单号/金额展示、轮询支付状态 | mall-order templates/pay.html 升级为真实收银台 |

### 2.2 P0 · 库存锁定 + 最终一致性 闭环 🔲

| 任务 | 交付物 | 技术/模块 |
| :--- | :--- | :--- |
| 2.2.1 submitOrder 中真正调用 ware Feign 锁定库存 | `OrderServiceImpl` 中增加 `wareFeign.orderLockStock(wareSkuLockVo)`；锁定失败则抛异常，事务回滚订单 | mall-order → mall-ware Feign `WareFeignService` |
| 2.2.2 库存锁定 + 工作单入库 | 完善 `WareSkuServiceImpl.orderLockStock()`：加 Redisson 锁 → 校验库存 → 扣 wms_ware_sku → 写 wms_ware_order_task + detail（带 task_status 锁定/已解锁） | mall-ware + Redisson |
| 2.2.3 幂等 + 防重复解锁 | `StockReleaseListener` 中判断 task_status；已解锁则不重复处理；关单消息在 order 侧做到「仅在未支付状态才关单」 | mall-order OrderCloseListener + mall-ware StockReleaseListener |
| 2.2.4 本地消息表替代可靠消息（可选增强） | 新建 `order_local_msg` 表，提交订单+消息同库事务落库，后台扫描投递 MQ，实现「下单→扣减消息」极端可靠 | mall-order + 事务 + 定时任务（学习本地消息表模式） |

### 2.3 P0 · 优惠券引擎真实接入 🔲

| 任务 | 交付物 | 技术/模块 |
| :--- | :--- | :--- |
| 2.3.1 结算页可选券展示与选择 | confirmOrder 中查询会员可用券 → 返回 confirmVo；confirm.html 支持选券、金额实时重算（JS） | mall-order + mall-coupon `CouponFeignService#getMemberAvailableCoupons` 新增 |
| 2.3.2 下单时优惠券预占 & 核销 | 提交订单时：校验券有效性 → 预占券（sms_coupon_history status=锁定）→ 订单落库 → 券改为已用；关单时退款回退券 | mall-order submitOrder + OrderCloseListener |
| 2.3.3 积分抵扣（可选） | 使用确认积分数量 → 扣 `ums_member.integration`；退款反还 | mall-member + mall-order |

### 2.4 P1 · 我的订单 / 订单详情 / 秒杀会场 页面 🔲

| 任务 | 交付物 | 技术/模块 |
| :--- | :--- | :--- |
| 2.4.1 我的订单列表页 | `MemberWebController` 分页查询当前登录会员订单（含订单项快照）→ `orderList.html` 前台页面；支持按状态筛选 | mall-member Feign→mall-order `OrderFeignService#memberList` |
| 2.4.2 订单详情页 | `OrderDetailController` /member/order/{orderSn} 展示订单、物流、支付信息、入口去支付/去退款 | mall-order templates/orderDetail.html |
| 2.4.3 秒杀会场页 `seckill.html` + 当前场次时间线 | 定时刷新秒杀列表、SKU 卡片带秒杀价、倒计时、开始/结束状态、「立即秒杀」按钮点击走 `/kill` 流程；成功跳订单/失败提示 | mall-seckill templates/seckill.html + JS 轮询 `getCurrentSeckillSkus` + `/sku/seckill/{skuId}` 取随机码 |
| 2.4.4 登录后临时购物车合并 | auth 登录成功后，取 Cookie user-key → 合并临时购物车到会员购物车；合并规则：同 SKU 叠加数量，选中状态取并集 | mall-auth-server 登录 success 分支 → mall-cart `CartService#mergeTempCart` 新增接口 |

### 2.5 P1 · Sentinel 控制台接入（秒杀限流 Demo）🔲

| 任务 | 交付物 | 技术 |
| :--- | :--- | :--- |
| 2.5.1 启动 Sentinel Dashboard | docker 或本地启动 1.8.0+ 控制台，各模块 `spring.cloud.sentinel.transport.dashboard` 配置指向 | Sentinel |
| 2.5.2 秒杀 /kill 接口热点参数限流 + 降级 | 配置对 skuId 的热点规则；`SeckillFeignServiceFallBack` 生效并返回友好降级页 | mall-seckill + Sentinel @SentinelResource |
| 2.5.3 登录/验证码接口限流防刷 | auth-server 的 `/sms/sendCode`、`/login` 增加限流规则；配合原有 Redis 60s 防刷形成双层防护 | mall-auth-server Sentinel |

### 2.6 v1.1.0 验收 checklist 🔲

- [ ] 结算页可看到「可用券列表」，点击选中后总价实时减少
- [ ] 提交订单 → ware 库存工作单被真实写入且 wms_ware_sku 库存减少对应数量
- [ ] pay.html 渲染支付宝支付二维码 → 扫码（沙箱买家）支付 → 异步回调 → 订单状态变为已支付
- [ ] 不支付等待 → 延时消息触发 → 订单自动关闭 → 库存 + 优惠券自动回退
- [ ] 秒杀会场页 `seckill.html` 可浏览当前场次 → 点击秒杀 → 在 MQ 消费成功后出现「秒杀成功」订单
- [ ] 我的订单列表页能看到历史订单、订单详情页能跳转去支付/去退款

---

## 三、v1.2.0（工程化可部署 + 可观测）🔲 规划中

> **目标版本**：生产级工程化能力 —— 调用链可视化、限流规则 Nacos 持久化、Docker 镜像与 Compose 一键启动；为后续上 Kubernetes 打基础。
> **核心学习目标**：
> - Sleuth + Zipkin 全链路追踪（网关→各服务→DB/MQ/ES，含异步线程 traceId 传递）
> - Sentinel 规则推送到 Nacos 持久化（避免控制台重启丢规则）
> - Spring Boot 应用 Docker 多阶段构建、镜像瘦身（JDK8 + jlink/Alpine 思路）
> - Docker Compose 端到端部署验证；本地/生产环境 profile 切换
> - 了解 CI 基础：GitHub Actions 跑 mvn test + 构建镜像
>
> **预计迭代周期**：3–4 周
> **验收标准**：
> 1. `docker compose up` 一条命令启动中间件 + 全部业务服务，访问 gulimall.com 首页成功
> 2. 下单一次 → Zipkin UI 能看到完整 网关→order→cart→member→ware 的跨服务调用链（含异步 MQ 传递 traceId）
> 3. 重启 Sentinel 控制台 → 限流规则仍存在（证明持久化生效）

### 3.1 可观测 · 链路追踪 🔲

| 任务 | 交付物 | 技术/模块 |
| :--- | :--- | :--- |
| 3.1.1 全模块加入 Sleuth + Zipkin 依赖与配置 | 所有业务模块 pom 加 `spring-cloud-starter-zipkin`；`spring.zipkin.base-url=http://zipkin:9411`；采样率 1.0；docker-compose.yml 增加 zipkin service（可用 zipkin:2.24） | Sleuth + Zipkin / 全部 12 模块 |
| 3.1.2 异步线程/线程池 traceId 传递 | `@Async`、`ThreadPoolTaskExecutor`、RabbitMQ 消费者（OrderCloseListener 等）装饰器继承 trace 上下文；否则调用链在 MQ 消费侧断掉 | Sleuth + MDC + 自定义线程池装饰器 |
| 3.1.3 日志文件规范 + traceId 打印 | 调整 logback-spring.xml pattern `[%X{traceId:-}/%X{spanId:-}]`；保证日志中可由 traceId 关联一次请求的全部跨服务日志 | 日志规范 |

### 3.2 可观测 · Sentinel 规则 Nacos 持久化 🔲

| 任务 | 交付物 | 技术 |
| :--- | :--- | :--- |
| 3.2.1 引入 Sentinel Datasource Nacos | pom 加 `sentinel-datasource-nacos`；配置 `spring.cloud.sentinel.datasource.ds.nacos.server-addr/data-id/group/rule-type`；Nacos 中新建 `sentinel-gulimall-flow-rules.json`、`sentinel-gulimall-degrade-rules.json`、`sentinel-gulimall-param-flow-rules.json` 配置 | Sentinel + Nacos datasource |
| 3.2.2 控制台改造（可选：双向同步 Push 模式） | 默认 Pull 模式仅从 Nacos→Sentinel；Push 模式改造 Sentinel Dashboard 从 Nacos 拉规则，编辑后回写（改 Dashboard 源码）—— 可选，Pull 模式也能完成学习 | Sentinel Dashboard Push 模式 |
| 3.2.3 验证：重启控制台规则不丢 | 写一套秒杀接口限流规则到 Nacos，重启 Dashboard，观察 mall-seckill 规则自动加载 | 自测验证 |

### 3.3 工程化 · Docker 镜像与 Compose 一键部署 🔲

| 任务 | 交付物 | 技术 |
| :--- | :--- | :--- |
| 3.3.1 各业务模块编写 Dockerfile | 使用多阶段构建：`mvn:3.8-openjdk-8` 先 `clean package -DskipTests`，再 `openjdk:8-jdk-alpine` 拷贝 jar、设置时区、设置 `SPRING_PROFILES_ACTIVE=prod`、`-Xms256m -Xmx512m` 起；mall-seckill、mall-common（父 pom）先 install | Dockerfile × 12 个模块 |
| 3.3.2 docker-compose.yml 新增全部业务服务 | 中间件 + 12 个 Java 服务；depends_on 按依赖顺序（nacos/mysql/redis/rabbit/es → product/order/…）；网络统一 `gulimall-gulimall-network`；Nacos 中命名空间配置 prod profile（服务之间使用容器名互调） | Docker Compose v3.8+ |
| 3.3.3 Nacos prod 命名空间一键导入 | `nacos/config/nacos_config_export_{namespaceId}.zip` 把各模块 prod yml 导出；README 增加「Import Config」步骤 | Nacos config 导出/导入 |
| 3.3.4 生产环境 hosts 与 Nginx | 新增 nginx.conf：gulimall.com → gateway:88；静态资源（/static）可由 Nginx 直接缓存 | Nginx |
| 3.3.5 CI 基础（可选）：GitHub Actions 流程 | `.github/workflows/maven.yml`：push → mvn clean test → 构建 12 个镜像 → push 到私有 registry（或本地 Docker Hub 个人空间） | GitHub Actions + Docker Registry |

### 3.4 v1.2.0 附加可选（会员中心完善）🔲

| 任务 | 交付物 |
| :--- | :--- |
| 3.4.1 我的优惠券 / 我的收藏 / 积分明细页 | `MemberCouponsController`、`MemberCollectController`、`MemberIntegrationLogController` + 对应 3 个模板页 |
| 3.4.2 个人资料修改页 | 修改昵称/头像/手机号、密码修改；敏感操作二次验证短信验证码 |

### 3.5 v1.2.0 验收 checklist 🔲

- [ ] `docker compose up -d` 等待 3–5 分钟 → `docker ps` 所有服务 healthy；浏览器直接访问 http://gulimall.com 首页成功
- [ ] 浏览商品 → 搜索 → 加购 → 下单 → 支付；打开 Zipkin UI（http://localhost:9411/zipkin/）按「服务名 + 时间范围」可查询出跨越 gateway/order/cart/member/ware 的完整 trace
- [ ] 关闭 Sentinel Dashboard 进程、重新启动 → mall-seckill 原有 `/kill` 热点参数限流规则仍生效（Nacos datasource 加载成功）
- [ ] 日志 `tail -f logs/mall-order.log` 每行输出 `[traceId/spanId]`，与 Zipkin 一一对应

---

## 四、跨版本不变：技术学习路线与代码规范

### 4.1 学习建议（贯穿 v1.0/v1.1/v1.2）

1. **每次改完代码，先写单元测试或写一个可复现的 curl 脚本**，存入 `script/{module}/curl_xxx.sh`，避免下次迭代忘记如何验证。
2. **跨服务调用 Bug 排查三步走**：先看请求有没有进网关 → 再看服务 Sleuth traceId → 最后看业务日志/异常堆栈；v1.2 接入 Zipkin 后会大幅简化。
3. **分布式事务必须先写失败场景**：下单→锁库存成功，但订单落库回滚 → 能否正确解锁？支付成功回调重复触发（网络重试）→ 能否幂等？**这些场景必须写 JUnit 集成测试**。
4. **Seata AT 模式作为后续拓展可选学习路径**（v1.3.0 待定）：v1.1.0 坚持本地消息表 + 最终一致性，更贴近真实业务也更通用。

### 4.2 代码规范（随 v1.1.0 起严格执行）

- 所有 public 类/接口/方法补 Javadoc（VO 字段补 /** 字段含义 */）
- 清理模板注释 `@Description/@Created/@createTime` 等无信息量内容
- 删除 `System.out.println` / `e.printStackTrace()` 调试代码，统一用 SLF4J log
- 跨模块 DTO 统一放 mall-common `to/` 目录，模块内部用 `vo/`
- 新增 MQ Topic / Queue / routingKey 统一到 `MQConstant.java`（可在 mall-common 新建），避免魔法字符串散落在各 Config

---

## 五、总览：从 v1.0 到 v1.2 的学习收获路径

| 能力维度 | v1.0（跑通） | v1.1（交易闭环） | v1.2（工程化） |
| :--- | :--- | :--- | :--- |
| 微服务治理 | Nacos 注册/配置、Feign、Gateway 路由 | Nacos 配置多环境（dev/prod）、Sentinel 限流实战 | Sentinel 规则持久化、Nacos 配置导入导出 |
| 分布式数据 | Spring Session Redis 共享、Redisson 锁 | 本地消息表 + RabbitMQ 最终一致性、分布式事务实战 | 链路追踪可视化（traceId 贯穿同步+异步） |
| 支付/交易 | 占位页 + 关单延时队列 | 支付宝沙箱全流程、验签/幂等、优惠券引擎 | 退款流程、订单全生命周期管理 |
| 电商业务 | 浏览/搜索/加购/结算基础 | 真实结算、秒杀会场、购物车合并 | 完整会员中心 |
| 可观测/运维 | 基础日志、容器化中间件 | Sentinel 控制台限流实战 | Zipkin 全链路追踪、Docker 全量镜像、Compose 一键部署 |

> 按照 v1.1 → v1.2 的顺序推进，每完成一个迭代即可输出一个独立的可演示 Demo（对应一个求职项目作品版本），简历中按版本线描述会比「功能清单罗列」更有说服力。

---

## 六、工程概述：当前代码组织深度审视（2026-07-28）

> **本节目的**：客观记录 v1.0.0 当前工程与代码的真实组织状况，识别"粗糙、不易理解"的根因，作为 v1.1.0 起工程治理的依据。所有结论均基于实际代码（非推测），关键证据以代码路径形式给出。
>
> **总体判断**：项目**业务功能完成度高，但工程规范度低**。这是一个典型的"教程式"代码库——为了讲清楚某个知识点（Feign、Seata、Redisson、RabbitMQ）而写，而不是按生产工程标准构建。这导致新人接手时需要大量上下文才能理解"为什么这样写"。

### 6.1 仓库整体结构问题

#### 6.1.1 顶层目录混乱，职责边界不清

仓库根目录同时混有：业务模块、配置、数据库脚本、Nginx、前端打包产物、启动脚本。

| 顶层目录 | 作用 | 问题 |
| :--- | :--- | :--- |
| `config/application-dev.yml` | 单一 dev 配置文件 | 顶层只放一份 dev 配置，与各模块 `src/main/resources/application*.yml` 重复，且无说明这份配置给谁用 |
| `db/*.sql` | 6 个库的建表脚本 | 与 `renren-generator` 的产物重叠；缺少 seed 数据与版本化迁移（Flyway/Liquibase） |
| `nginx/html/2607241922/` | renren-fast-vue 编译产物 | 前端打包产物不应进入后端仓库，应通过 CI 输出到部署目录 |
| `mysql/my.cnf` | MySQL 配置 | 单文件，缺少与其他中间件配置（redis.conf、rabbitmq.conf）的对称组织 |
| `gulimall-start.sh` | 启动脚本 | 单一脚本启动所有 Java 进程，无参数说明、无服务依赖编排（应交给 docker-compose） |
| `renren-generator/` | 代码生成器 | 是开发期工具，不应与运行时业务模块平级放在父 pom 的 `<modules>` 中 |

**根因**：仓库没有按"开发期工具 / 业务源码 / 部署产物"分层组织。

#### 6.1.2 父 pom 缺失依赖版本治理

[pom.xml](file:///Users/yongxu/Documents/learn/gulimall/pom.xml) 父 pom **仅声明 `<modules>` 与三个 java 版本属性**，未做任何依赖版本统一管理：

```xml
<properties>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <java.version>1.8</java.version>
</properties>
```

后果：
- `gulimall-common` 的 pom 中 `spring-cloud.version=Greenwich.SR3`，而项目技术文档声明的是 `Hoxton.SR3` —— **版本声明与文档不一致，且无人察觉**。
- 各模块各自管理依赖版本，例如 `gulimall-common/pom.xml` 中 `spring-cloud-alibaba-dependencies=2.1.0.RELEASE`，但 `gulimall-product` 等业务模块并未显式继承该 BOM，依赖版本由 Spring Boot 父 pom 间接解析，**同一份依赖在不同模块可能解析到不同版本**。
- `mybatis-plus` 在 common 中声明为 `3.2.0`，但项目技术文档写的是 `3.4.2` —— 文档与代码版本再次出现偏差。

### 6.2 模块内部包结构问题

#### 6.2.1 包命名不统一（最严重的"粗糙"证据）

同样是 Controller 层，不同模块用了**三种不同的包名**：

| 模块 | Controller 包名 | 入口类型 |
| :--- | :--- | :--- |
| [gulimall-product](file:///Users/yongxu/Documents/learn/gulimall/gulimall-product/src/main/java/com/xunqi/gulimall/product/app) | `app` | 后台 CRUD + 前台 |
| [gulimall-order](file:///Users/yongxu/Documents/learn/gulimall/gulimall-order/src/main/java/com/xunqi/gulimall/order/controller) | `controller` | 后台 CRUD |
| [gulimall-order/web](file:///Users/yongxu/Documents/learn/gulimall/gulimall-order/src/main/java/com/xunqi/gulimall/order/web) | `web` | 前台页面 Controller |
| [gulimall-member](file:///Users/yongxu/Documents/learn/gulimall/gulimall-member/src/main/java/com/xunqi/gulimall/member/controller) | `controller` | 后台 CRUD |
| [gulimall-member/web](file:///Users/yongxu/Documents/learn/gulimall/gulimall-member/src/main/java/com/xunqi/gulimall/member/web) | `web` | 前台页面 |
| [gulimall-auth-server](file:///Users/yongxu/Documents/learn/gulimall/gulimall-auth-server/src/main/java/com/xunqi/gulimall/auth/controller) | `controller` | 前后台混合 |

**product 模块用 `app`、order/member 用 `controller` + `web` 双包拆分、auth-server 又只有 `controller`** —— 同一工程三种分层约定。这直接导致 IDE 中按包名查找 Controller 失效，新人无法形成稳定心智模型。

#### 6.2.2 配置类重复且散落

每个业务模块都有 `config/` 包，且**几乎所有模块都重复定义以下 3 个配置类**：

| 配置类 | 作用 | 出现的模块 |
| :--- | :--- | :--- |
| `GulimallSessionConfig` | Spring Session Cookie 序列化 + Redis JSON 序列化 | product, order, member, cart, auth-server（5 份完全相同代码） |
| `Gulimall{Module}SentinelConfig` | Sentinel 限流降级返回 JSON | product, order, member, cart, auth-server, coupon（6 份几乎相同代码） |
| `MyThreadConfig` + `ThreadPoolConfigProperties` | 自定义线程池 | product, order, cart（3 份相同代码） |
| `MySeataConfig` | Seata 配置（实际未启用 Seata） | product, order, ware（3 份空壳配置） |

**根因**：这些配置应抽到 `gulimall-common` 作为 `@Configuration` 自动装配（`spring.factories` 或 `AutoConfiguration.imports`），各模块引入 common 即可复用，避免每次复制粘贴。

#### 6.2.3 `app` 包内 Controller 职责混杂

[gulimall-product/.../app/](file:///Users/yongxu/Documents/learn/gulimall/gulimall-product/src/main/java/com/xunqi/gulimall/product/app) 下 15 个 Controller 全平铺在一起，既有：
- 后台 CRUD（`SpuInfoController`、`BrandController`、`CategoryController`）
- 内部 Feign 接口（`SkuInfoController` 暴露 `/skuinfo/info/{skuId}` 给 cart 调用）
- 前台展示（但前台 Controller 又被拆到 `web/IndexController`、`web/ItemController`）

**一个包里混了 3 种角色的 Controller**，且 `web/` 与 `app/` 的拆分逻辑没有文档说明。新人看 `SkuInfoController` 时无法判断它是给后台 renren-vue 用、给 cart 做 Feign 调用、还是给前台用——实际上三者都有。

### 6.3 公共模块（gulimall-common）设计问题

#### 6.3.1 common 沦为"杂物间"

[gulimall-common/src/main/java/com/xunqi/common/](file:///Users/yongxu/Documents/learn/gulimall/gulimall-common/src/main/java/com/xunqi/common) 下混合了：

| 子包 | 内容 | 问题 |
| :--- | :--- | :--- |
| `es/SkuEsModel` | ES 索引模型 | 应属于 search 模块，放在 common 让 product 也能用，但破坏了 search 对 ES 模型的封装 |
| `to/` | 跨服务 DTO（OrderTo、SpuBoundTo、SkuReductionTo 等） | 命名混乱：有 `to/OrderTo.java`，也有 `to/mq/StockLockedTo.java`，MQ 专用 TO 单独建子包但非 MQ 的 TO 又平铺 |
| `to/MemberPrice.java` | 会员价 DTO | 应属于 member 模块，却出现在 common |
| `utils/R.java` | 统一响应 | R 继承自 `HashMap<String,Object>` —— **类型不安全**，`getData(key, typeReference)` 通过 fastjson 二次序列化反推类型，是反模式 |
| `valid/` | JSR-303 自定义校验注解 | 与 `xss/`（XSS 过滤工具）混在一起，两者完全无关 |
| `constant/` | 常量类 | 4 个模块常量类（AuthServerConstant、CartConstant、ProductConstant、WareConstant）平铺，未按模块分子包 |
| `exception/BizCodeEnum` | 错误码枚举 | 错误码注释声明"前两位表示业务场景"，但 `TO_MANY_REQUEST=10002` 与 `SMS_CODE_EXCEPTION=10002` **错误码冲突**，无人发现 |

#### 6.3.2 common 携带了不应有的运行时依赖

[gulimall-common/pom.xml](file:///Users/yongxu/Documents/learn/gulimall/gulimall-common/pom.xml) 把以下依赖**强加给所有引用方**：

- `spring-cloud-starter-alibaba-nacos-discovery` —— 网关、第三方服务都被迫注册到 Nacos
- `spring-cloud-starter-alibaba-sentinel` —— 所有模块都被迫接入 Sentinel
- `spring-cloud-starter-zipkin` —— **声明了但配置文件中 base-url 指向 `192.168.77.130:9411`（一台不存在的机器）**，且与 TodoList §3.1.1 记录的"Sleuth/Zipkin 未实际集成"矛盾。实际是 common 引入依赖、product 的 application.properties 配了地址，但其他模块没配，导致**部分模块带 zipkin 依赖但无配置，部分模块带配置但依赖未生效**。
- `mysql-connector-java` —— 网关、auth-server、cart、search 不连 MySQL，却被强加了驱动
- `mybatis-plus-boot-starter` —— 同上，不连 DB 的模块也被强加
- `servlet-api 2.5`（provided）—— 与 Spring Boot 内嵌 Tomcat 的 servlet-api 版本冲突风险

**根因**：common 没有按"是否需要 DB / 是否需要服务治理 / 是否需要 Web"拆分。生产级做法是 common 拆为 `common-core`（纯工具+R）、`common-web`（Session、异常处理、Sentinel）、`common-db`（MyBatis-Plus、MySQL）、`common-cloud`（Nacos、Feign 配置），各模块按需引入。

### 6.4 配置文件组织问题

#### 6.4.1 每个模块三个配置文件并存且职责不清

每个业务模块 `src/main/resources/` 下都有：
- `application.yml` —— 本地配置（端口、数据源、Redis）
- `application.properties` —— **又一份本地配置**（线程池参数、zipkin 地址、缓存配置）
- `bootstrap.yml` —— Nacos 配置中心连接

**问题**：`application.yml` 和 `application.properties` 同时存在是 Spring Boot 反模式。两者都会被加载，优先级为 `properties > yml`，但人类阅读时很难在脑中合并两份配置。例如 [gulimall-product](file:///Users/yongxu/Documents/learn/gulimall/gulimall-product/src/main/resources/application.properties) 的 zipkin 配置写在 `.properties` 里，而 [application.yml](file:///Users/yongxu/Documents/learn/gulimall/gulimall-product/src/main/resources/application.yml) 里完全没提 zipkin —— 读 yml 时会误以为没接 zipkin。

#### 6.4.2 Nacos 命名空间 ID 硬编码且无说明

[bootstrap.yml](file:///Users/yongxu/Documents/learn/gulimall/gulimall-product/src/main/resources/bootstrap.yml) 中：

```yaml
namespace: 67ee5d01-6af9-4aa6-a2e8-936d5f6fa67f
```

每个模块一个不同的 UUID 命名空间，但：
- 仓库中没有任何文档记录这些命名空间 ID 的对应关系（技术文档 §6.1 列了 4 个，但与实际代码不一致）
- 切换环境（dev→prod）需要改 bootstrap.yml，无法通过 profile 覆盖（因为 Nacos 配置在 bootstrap 阶段加载，早于 application profile）

#### 6.4.3 Sentinel 控制台地址全模块写死 `localhost:8080`

所有模块的 `application.yml` 都有：

```yaml
sentinel:
  transport:
    dashboard: localhost:8080
```

但 `localhost:8080` 实际是 renren-fast 后台的端口，**Sentinel 控制台默认端口是 8858**。这是复制粘贴遗留 bug，无人验证过 Sentinel 是否真的接上。

#### 6.4.4 Seata 配置文件残留

`gulimall-product`、`gulimall-order`、`gulimall-ware` 的 `src/main/resources/` 下都有 `file.conf` 和 `registry.conf`（Seata 客户端配置文件），但：
- 项目从未真正集成 Seata（TodoList §3 已确认）
- pom 中 `MySeataConfig` 是空壳
- 这些文件是早期教程跟做时留下的，**未被清理**

### 6.5 代码层面的"粗糙"细节

#### 6.5.1 ThreadLocal 未清理，存在内存泄漏风险

[OrderServiceImpl.java:62](file:///Users/yongxu/Documents/learn/gulimall/gulimall-order/src/main/java/com/xunqi/gulimall/order/service/impl/OrderServiceImpl.java#L62)：

```java
private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();
```

`submitOrder()` 中 `confirmVoThreadLocal.set(vo)`，但**整个方法没有 `finally { confirmVoThreadLocal.remove(); }`**。在线程池复用场景下，上次请求的 `OrderSubmitVo` 会残留在线程中，导致：
1. 内存泄漏（大对象不释放）
2. 下次请求若读到脏数据，可能产生业务 bug

同样问题见 [LoginUserInterceptor.java](file:///Users/yongxu/Documents/learn/gulimall/gulimall-member/src/main/java/com/xunqi/gulimall/member/interceptor/LoginUserInterceptor.java) 的 `public static ThreadLocal<MemberResponseVo> loginUser` —— `afterCompletion` 是空的，没有 `loginUser.remove()`。

#### 6.5.2 异步任务异常被吞，链路追踪断裂

[OrderServiceImpl.java:120-157](file:///Users/yongxu/Documents/learn/gulimall/gulimall-order/src/main/java/com/xunqi/gulimall/order/service/impl/OrderServiceImpl.java#L120-157) 的 `confirmOrder()`：

```java
CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
    RequestContextHolder.setRequestAttributes(requestAttributes);
    List<MemberAddressVo> address = memberFeignService.getAddress(memberResponseVo.getId());
    confirmVo.setMemberAddressVos(address);
}, threadPoolExecutor);
// ...
CompletableFuture.allOf(addressFuture, cartInfoFuture).get();
```

**问题**：
1. `runAsync` 内部 Feign 调用若抛异常，`CompletableFuture` 会记录异常但 `get()` 不抛（因为用的是 `allOf().get()` 而非 `future.get()`），**异常被静默吞掉**，confirmVo 中对应字段为 null，前端显示空但不报错。
2. `RequestContextHolder.setRequestAttributes(requestAttributes)` 在异步线程中设置后**从未清理**，同样是 ThreadLocal 泄漏。
3. Feign 调用丢失请求头的修复方式是手动透传 RequestAttributes，但**没有用 TransmittableThreadLocal**，在线程池复用场景下不可靠。

#### 6.5.3 `R` 类的类型不安全设计

[R.java](file:///Users/yongxu/Documents/learn/gulimall/gulimall-common/src/main/java/com/xunqi/common/utils/R.java) 继承 `HashMap<String, Object>`：

```java
public class R extends HashMap<String, Object> {
    public <T> T getData(TypeReference<T> typeReference) {
        Object data = get("data");  // 默认是 map
        String jsonString = JSON.toJSONString(data);
        T t = JSON.parseObject(jsonString, typeReference);
        return t;
    }
}
```

**问题**：
1. `R` 是个 Map，调用方可以 `r.put("任意key", 任意value)`，无任何约束。代码中随处可见 `r.get("msg")` 强转 String，类型完全靠约定。
2. `getData` 通过 **JSON 序列化再反序列化** 来转换类型，性能差且容易丢失精度（如 BigDecimal、Date）。
3. `getCode()` 返回 `Integer` 但调用方经常用 `r.getCode() == 0` 比较 —— 若 code 为 null 会 NPE。

正确做法是用泛型 `R<T>` + 明确的字段（code、msg、data），而不是继承 HashMap。

#### 6.5.4 Feign 接口命名与拼写错误

- [ThridFeignService.java](file:///Users/yongxu/Documents/learn/gulimall/gulimall-order/src/main/java/com/xunqi/gulimall/order/feign/ThridFeignService.java) —— `Thrid` 应为 `Third`
- [SeckillFeignService.java:18](file:///Users/yongxu/Documents/learn/gulimall/gulimall-product/src/main/java/com/xunqi/gulimall/product/feign/SeckillFeignService.java#L18) —— 方法名 `getSkuSeckilInfo` 少了一个 `l`，应为 `getSkuSeckillInfo`
- Feign 客户端命名不统一：`WmsFeignService`（缩写）、`CartFeignService`（全称）、`ThridFeignService`（拼写错误+缩写）

#### 6.5.5 硬编码的魔法值散落业务代码

[OrderServiceImpl.java:196](file:///Users/yongxu/Documents/learn/gulimall/gulimall-order/src/main/java/com/xunqi/gulimall/order/service/impl/OrderServiceImpl.java#L196)：

```java
String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
```

Lua 脚本硬编码在方法体内，应抽到常量或资源文件。同方法中：
- `redisTemplate.opsForValue().set(USER_ORDER_TOKEN_PREFIX+..., token, 30, TimeUnit.MINUTES)` —— 30 分钟硬编码
- `rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", ...)` —— exchange 名和 routing key 硬编码字符串
- `Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01` —— 0.01 元的精度容差硬编码

#### 6.5.6 注释与代码不一致（已部分修复）

虽然上一轮已清理 auth-server 和 member 模块的模板注释，但其他模块仍存在：
- `OrderServiceImpl` 中 `// int i = 10/0;`（调试代码残留）
- `// responseVo.setCode(3); return responseVo;`（被注释掉的旧逻辑）
- `//TODO 3、保存订单`（TODO 但代码已实现）
- `//TODO 调用远程锁定库存的方法`（TODO 但下一行就是 `wmsFeignService.orderLockStock(lockVo)`）

这些过时注释会误导读者以为功能未实现。

### 6.6 前端资源组织问题

#### 6.6.1 静态资源路径不统一

| 模块 | 静态资源根路径 | 说明 |
| :--- | :--- | :--- |
| product | `static/index/`、`static/item/` | 按页面分目录 |
| search | `static/list/` | 按页面分目录 |
| cart | `static/cart/` | 按页面分目录 |
| auth-server | `static/JD_img/`、`static/auth/` | **图片直接平铺在 static 根下**，且目录名 `JD_img` 暗示是从京东扒的图 |
| member | 无 static | 模板 `orderList.html` 引用的静态资源靠其他模块提供 |

**问题**：静态资源路径无统一规范，导致 Nginx 缓存规则、网关静态资源路由都难以统一配置。`JD_img` 这种命名不应出现在生产代码中。

#### 6.6.2 Thymeleaf 模板与后端强耦合

每个有前台页面的模块都在 `src/main/resources/templates/` 下放 HTML，导致：
- 前端改动需要重新编译部署后端模块
- 无法独立部署前端
- 页面散落在 7 个模块中，无法统揽"商城到底有多少页面"

这是教程式项目的典型特征——为了讲 Thymeleaf + Spring MVC，牺牲了前后端分离。

### 6.7 测试与质量保障完全缺失

- **全仓库无任何单元测试或集成测试**（除 `gulimall-test-sso-client` 的 `ApplicationTests` 空壳）
- 无 CI 配置（`.github/workflows/` 不存在）
- 无代码质量检查（无 checkstyle/spotbugs/pmd 配置）
- 无 Swagger/OpenAPI 文档（接口全靠读代码）

这意味着每次改动的正确性完全依赖手动验证，回归风险极高。

### 6.8 工程治理改进建议（纳入 v1.1.0 起的迭代）

| 优先级 | 改进项 | 目标 | 建议版本 |
| :--- | :--- | :--- | :--- |
| 🔴 P0 | 父 pom 统一依赖版本管理（Spring Boot BOM + Spring Cloud BOM + 自定义 properties） | 消除版本漂移 | v1.1.0 起步 |
| 🔴 P0 | 拆分 common 为 common-core / common-web / common-db / common-cloud | 消除强制依赖 | v1.1.0 |
| 🔴 P0 | 统一 Controller 包名为 `controller`（后台）+ `web`（前台），废弃 `app` | 统一分层心智 | v1.1.0 |
| 🟠 P1 | 配置类抽取到 common-web 自动装配（Session/Sentinel/线程池） | 消除 6 份重复代码 | v1.1.0 |
| 🟠 P1 | 合并 application.yml + application.properties 为单文件 | 消除配置分裂 | v1.1.0 |
| 🟠 P1 | 修复 ThreadLocal 泄漏（finally remove + afterCompletion remove） | 消除内存泄漏 | v1.1.0 |
| 🟠 P1 | 清理 Seata file.conf/registry.conf 残留 + zipkin 无效配置 | 消除误导 | v1.1.0 |
| 🟡 P2 | `R` 类重构为泛型 `R<T>` | 类型安全 | v1.2.0（影响面大） |
| 🟡 P2 | 静态资源统一到 `static/{module}/` 规范 + 重命名 `JD_img` | 路径统一 | v1.2.0 |
| 🟡 P2 | 引入 Flyway 管理数据库版本 | 可追溯迁移 | v1.2.0 |
| 🟡 P2 | renren-generator 移出父 pom modules，作为独立工具项目 | 职责清晰 | v1.2.0 |
| 🟢 P3 | 补充核心 Service 的单元测试（OrderService.submitOrder、StockReleaseListener 等） | 回归保障 | v1.2.0 |
| 🟢 P3 | 引入 checkstyle + spotbugs + CI | 自动质量门禁 | v1.2.0 |

### 6.9 工程概述小结

当前项目的代码组织问题可以归纳为**"三个不统一"**：

1. **命名不统一**：包名（app/controller/web）、Feign 命名（Wms/Cart/Thrid）、错误码（冲突的 10002）、静态资源路径（JD_img/index/list/cart）。
2. **配置不统一**：yml 与 properties 并存、Nacos 命名空间硬编码、Sentinel 地址写死且错误、Seata 残留文件未清理。
3. **职责不统一**：common 携带所有依赖、product 的 app 包混三种 Controller、模板散落在 7 个模块。

这些问题的根因是**项目按"知识点跟练"方式构建，而非按"工程产品"标准构建**。作为学习项目，这并不影响功能验证；但若要作为求职作品或团队协作基线，建议在 v1.1.0 迭代中优先处理标红的 P0 项（父 pom 版本管理 + common 拆分 + Controller 包统一），这三项改动成本低但收益最大，能显著提升项目的"第一印象专业度"。
