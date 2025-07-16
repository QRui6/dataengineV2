package com.urban.carbon.lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁的具体实现逻辑
 * 
 * <p>该类通过AOP方式实现分布式锁功能，适用于需要控制并发访问的场景。</p>
 *
 * @author XuGaoran 
 * @since 1.0.0
 */
@Aspect
@Component
@Order(Integer.MIN_VALUE)
@Slf4j
public class DistributeLockAspect {

    /**
     * redisson 分布式redis 客户端
     * 
     * <p>用于获取Redisson的分布式锁对象。</p>
     */
    private final RedissonClient redissonClient;
    public DistributeLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 环绕通知方法，用于处理带有@DistributeLock注解的方法。
     *
     * <p>在方法执行前后进行加锁与解锁操作，确保同一时间只有一个线程可以执行被注解的方法。</p>
     *
     * @param pjp 切点信息
     * @return 方法执行结果
     * @throws Exception 如果加锁失败或方法执行异常
     */
    @Around("@annotation(com.urban.carbon.lock.DistributeLock)")
    public Object process(ProceedingJoinPoint pjp) throws Exception {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        DistributeLock distributeLock = method.getAnnotation(DistributeLock.class);
        // 获取key
        String key = getDistributeLockKey(pjp, distributeLock, method);
        // 获取当前场景
        String scene = distributeLock.scene();
        // 拼接存储在redisson中的键值
        String lockKey = scene + "#" + key;
        // 获取过期时间与等待时间
        int expireTime = distributeLock.expireTime();
        int waitTime = distributeLock.waitTime();
        // 加锁并返回结果
        return getResponse(pjp, lockKey, waitTime, expireTime);
    }

    /**
     * 获取response结果
     *
     * <p>尝试获取分布式锁，并在成功后执行目标方法，最后释放锁。</p>
     *
     * @param pjp 切入点
     * @param lockKey 分布式锁所用真正的键值
     * @param waitTime 等待时间
     * @param expireTime 过期时间
     * @return 响应
     * @throws Exception 抛出分布式锁异常
     */
    private Object getResponse(ProceedingJoinPoint pjp, String lockKey,
                               int waitTime, int expireTime)
            throws Exception {
        // 初始化response
        Object response;
        // 获取锁对象，基于传入的锁键值
        RLock rLock= redissonClient.getLock(lockKey);
        try {
            // 尝试加锁的结果
            boolean lockResult;
            // 等待时间是默认值
            if (waitTime == DistributeLockConstant.DEFAULT_WAIT_TIME) {
                // 过期时间也是默认值
                if (expireTime == DistributeLockConstant.DEFAULT_EXPIRE_TIME) {
                    log.info("lock for key : {}", lockKey);
                    rLock.lock();
                } else {
                    log.info("lock for key : {} , expire : {}", lockKey, expireTime);
                    rLock.lock(expireTime, TimeUnit.MILLISECONDS);
                }
                lockResult = true;
            } else {
                // 过期时间为默认值，等待时间不为默认值
                if (expireTime == DistributeLockConstant.DEFAULT_EXPIRE_TIME) {
                    log.info("try lock for key : {} , wait : {}", lockKey, waitTime);
                    lockResult = rLock.tryLock(waitTime, TimeUnit.MILLISECONDS);
                } else {
                    log.info("try lock for key : {} , expire : {} , wait : {}", lockKey, expireTime, waitTime);
                    lockResult = rLock.tryLock(waitTime, expireTime, TimeUnit.MILLISECONDS);
                }
            }
            if (!lockResult) {
                log.warn("lock failed for key : {} , expire : {}", lockKey, expireTime);
                throw new DistributeLockException("acquire lock failed... key : " + lockKey);
            }
            log.info("lock success for key : {} , expire : {}", lockKey, expireTime);
            response = pjp.proceed();
        } catch (Throwable e) {
            throw new Exception(e);
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info("unlock for key : {} , expire : {}", lockKey, expireTime);
            }
        }
        return response;
    }

    /**
     * 获取分布式锁的key
     * 
     * <p>根据注解配置和方法参数动态生成锁的key。</p>
     *
     * @param pjp 切入点
     * @param distributeLock 分布式锁（注解）
     * @param method 具体的方法
     * @return 返回key
     * @throws DistributeLockException 如果无法解析锁key表达式
     */
    private static String getDistributeLockKey(
            ProceedingJoinPoint pjp, DistributeLock distributeLock, Method method) {
        String key = distributeLock.key();
        // 如果分布式锁的 Key 为 None 的时候
        if (DistributeLockConstant.NONE_KEY.equals(key)) {
            if (DistributeLockConstant.NONE_KEY.equals(distributeLock.keyExpression())) {
                throw new DistributeLockException("no lock key found...");
            }
            SpelExpressionParser parser = new SpelExpressionParser();
            Expression expression = parser.parseExpression(distributeLock.keyExpression());
            EvaluationContext context = new StandardEvaluationContext();
            // 获取参数值
            Object[] args = pjp.getArgs();
            // 获取运行时参数的名称
            StandardReflectionParameterNameDiscoverer discoverer
                    = new StandardReflectionParameterNameDiscoverer();
            String[] parameterNames = discoverer.getParameterNames(method);
            // 将参数绑定到context中
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
            // 解析表达式，获取结果
            key = String.valueOf(expression.getValue(context));
        }
        return key;
    }
}

