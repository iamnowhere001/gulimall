-- 商品系统按钮权限
-- 分类维护 (menu_id=32)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(32, '查看', NULL, 'product:category:list,product:category:info', 2, NULL, 1),
(32, '新增', NULL, 'product:category:save', 2, NULL, 2),
(32, '修改', NULL, 'product:category:update', 2, NULL, 3),
(32, '删除', NULL, 'product:category:delete', 2, NULL, 4);

-- 品牌管理 (menu_id=34)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(34, '查看', NULL, 'product:brand:list,product:brand:info', 2, NULL, 1),
(34, '新增', NULL, 'product:brand:save', 2, NULL, 2),
(34, '修改', NULL, 'product:brand:update', 2, NULL, 3),
(34, '删除', NULL, 'product:brand:delete', 2, NULL, 4),
(34, '修改状态', NULL, 'product:brand:updateStatus', 2, NULL, 5);

-- 属性分组 (menu_id=38)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(38, '查看', NULL, 'product:attrgroup:list,product:attrgroup:info', 2, NULL, 1),
(38, '新增', NULL, 'product:attrgroup:save', 2, NULL, 2),
(38, '修改', NULL, 'product:attrgroup:update', 2, NULL, 3),
(38, '删除', NULL, 'product:attrgroup:delete', 2, NULL, 4);

-- 规格参数 (menu_id=39)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(39, '查看', NULL, 'product:attr:list,product:attr:info', 2, NULL, 1),
(39, '新增', NULL, 'product:attr:save', 2, NULL, 2),
(39, '修改', NULL, 'product:attr:update', 2, NULL, 3),
(39, '删除', NULL, 'product:attr:delete', 2, NULL, 4);

-- 销售属性 (menu_id=40)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(40, '查看', NULL, 'product:saleattr:list,product:saleattr:info', 2, NULL, 1),
(40, '新增', NULL, 'product:saleattr:save', 2, NULL, 2),
(40, '修改', NULL, 'product:saleattr:update', 2, NULL, 3),
(40, '删除', NULL, 'product:saleattr:delete', 2, NULL, 4);

-- spu管理 (menu_id=68)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(68, '查看', NULL, 'product:spu:list,product:spu:info', 2, NULL, 1),
(68, '新增', NULL, 'product:spu:save', 2, NULL, 2),
(68, '修改', NULL, 'product:spu:update', 2, NULL, 3),
(68, '删除', NULL, 'product:spu:delete', 2, NULL, 4);

-- 发布商品 (menu_id=69)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(69, '查看', NULL, 'product:spuadd:list,product:spuadd:info', 2, NULL, 1),
(69, '新增', NULL, 'product:spuadd:save', 2, NULL, 2),
(69, '修改', NULL, 'product:spuadd:update', 2, NULL, 3),
(69, '删除', NULL, 'product:spuadd:delete', 2, NULL, 4);

-- 商品管理 (menu_id=73)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(73, '查看', NULL, 'product:manager:list,product:manager:info', 2, NULL, 1),
(73, '新增', NULL, 'product:manager:save', 2, NULL, 2),
(73, '修改', NULL, 'product:manager:update', 2, NULL, 3),
(73, '删除', NULL, 'product:manager:delete', 2, NULL, 4);

-- 优惠营销按钮权限
-- 优惠券管理 (menu_id=47)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(47, '查看', NULL, 'coupon:coupon:list,coupon:coupon:info', 2, NULL, 1),
(47, '新增', NULL, 'coupon:coupon:save', 2, NULL, 2),
(47, '修改', NULL, 'coupon:coupon:update', 2, NULL, 3),
(47, '删除', NULL, 'coupon:coupon:delete', 2, NULL, 4);

-- 发放记录 (menu_id=48)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(48, '查看', NULL, 'coupon:history:list,coupon:history:info', 2, NULL, 1),
(48, '新增', NULL, 'coupon:history:save', 2, NULL, 2),
(48, '修改', NULL, 'coupon:history:update', 2, NULL, 3),
(48, '删除', NULL, 'coupon:history:delete', 2, NULL, 4);

-- 专题活动 (menu_id=49)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(49, '查看', NULL, 'coupon:subject:list,coupon:subject:info', 2, NULL, 1),
(49, '新增', NULL, 'coupon:homesubject:save', 2, NULL, 2),
(49, '修改', NULL, 'coupon:homesubject:update', 2, NULL, 3),
(49, '删除', NULL, 'coupon:homesubject:delete', 2, NULL, 4);

-- 秒杀活动 (menu_id=50)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(50, '查看', NULL, 'coupon:seckill:list,coupon:seckill:info', 2, NULL, 1),
(50, '新增', NULL, 'coupon:seckillpromotion:save', 2, NULL, 2),
(50, '修改', NULL, 'coupon:seckillpromotion:update', 2, NULL, 3),
(50, '删除', NULL, 'coupon:seckillpromotion:delete', 2, NULL, 4);

