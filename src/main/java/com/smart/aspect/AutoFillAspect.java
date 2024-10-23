package com.smart.aspect;

import com.smart.annotation.AutoFill;
import com.smart.constants.OperationConstant;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.smart.mapper.*.*(..)) && @annotation(com.smart.annotation.AutoFill) ")
    public void autoFill(){};
    @Before("autoFill()")
    public void doFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("开始自动注入");
        //获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //从方法签名获取注解
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);

        String value = autoFill.operationType();
        //获取传入的参数，必须是一个包含setUpdateTime或者setUpdateTime的对象
        Object arg = joinPoint.getArgs()[0];
        //根据传入的对象参数去执行方法
        Method setUpdateTime = arg.getClass().getMethod("setUpdateTime", LocalDateTime.class);
        Method setCreateTime = arg.getClass().getMethod("setCreateTime", LocalDateTime.class);

        if (value == OperationConstant.INSERT) {

            setCreateTime.invoke(arg, LocalDateTime.now());

            setUpdateTime.invoke(arg, LocalDateTime.now());
        }
        else if (value == OperationConstant.UPDATE) {

            setUpdateTime.invoke(arg, LocalDateTime.now());
        }
    }

}
