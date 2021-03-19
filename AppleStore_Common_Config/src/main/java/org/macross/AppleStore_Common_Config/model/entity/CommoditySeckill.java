package org.macross.AppleStore_Common_Config.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * CREATE TABLE `commodity_seckill`  (
 *         `id` int(11) NOT NULL AUTO_INCREMENT,
 *         `commodity_id` int(11) NULL DEFAULT NULL,
 *         `seckill_price` int(255) NULL DEFAULT NULL,
 *         `stock` int(255) NULL DEFAULT NULL,
 *         `start_date` datetime(0) NULL DEFAULT NULL,
 *         `end_date` datetime(0) NULL DEFAULT NULL,
 *         PRIMARY KEY (`id`) USING BTREE
 *         ) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
 */
@JsonInclude(value =JsonInclude.Include.NON_NULL)
@Data
@ToString
public class CommoditySeckill {

    private Integer id;

    @JsonProperty("commodity_id")
    private Integer commodityId;

    @JsonProperty("seckill_price")
    private BigDecimal seckillPrice;

    private Integer stock;

    @JsonProperty("start_date")
    private Date startDate;

    @JsonProperty("end_date")
    private Date endDate;

}
