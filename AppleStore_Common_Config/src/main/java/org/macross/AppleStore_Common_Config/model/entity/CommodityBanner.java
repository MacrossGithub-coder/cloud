package org.macross.AppleStore_Common_Config.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 *         id	int
 *         commodity_id	int	商品id
 *         img	varchar	图片地址
 *         weight	int	数字越小排越前
 *         create_time	datetime
 */
@Data
@ToString
@JsonInclude(value =JsonInclude.Include.NON_NULL)
public class CommodityBanner {

    private Integer id;

    private Integer commodityId;

    private String img;

    private Integer weight;

    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