-- 积分维护 (menu_id=51)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(51, '查看', NULL, 'coupon:bounds:list,coupon:bounds:info', 2, NULL, 1),
(51, '新增', NULL, 'coupon:spubounds:save', 2, NULL, 2),
(51, '修改', NULL, 'coupon:spubounds:update', 2, NULL, 3),
(51, '删除', NULL, 'coupon:spubounds:delete', 2, NULL, 4);

-- 满减折扣 (menu_id=52)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(52, '查看', NULL, 'coupon:full:list,coupon:full:info', 2, NULL, 1),
(52, '新增', NULL, 'coupon:skufullreduction:save', 2, NULL, 2),
(52, '修改', NULL, 'coupon:skufullreduction:update', 2, NULL, 3),
(52, '删除', NULL, 'coupon:skufullreduction:delete', 2, NULL, 4);

-- 会员价格 (menu_id=74)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(74, '查看', NULL, 'coupon:memberprice:list,coupon:memberprice:info', 2, NULL, 1),
(74, '新增', NULL, 'coupon:memberprice:save', 2, NULL, 2),
(74, '修改', NULL, 'coupon:memberprice:update', 2, NULL, 3),
(74, '删除', NULL, 'coupon:memberprice:delete', 2, NULL, 4);

-- 每日秒杀 (menu_id=75)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(75, '查看', NULL, 'coupon:seckillsession:list,coupon:seckillsession:info', 2, NULL, 1),
(75, '新增', NULL, 'coupon:seckillsession:save', 2, NULL, 2),
(75, '修改', NULL, 'coupon:seckillsession:update', 2, NULL, 3),
(75, '删除', NULL, 'coupon:seckillsession:delete', 2, NULL, 4);

-- 库存系统按钮权限
-- 仓库维护 (menu_id=53)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(53, '查看', NULL, 'ware:wareinfo:list,ware:wareinfo:info', 2, NULL, 1),
(53, '新增', NULL, 'ware:wareinfo:save', 2, NULL, 2),
(53, '修改', NULL, 'ware:wareinfo:update', 2, NULL, 3),
(53, '删除', NULL, 'ware:wareinfo:delete', 2, NULL, 4);

-- 库存工作单 (menu_id=54)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(54, '查看', NULL, 'ware:task:list,ware:task:info', 2, NULL, 1),
(54, '新增', NULL, 'ware:wareordertask:save', 2, NULL, 2),
(54, '修改', NULL, 'ware:wareordertask:update', 2, NULL, 3),
(54, '删除', NULL, 'ware:wareordertask:delete', 2, NULL, 4);

-- 商品库存 (menu_id=55)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(55, '查看', NULL, 'ware:sku:list,ware:sku:info', 2, NULL, 1),
(55, '新增', NULL, 'ware:waresku:save', 2, NULL, 2),
(55, '修改', NULL, 'ware:waresku:update', 2, NULL, 3),
(55, '删除', NULL, 'ware:waresku:delete', 2, NULL, 4);

-- 采购需求 (menu_id=71)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(71, '查看', NULL, 'ware:purchaseitem:list,ware:purchaseitem:info', 2, NULL, 1),
(71, '新增', NULL, 'ware:purchasedetail:save', 2, NULL, 2),
(71, '修改', NULL, 'ware:purchasedetail:update', 2, NULL, 3),
(71, '删除', NULL, 'ware:purchasedetail:delete', 2, NULL, 4);

-- 采购单 (menu_id=72)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(72, '查看', NULL, 'ware:purchase:list,ware:purchase:info', 2, NULL, 1),
(72, '新增', NULL, 'ware:purchase:save', 2, NULL, 2),
(72, '修改', NULL, 'ware:purchase:update', 2, NULL, 3),
(72, '删除', NULL, 'ware:purchase:delete', 2, NULL, 4);

-- 订单系统按钮权限
-- 订单查询 (menu_id=56)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(56, '查看', NULL, 'order:order:list,order:order:info', 2, NULL, 1),
(56, '新增', NULL, 'order:order:save', 2, NULL, 2),
(56, '修改', NULL, 'order:order:update', 2, NULL, 3),
(56, '删除', NULL, 'order:order:delete', 2, NULL, 4);

-- 退货单处理 (menu_id=57)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(57, '查看', NULL, 'order:return:list,order:return:info', 2, NULL, 1),
(57, '新增', NULL, 'order:orderreturnapply:save', 2, NULL, 2),
(57, '修改', NULL, 'order:orderreturnapply:update', 2, NULL, 3),
(57, '删除', NULL, 'order:orderreturnapply:delete', 2, NULL, 4);

