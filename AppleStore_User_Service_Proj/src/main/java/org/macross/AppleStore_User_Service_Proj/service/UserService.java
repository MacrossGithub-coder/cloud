package org.macross.AppleStore_User_Service_Proj.service;

import org.macross.AppleStore_Common_Config.model.entity.User;
import org.macross.AppleStore_Common_Config.model.request.LoginRequest;
import org.macross.AppleStore_Common_Config.model.request.RegisterRequest;



public interface UserService {

    int register(RegisterRequest registerRequest);

    String login(LoginRequest loginRequest);

    User findUserInfoById(Integer user_id);

    boolean logout(Integer userId, String accessToken);
}
