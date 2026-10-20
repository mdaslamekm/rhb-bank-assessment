package com.rhb.assessment.bank_service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(com.rhb.assessment.bank_service.controller..*)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args = joinPoint.getArgs();

        logger.info("Incoming Request => Class: [{}], Method: [{}], Arguments: {}",
                className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("Exception in Method: [{}] with cause: {}", methodName, throwable.getMessage());
            throw throwable;
        }

        long executionTime = System.currentTimeMillis() - startTime;

        logger.info("Outgoing Response <= Method: [{}], Execution Time: {}ms, Body: {}",
                methodName, executionTime, result);

        return result;
    }
}
