package com.f1.fastone.common.aop;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.InternalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RedisExceptionHandlerAspect {

    @Around("execution(* com.f1.fastone.cart.repository..*(..))")
    public Object handleRedisExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (RedisConnectionFailureException e) {
            log.error("[Redis 연결 오류] {} - {}", joinPoint.getSignature(), e.getMessage());
            throw new InternalServerException(ErrorCode.REDIS_CONNECTION_ERROR);
        } catch (JsonProcessingException e) {
            log.error("[Redis 데이터 직렬화 오류] {} - {}", joinPoint.getSignature(), e.getMessage());
            throw new InternalServerException(ErrorCode.REDIS_DATA_CORRUPTED);
        } catch (DataAccessException e) {
            log.error("[Redis 데이터 접근 오류] {} - {}", joinPoint.getSignature(), e.getMessage());
            throw new InternalServerException(ErrorCode.REDIS_OPERATION_FAILED);
        } catch (Exception e) {
            log.error("[Redis 예기치 못한 오류] {} - {}", joinPoint.getSignature(), e.getMessage());
            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
