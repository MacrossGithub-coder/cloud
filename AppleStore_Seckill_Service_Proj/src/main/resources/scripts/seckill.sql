TRUNCATE TABLE seckill_order;
TRUNCATE TABLE commodity_order;
UPDATE commodity_seckill SET stock = 10 WHERE commodity_id > 0;