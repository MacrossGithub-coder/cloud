package org.macross.AppleStore_Common_Config.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
 `out_trade_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单唯一标识',
 `user_id` int(12) NULL DEFAULT NULL COMMENT '用户id',
 `state` int(11) NULL DEFAULT NULL COMMENT '0表示未支付，1表示已支付',
 `total_fee` int(11) NULL DEFAULT NULL COMMENT '支付金额，单位分',
 `commodity_id` int(11) NULL DEFAULT NULL COMMENT '商品id',
 `commodity_describe` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品描述',
 `commodity_img` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品封面图',
 `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送地址',
 `if_seckill` int(11) NULL DEFAULT NULL COMMENT '是否为秒杀订单',
 `seckill_code` int(11) NULL DEFAULT NULL COMMENT 'if_seckill为1时该字段有值，为0正常，-2余额不足，-3系统异常',
 `fail_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '失败原因，seckill_code<0时该字段有值',
 `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单生成时间',
 */
@JsonInclude(value =JsonInclude.Include.NON_NULL)
@Data
@ToString
public class CommodityOrder {

    private Integer id;

    @JsonProperty("out_trade_no")
    private String outTradeNo;

    @JsonProperty("user_id")
    private Integer userId;

    private Integer state;

    @JsonProperty("total_fee")
    private BigDecimal totalFee;

    @JsonProperty("commodity_id")
    private Integer commodityId;

    @JsonProperty("commodity_describe")
    private String commodityDescribe;

    private String address;

    @JsonProperty("commodity_img")
    private String commodityImg;

    @JsonProperty("if_seckill")
    private Integer ifSeckill;

    @JsonProperty("seckill_code")
    private Integer seckillCode;

    @JsonProperty("fail_msg")
    private String failMsg;

    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

}
