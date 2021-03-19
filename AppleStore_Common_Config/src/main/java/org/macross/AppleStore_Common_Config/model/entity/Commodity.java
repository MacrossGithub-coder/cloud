package org.macross.AppleStore_Common_Config.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *         id	int	商品id
 *         category_id	int	商品类别id
 *         describe	varchar	商品描述
 *         home_img	varchar	首页展示图
 *         cover_img	varchar	封面图
 *         detail_img	varchar	细节图
 *         price	int	价格
 *         point	int	好评率,单位%
 *         create_time	datetime
 *         weight 权重
 */
@Data
@ToString
@JsonInclude(value =JsonInclude.Include.NON_NULL)
public class Commodity {

    private Integer id;

    private Integer stock;

    @JsonProperty("category_id")
    private Integer categoryId;

    private String describe;

    @JsonProperty("home_img")
    private String homeImg;

    @JsonProperty("cover_img")
    private String coverImg;

    @JsonProperty("detail_img")
    private String detailImg;

    private BigDecimal price;

    @JsonProperty("seckill_price")
    private Integer seckillPrice;

    private Integer point;

    private Integer weight;

    @JsonProperty("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startDate;

    @JsonProperty("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endDate;

    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTIme;

    private List<CommodityBanner> commodityBannerList;

}
