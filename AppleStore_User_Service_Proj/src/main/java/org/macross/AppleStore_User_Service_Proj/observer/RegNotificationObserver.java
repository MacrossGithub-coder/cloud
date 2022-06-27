package org.macross.AppleStore_User_Service_Proj.observer;


import org.macross.AppleStore_User_Service_Proj.eventBus.Subscribe;

public class RegNotificationObserver {

    @Subscribe
    public void handleRegSuccess(UserRegistration userRegistration){
        System.out.println("Welcome!"+userRegistration.toString());
    }
}