-- 等级规则 (menu_id=58)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(58, '查看', NULL, 'order:settings:list,order:settings:info', 2, NULL, 1),
(58, '新增', NULL, 'order:ordersetting:save', 2, NULL, 2),
(58, '修改', NULL, 'order:ordersetting:update', 2, NULL, 3),
(58, '删除', NULL, 'order:ordersetting:delete', 2, NULL, 4);

-- 支付流水查询 (menu_id=59)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(59, '查看', NULL, 'order:payment:list,order:payment:info', 2, NULL, 1),
(59, '新增', NULL, 'order:paymentinfo:save', 2, NULL, 2),
(59, '修改', NULL, 'order:paymentinfo:update', 2, NULL, 3),
(59, '删除', NULL, 'order:paymentinfo:delete', 2, NULL, 4);

-- 退款流水查询 (menu_id=60)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(60, '查看', NULL, 'order:refund:list,order:refund:info', 2, NULL, 1),
(60, '新增', NULL, 'order:refundinfo:save', 2, NULL, 2),
(60, '修改', NULL, 'order:refundinfo:update', 2, NULL, 3),
(60, '删除', NULL, 'order:refundinfo:delete', 2, NULL, 4);

-- 用户系统按钮权限
-- 会员列表 (menu_id=61)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(61, '查看', NULL, 'member:member:list,member:member:info', 2, NULL, 1),
(61, '新增', NULL, 'member:member:save', 2, NULL, 2),
(61, '修改', NULL, 'member:member:update', 2, NULL, 3),
(61, '删除', NULL, 'member:member:delete', 2, NULL, 4);

-- 会员等级 (menu_id=62)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(62, '查看', NULL, 'member:level:list,member:level:info', 2, NULL, 1),
(62, '新增', NULL, 'member:memberlevel:save', 2, NULL, 2),
(62, '修改', NULL, 'member:memberlevel:update', 2, NULL, 3),
(62, '删除', NULL, 'member:memberlevel:delete', 2, NULL, 4);

-- 积分变化 (menu_id=63)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(63, '查看', NULL, 'member:growth:list,member:growth:info', 2, NULL, 1),
(63, '新增', NULL, 'member:growth:save', 2, NULL, 2),
(63, '修改', NULL, 'member:growth:update', 2, NULL, 3),
(63, '删除', NULL, 'member:growth:delete', 2, NULL, 4);

-- 统计信息 (menu_id=64)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(64, '查看', NULL, 'member:statistics:list,member:statistics:info', 2, NULL, 1),
(64, '新增', NULL, 'member:statistics:save', 2, NULL, 2),
(64, '修改', NULL, 'member:statistics:update', 2, NULL, 3),
(64, '删除', NULL, 'member:statistics:delete', 2, NULL, 4);

-- 内容管理按钮权限
-- 首页推荐 (menu_id=65)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(65, '查看', NULL, 'content:index:list,content:index:info', 2, NULL, 1),
(65, '新增', NULL, 'content:index:save', 2, NULL, 2),
(65, '修改', NULL, 'content:index:update', 2, NULL, 3),
(65, '删除', NULL, 'content:index:delete', 2, NULL, 4);

-- 分类热门 (menu_id=66)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(66, '查看', NULL, 'content:category:list,content:category:info', 2, NULL, 1),
(66, '新增', NULL, 'content:category:save', 2, NULL, 2),
(66, '修改', NULL, 'content:category:update', 2, NULL, 3),
(66, '删除', NULL, 'content:category:delete', 2, NULL, 4);

-- 评论管理 (menu_id=67)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(67, '查看', NULL, 'content:comments:list,content:comments:info', 2, NULL, 1),
(67, '新增', NULL, 'content:comments:save', 2, NULL, 2),
(67, '修改', NULL, 'content:comments:update', 2, NULL, 3),
(67, '删除', NULL, 'content:comments:delete', 2, NULL, 4);

-- 系统管理按钮权限补充
-- 文件上传 (menu_id=30)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(30, '查看', NULL, 'sys:oss:list,sys:oss:info', 2, NULL, 1),
(30, '新增', NULL, 'sys:oss:save', 2, NULL, 2),
(30, '修改', NULL, 'sys:oss:update', 2, NULL, 3),
(30, '删除', NULL, 'sys:oss:delete', 2, NULL, 4);

-- 参数管理 (menu_id=27)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(27, '查看', NULL, 'sys:config:list,sys:config:info', 2, NULL, 1),
(27, '新增', NULL, 'sys:config:save', 2, NULL, 2),
(27, '修改', NULL, 'sys:config:update', 2, NULL, 3),
(27, '删除', NULL, 'sys:config:delete', 2, NULL, 4);

-- 系统日志 (menu_id=29)
INSERT INTO `sys_menu` (`parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`) VALUES 
(29, '查看', NULL, 'sys:log:list,sys:log:info', 2, NULL, 1);