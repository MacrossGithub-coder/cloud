package org.macross.AppleStore_Common_Config.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/1/7 11:51
 */
@Data
@AllArgsConstructor
public class UpdateAccountRequest {

    private Integer user_id;

    private BigDecimal account;

    private BigDecimal org_account;
}
