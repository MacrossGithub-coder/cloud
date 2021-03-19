package org.macross.AppleStore_Seckill_Service_Proj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/2/24 15:16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLimitAnno {
    String limitKey() default "limit";
    int limit() default 1;
}