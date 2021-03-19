package org.macross.AppleStore_Common_Config.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * id	int
 * out_trade_no	varchar	订单唯一标识
 * user_id	int	用户id
 * state	int	0表示未支付，1表示已支付,默认为0
 * total_fee	int	支付金额，单位分
 * commodity_id	int	商品id
 * commodity_describe	varchar	商品描述
 * commodity_img	varchar	商品封面图
 * create_time	datetime	订单生成时间
 */
@Data
@ToString
@JsonInclude(value =JsonInclude.Include.NON_NULL)
public class ShoppingCartOrder {

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

    @JsonProperty("commodity_img")
    private String commodityImg;

    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}
