package org.macross.AppleStore_User_Service_Proj.service.impl;


import org.macross.AppleStore_Common_Config.model.entity.User;
import org.macross.AppleStore_Common_Config.model.request.LoginRequest;
import org.macross.AppleStore_Common_Config.model.request.RegisterRequest;
import org.macross.AppleStore_User_Service_Proj.mapper.UserMapper;
import org.macross.AppleStore_User_Service_Proj.service.UserService;
import org.macross.AppleStore_User_Service_Proj.utils.CommonsUtils;
import org.macross.AppleStore_User_Service_Proj.utils.JWTUtils;
import org.macross.AppleStore_User_Service_Proj.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    @Qualifier("redisTemplateMaster")
    private RedisTemplate<String, Object> redisTemplateMaster;

    @Override
    public int register(RegisterRequest registerRequest) {

        if (registerRequest.getName() != null && registerRequest.getPhone() != null && registerRequest.getPwd() != null) {
            User user = new User();
            user.setName(registerRequest.getName());
            user.setPhone(registerRequest.getPhone());
            user.setPwd(CommonsUtils.MD5(registerRequest.getPwd()));
            user.setAccount(new BigDecimal(10000000));//初始金额为10000000分，100000元
            user.setAddress("中国");//初始地址为中国
            user.setHeadImg(getRandomImg());
            user.setCreateTime(new Date());

            return userMapper.register(user);
        }
        return -1;

    }

    @Override
    public String login(LoginRequest loginRequest) {

        User user = userMapper.login(loginRequest.getPhone(), CommonsUtils.MD5(loginRequest.getPwd()));

        //生成token
        if (!Objects.isNull(user) && user.getId() != null) {
            String token = JWTUtils.genericJsonWebToken(user);
            //存储token { token: userId } 过期时间为1周
            redisUtil.setObj(token, user.getId(), 60 * 60 * 24 * 7);

            //测试专用token
//            redisUtil.setObj(token, user.getId());

//            单设备登录方案：
//            { userId ：token } 防止用户重复登录
//            boolean result2 = redisUtil.set(user.getId().toString(),token);
//            return result && result2 ? token:null;

            Long size = redisTemplateMaster.opsForList().size(user.getId().toString());
            //多设备登录方案：表示允许number台设备同时登录,大于number则将最早登录的设备挤出
            int number = 3;
            if (size > number - 1) {
                String discard_token = (String) redisTemplateMaster.opsForList().rightPop(user.getId().toString());
                redisTemplateMaster.delete(discard_token);
            }
            redisTemplateMaster.opsForList().leftPush(user.getId().toString(), token);
            return token;
        }
        return null;
    }

    @Override
    public User findUserInfoById(Integer user_id) {

        User user = userMapper.findUserInfoById(user_id);
        return user;
    }

    @Override
    public boolean logout(Integer userId, String accessToken) {
        try {
            String temp_token;
            List<String> list = new ArrayList<>();
            while (!accessToken.equals(temp_token = (String) redisTemplateMaster.opsForList().rightPop(userId.toString()))) {
                list.add(temp_token);
            }
            int size = list.size();
            if (size > 0) {
                for (int i = size - 1; i >= 0; i--) {
                    redisTemplateMaster.opsForList().rightPush(userId.toString(), list.get(i));
                }
            }
            return redisTemplateMaster.delete(accessToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 随机头像
     */
    private static final String[] headImg = {
            "http://112.124.18.163/macross/img/a.jpg",
    };

    private String getRandomImg() {

        int size = headImg.length;
        Random random = new Random();
        int index = random.nextInt(size);
        return headImg[index];
    }


}
