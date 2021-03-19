package org.macross.AppleStore_User_Service_Proj.mapper;

import org.apache.ibatis.annotations.Param;
import org.macross.AppleStore_Common_Config.model.entity.User;

import java.math.BigDecimal;


public interface UserMapper {

    int register(User user);

    User login(@Param("phone") String phone, @Param("pwd") String pwd);

    User findUserInfoById(@Param("id") Integer user_id);

    int UpdateUserAccount(@Param("user_id") Integer userId, @Param("account") BigDecimal account, @Param("org_account")BigDecimal orgAccount);
}
