package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.reflect.MethodSignature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@SuppressWarnings("all")
public class AutoFillAspect {

    // 实现类的自动填充字段
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {

    }

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行数据填充");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();// 获取方法签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = autoFill.value();

        Object[] args = joinPoint.getArgs();// 获取方法参数
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();


        if (value == OperationType.INSERT) {
            //为4个字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                setCreateTime.invoke(entity, now); // Fixed method name from setCreateTime to setCreateTime
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (value == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    }