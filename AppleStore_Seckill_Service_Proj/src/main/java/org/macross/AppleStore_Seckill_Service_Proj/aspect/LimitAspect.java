package org.macross.AppleStore_Seckill_Service_Proj.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.macross.AppleStore_Common_Config.exception.RunTimeException;
import org.macross.AppleStore_Seckill_Service_Proj.annotation.DistributeLimitAnno;
import org.macross.AppleStore_Seckill_Service_Proj.lua.DistributedLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Author: jiancheng.zhang
 * @Date: 2021/2/24 15:50
 */
@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LimitAspect {

    @Autowired
    DistributedLimit distributedLimit;

    @Pointcut("@annotation(org.macross.AppleStore_Seckill_Service_Proj.annotation.DistributeLimitAnno)")
    public void limit() {}

    @Before("limit()")
    public void beforeLimit(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributeLimitAnno distributeLimitAnno = method.getAnnotation(DistributeLimitAnno.class);
        String key = distributeLimitAnno.limitKey();
        int limit = distributeLimitAnno.limit();
        Boolean exceededLimit = distributedLimit.distributedLimit(key, String.valueOf(limit));
        if(!exceededLimit) {
            log.info("接口限流处理，拒绝请求");
            throw new RunTimeException(-5,"接口限流处理");
        }
    }

}
