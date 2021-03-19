package org.macross.AppleStore_Order_Service_proj.service;


import org.macross.AppleStore_Common_Config.model.entity.ShoppingCartOrder;

import java.util.List;

public interface ShoppingCartOrderService {
    int addToCart(Integer userId, Integer commodityId);

    int emptyCart(Integer userId);

    List<ShoppingCartOrder> findUserCartInfo(Integer userId);
}
