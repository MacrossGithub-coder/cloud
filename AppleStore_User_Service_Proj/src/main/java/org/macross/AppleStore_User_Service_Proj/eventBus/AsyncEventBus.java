package org.macross.AppleStore_User_Service_Proj.eventBus;

import java.util.concurrent.ExecutorService;

public class AsyncEventBus extends EventBus{
    public AsyncEventBus(ExecutorService executor){
        super(executor);
    }
}
