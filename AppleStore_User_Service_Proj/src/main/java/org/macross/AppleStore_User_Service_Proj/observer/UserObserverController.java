package org.macross.AppleStore_User_Service_Proj.observer;


import org.macross.AppleStore_User_Service_Proj.eventBus.AsyncEventBus;
import org.macross.AppleStore_User_Service_Proj.eventBus.EventBus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class UserObserverController {

    private EventBus eventBus;
    private static final int DEFAULT_EVENTBUS_THREAD_POOL_SIZE =2;

    public UserObserverController(){
//        eventBus = new EventBus(); //同步阻塞模式
        eventBus = new AsyncEventBus(new
                ThreadPoolExecutor(DEFAULT_EVENTBUS_THREAD_POOL_SIZE,DEFAULT_EVENTBUS_THREAD_POOL_SIZE,60*60L, TimeUnit.SECONDS,new ArrayBlockingQueue<>(1)));
    }

    public void setRegObservers(List<Object> observers){
        for (Object observer:observers){
            eventBus.register(observer);
        }
    }

    public Integer register(UserRegistration userRegistration){
        //userService.register(telephone,password);
        System.out.println(userRegistration.toString()+"  注册成功！");
        Integer userId = new Random().nextInt();
        userRegistration.setUserId(userId);
        eventBus.post(userRegistration);
        return userId;
    }
}
