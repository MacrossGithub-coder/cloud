package org.macross.AppleStore_Order_Service_proj.mapper;

import org.apache.ibatis.annotations.Param;
import org.macross.AppleStore_Common_Config.model.entity.ShoppingCartOrder;

import java.util.List;

public interface ShoppingCartOrderMapper {

    int addToCart(ShoppingCartOrder shoppingCartOrder);

    List<ShoppingCartOrder> findUserCartInfo(@Param("user_id") Integer userId);

    int updateOrderState(@Param("user_id") Integer userId);
}
