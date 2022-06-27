package org.macross.AppleStore_User_Service_Proj.eventBus;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class EventBus {
    private ExecutorService executor;
    private ObserverRegistry registry = new ObserverRegistry();

//    public EventBus(){
//        this(MoreExecutors.directExecutor());
//    }

    public EventBus(ExecutorService executor){
        this.executor = executor;
    }

    public void register(Object object){
        registry.register(object);
    }

    public void post(Object event){
        List<ObserverAction> observerActions = registry.getMatchedObserverActions(event);
        for (ObserverAction observerAction:observerActions){
            executor.execute(() -> observerAction.execute(event));
        }
        executor.shutdown();
    }
}
