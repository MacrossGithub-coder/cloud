package org.macross.AppleStore_User_Service_Proj.observer;


import org.macross.AppleStore_User_Service_Proj.eventBus.Subscribe;

public class RegPromotionObserver {

    @Subscribe
    public void handleRegSuccess(UserRegistration userRegistration){
        System.out.println(userRegistration.toString()+"注册成功，已发放体验金");
    }
}
