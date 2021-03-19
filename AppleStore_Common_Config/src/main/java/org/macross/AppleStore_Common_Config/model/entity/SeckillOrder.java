package org.macross.AppleStore_Common_Config.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * CREATE TABLE `seckill_order`  (
 *   `id` int(11) NOT NULL AUTO_INCREMENT,
 *   `user_id` int(11) NULL DEFAULT NULL,
 *   `commodity_id` int(11) NULL DEFAULT NULL,
 *   `order_id` int(11) NULL DEFAULT NULL,
 *   PRIMARY KEY (`id`) USING BTREE
 * ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
 */
@Data
@ToString
@JsonInclude(value =JsonInclude.Include.NON_NULL)
public class SeckillOrder {
    private Integer id;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("commodity_id")
    private Integer commodityId;

    @JsonProperty("order_id")
    private Integer orderId;

}
