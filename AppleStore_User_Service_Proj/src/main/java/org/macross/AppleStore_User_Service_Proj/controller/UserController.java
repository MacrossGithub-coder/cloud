package org.macross.AppleStore_User_Service_Proj.controller;

import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.model.entity.User;
import org.macross.AppleStore_Common_Config.model.request.LoginRequest;
import org.macross.AppleStore_Common_Config.model.request.RegisterRequest;
import org.macross.AppleStore_Common_Config.model.request.UpdateAccountRequest;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_User_Service_Proj.mapper.UserMapper;
import org.macross.AppleStore_User_Service_Proj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/1/5 11:09
 */

@RestController
@RequestMapping("api/v1/pri/user")
@Slf4j
public class UserController {


    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @RequestMapping(value = "register",method = RequestMethod.POST)
    public JsonData register(@RequestBody RegisterRequest registerRequest){
        log.info("Receive postRequest register param [{}]",registerRequest);
        int result = userService.register(registerRequest);
        return result == 1 ? JsonData.buildSuccess("注册成功"):JsonData.buildError("注册失败");
    }

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public JsonData login(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
        log.info("Receive postRequest login param [{}]",loginRequest);
        String token = userService.login(loginRequest);
        return token != null ? JsonData.buildSuccess(token):JsonData.buildError("登录失败,请检查账号或密码是否正确");
    }

    @RequestMapping(value = "logout",method = RequestMethod.POST)
    public JsonData login(HttpServletRequest request){
        log.info("Receive request logout");
        String accessToken = request.getHeader("token");
        if (accessToken == null){
            accessToken = request.getParameter("token");
        }

        Integer user_id = Integer.parseInt(request.getHeader("user_id"));
        boolean result =  userService.logout(user_id,accessToken);
        return result ? JsonData.buildSuccess("删除redis中的token缓存成功"):JsonData.buildError("删除redis中的token缓存失败");
    }

    @RequestMapping(value = "find_by_token",method = RequestMethod.POST)
    public JsonData findUserInfoByToken(HttpServletRequest request){
        log.info("Receive getRequest findUserInfoByToken");
        Integer user_id = Integer.parseInt(request.getHeader("user_id"));
        User user = userService.findUserInfoById(user_id);
        return user != null ? JsonData.buildSuccess(user):JsonData.buildError("查询失败！");
    }

    @RequestMapping(value = "find_by_user_id",method = RequestMethod.POST)
    public JsonData findUserInfoByUserId(@RequestParam("user_id")Integer userId){
        log.info("Receive getRequest findUserInfoByUserId");
        User user = userService.findUserInfoById(userId);
        return user != null ? JsonData.buildSuccess(user):JsonData.buildError("查询失败！");
    }

    @RequestMapping(value = "update_user_account",method = RequestMethod.POST)
    public JsonData updateUserAccount(@RequestBody UpdateAccountRequest updateAccountRequest){
        log.info("Receive getRequest updateUserAccount");
        int result = userMapper.UpdateUserAccount(updateAccountRequest.getUser_id(), updateAccountRequest.getAccount(),updateAccountRequest.getOrg_account());
        return result >0 ? JsonData.buildSuccess("更新用户账户成功！"):JsonData.buildError("更新用户账户失败！");
    }
}
