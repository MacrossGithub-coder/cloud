package org.macross.AppleStore_User_Service_Proj.eventBus;

import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObserverAction {
    private final Object target;
    private final Method method;

    public ObserverAction(Object target, Method method) {
        this.target = Preconditions.checkNotNull(target);
        this.method = method;
        this.method.setAccessible(true);
    }

    /**
     * @param event event is the method parameters
     */
    public void execute(Object event) {
        try {
            method.invoke(target, event);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
