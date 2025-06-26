package net.cnki.training.okms.basecom.redis.lock;

import lombok.extern.slf4j.Slf4j;
import net.cnki.training.okms.basecom.util.SpringContextHolder;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * @author fansl
 * @description DistributedLockUtil
 * @date 2020-06-28 14:14
 */
public class DistributedLockUtil {
    /**
     * redis key分隔符
     */
    static String REDIS_KEY_SEPARATOR = ":";
    /**
     * 分布式锁
     */
    static String DISTRIBUTED_LOCK_KEY_PREFIX="REDIS-LOCK"+REDIS_KEY_SEPARATOR;
    /**
     * 获取分布式锁
     * 默认锁过期时间15s
     */
    public static DistributedLock getDistributedLock(String lockKey) {
        lockKey = assembleKey(lockKey);
        return new RedisLock(lockKey);
    }

    /**
     * 获取分布式锁
     */
    public static DistributedLock getDistributedLock(String lockKey, int expireMilliseconds) {
        lockKey = assembleKey(lockKey);
        return new RedisLock(lockKey, expireMilliseconds);
    }

    /**
     * 对 key 进行拼接
     */
    private static String assembleKey(String lockKey) {
        return DISTRIBUTED_LOCK_KEY_PREFIX + lockKey;
    }

    /**
     * @author fansl
     * @description DistributedLock 分布式锁
     * @date 2020-06-28 14:13
     */
    public interface DistributedLock {
        /**
         * 获取锁
         * @return true=加锁成功；false=加锁失败
         */
        boolean acquireLock(String value);

        /**
         * 释放锁
         * true=解锁成功；false=解锁失败
         */
        void releaseLock(String value);
    }
    @Slf4j
    static class RedisLock implements DistributedLock {
        private  StringRedisTemplate redisTemplate;
        /**
         * 锁的键值
         */
        private String lockKey;

        /**
         * 锁超时, 防止线程得到锁之后, 不去释放锁
         */
        private int expireMilliseconds = 15 * 1000;

        public RedisLock(@NotNull String lockKey) {
            this.lockKey = lockKey;
        }

        public RedisLock(@NotNull String lockKey, int expireMilliseconds) {
            this.lockKey = lockKey;
            this.expireMilliseconds = expireMilliseconds;
        }

        /**
         * 获取锁
         *
         * @return true=加锁成功；false=加锁失败
         */
        @Override
        public boolean acquireLock(String value) {
            if (redisTemplate == null) {
                redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
            }

            Boolean ret = redisTemplate.opsForValue().setIfAbsent(this.lockKey, value);
            if (ret) {
                Boolean ret1 = redisTemplate.expire(this.lockKey, (long)this.expireMilliseconds, TimeUnit.MILLISECONDS);
                if (log.isDebugEnabled()) {
                    log.info("[{}]分布式锁设置过期时间[{}]:{}", new Object[]{this.lockKey, this.expireMilliseconds, ret1 ? "成功" : "失败"});
                }
            }

            if (log.isDebugEnabled()) {
                log.info("[{}]获取分布式锁:{}", this.lockKey, ret ? "成功" : "失败");
            }

            return ret;
        }

        /**
         * 释放锁
         */
        @Override
        public void releaseLock(String value) {
            if (redisTemplate == null) {
                redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
            }

            String lockValue = redisTemplate.opsForValue().get(this.lockKey);
            boolean result = lockValue == null || value.equals(lockValue);
            if (result) {
                redisTemplate.delete(this.lockKey);
                log.info("[{}]释放分布式锁", this.lockKey);
            }
        }
    }
}
