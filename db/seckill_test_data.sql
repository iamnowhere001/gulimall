-- =============================================
-- 谷粒商城 - 秒杀模块测试数据（修正版）
-- =============================================

-- 清理旧数据
DELETE FROM gulimall_sms.sms_seckill_sku_relation;
DELETE FROM gulimall_sms.sms_seckill_session;
DELETE FROM gulimall_sms.sms_seckill_promotion;

-- =============================================
-- 1. 秒杀活动
-- =============================================
INSERT INTO gulimall_sms.sms_seckill_promotion (id, title, start_time, end_time, status, create_time, user_id)
VALUES
(1, '暑期大促秒杀活动', DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1, NOW(), 1);

-- =============================================
-- 2. 秒杀活动场次
-- 调整时间确保有正在进行和即将开始的场次
-- =============================================
INSERT INTO gulimall_sms.sms_seckill_session (id, name, start_time, end_time, status, create_time)
VALUES
-- 进行中场次：当前时间-2小时 ~ 当前时间+30分钟
(1, '热门进行场', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 30 MINUTE), 1, NOW()),
-- 即将开始场：当前时间+30分钟 ~ 当前时间+5小时
(2, '即将开场1', DATE_ADD(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 5 HOUR), 1, NOW()),
-- 晚场
(3, '晚间专场', TIMESTAMP(CURDATE(), '19:00:00'), TIMESTAMP(CURDATE(), '23:59:59'), 1, NOW()),
-- 明天场次
(4, '明日0点场', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '00:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00'), 1, NOW()),
(5, '明日10点场', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '18:00:00'), 1, NOW()),
-- 今天上午已结束场次（用于测试历史数据）
(6, '上午结束场', TIMESTAMP(CURDATE(), '08:00:00'), DATE_SUB(NOW(), INTERVAL 1 HOUR), 1, NOW()),
-- 后天场次
(7, '后日专场', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '18:00:00'), 1, NOW());

-- =============================================
-- 3. 秒杀活动商品关联
-- 实际sku对应（从pms_sku_info查询）：
--   sku_id=1  华为Mate30 Pro 星河银 8+256G  原价6299
--   sku_id=2  华为Mate30 Pro 星河银 8+128G  原价5799
--   sku_id=3  华为Mate30 Pro 亮黑色 8+256G  原价6299
--   sku_id=4  华为Mate30 Pro 亮黑色 8+128G  原价5799
--   sku_id=5  华为Mate30 Pro 翡冷翠 8+256G  原价6299
--   sku_id=6  华为Mate30 Pro 翡冷翠 8+128G  原价5799
--   sku_id=7  华为Mate30 Pro 罗兰紫 8+256G  原价6299
--   sku_id=8  华为Mate30 Pro 罗兰紫 8+128G  原价5799
--   sku_id=9  Apple iPhone 11 黑色 128G    原价5999
--   sku_id=10 Apple iPhone 11 黑色 256G    原价6799
--   sku_id=11 Apple iPhone 11 黑色 64G     原价5499
--   sku_id=12 Apple iPhone 11 白色 128G    原价5999
--   sku_id=13 Apple iPhone 11 白色 256G    原价6799
--   sku_id=14 Apple iPhone 11 白色 64G     原价5499
--   sku_id=15 Apple iPhone 11 绿色 128G    原价5999
-- 秒杀价约为原价的 55%~70%
-- =============================================

-- 进行中场次 (session_id=1)  6款商品：华为4款 + iPhone2款
INSERT INTO gulimall_sms.sms_seckill_sku_relation (id, promotion_id, promotion_session_id, sku_id, seckill_price, seckill_count, seckill_limit, seckill_sort)
VALUES
(1,  1, 1, 1,  3999.00, 100, 2, 1),  -- 华为Mate30Pro 8+256G 原价6299 秒杀6.3折
(2,  1, 1, 2,  3699.00, 150, 2, 2),  -- 华为Mate30Pro 8+128G 原价5799 秒杀6.4折
(3,  1, 1, 4,  3599.00, 120, 2, 3),  -- 华为Mate30Pro亮黑8+128G 原价5799 秒杀6.2折
(4,  1, 1, 7,  3999.00, 80,  1, 4),  -- 华为Mate30Pro罗兰紫8+256G 原价6299
(5,  1, 1, 9,  3499.00, 200, 2, 5),  -- iPhone11 黑128G 原价5999 秒杀5.8折
(6,  1, 1, 11, 3199.00, 180, 2, 6);  -- iPhone11 黑64G 原价5499 秒杀5.8折

-- 即将开始场 (session_id=2) 6款商品
INSERT INTO gulimall_sms.sms_seckill_sku_relation (id, promotion_id, promotion_session_id, sku_id, seckill_price, seckill_count, seckill_limit, seckill_sort)
VALUES
(7,  1, 2, 3,  3999.00, 80,  1, 1),  -- 华为Mate30Pro亮黑8+256G 原价6299
(8,  1, 2, 5,  3999.00, 80,  1, 2),  -- 华为Mate30Pro翡冷翠8+256G
(9,  1, 2, 6,  3699.00, 100, 2, 3),  -- 华为Mate30Pro翡冷翠8+128G
(10, 1, 2, 10, 3999.00, 90,  1, 4),  -- iPhone11 黑256G 原价6799 秒杀5.9折
(11, 1, 2, 12, 3499.00, 120, 2, 5),  -- iPhone11 白128G 原价5999
(12, 1, 2, 14, 3199.00, 150, 2, 6);  -- iPhone11 白64G 原价5499

-- 晚间专场 (session_id=3) 4款商品
INSERT INTO gulimall_sms.sms_seckill_sku_relation (id, promotion_id, promotion_session_id, sku_id, seckill_price, seckill_count, seckill_limit, seckill_sort)
VALUES
(13, 1, 3, 8,  3599.00, 100, 2, 1),  -- 华为Mate30Pro罗兰紫8+128G
(14, 1, 3, 13, 3999.00, 70,  1, 2),  -- iPhone11 白256G 原价6799
(15, 1, 3, 15, 3499.00, 110, 2, 3),  -- iPhone11 绿128G 原价5999
(16, 1, 3, 2,  3699.00, 90,  2, 4);  -- 华为Mate30Pro星河银8+128G

-- 已结束上午场 (session_id=6) 用于历史测试
INSERT INTO gulimall_sms.sms_seckill_sku_relation (id, promotion_id, promotion_session_id, sku_id, seckill_price, seckill_count, seckill_limit, seckill_sort)
VALUES
(17, 1, 6, 1,  4199.00, 200, 2, 1),
(18, 1, 6, 9,  3699.00, 250, 2, 2);

-- 明天场次 (session_id=4 & 5)
INSERT INTO gulimall_sms.sms_seckill_sku_relation (id, promotion_id, promotion_session_id, sku_id, seckill_price, seckill_count, seckill_limit, seckill_sort)
VALUES
(19, 1, 4, 1,  3899.00, 80, 2, 1),
(20, 1, 4, 9,  3399.00, 100, 2, 2),
(21, 1, 5, 3,  3899.00, 70, 1, 1),
(22, 1, 5, 10, 3899.00, 60, 1, 2);
