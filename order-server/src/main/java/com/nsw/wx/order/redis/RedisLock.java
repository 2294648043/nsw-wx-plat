package com.nsw.wx.order.redis;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.xml.core.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * 加锁
 *
 * @author 张维维
 * date: 2018/10/30/030 18:57
 */
@Component
@Slf4j
public class RedisLock {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 加锁
     * @param key
     * @param value 当前时间≠超时时间
     * @return
     */
    public boolean lock(String key,String value) {
        if (redisTemplate.opsForValue().setIfAbsent(key, value)){
            return true;
        }

        String currentValue = redisTemplate.opsForValue().get(key);
        //如果锁超时
        if (!StringUtils.isEmpty(currentValue)&&Long.parseLong(currentValue)
                <System.currentTimeMillis()){
            //获取上一个锁的时间
            String oldvalue = redisTemplate.opsForValue().getAndSet(key, value);
            if(!StringUtils.isEmpty(oldvalue)&& oldvalue.equals(currentValue)){
                return true;
            }
        }
        return false;

    }

    public  void  unlock(String key,String value){
        try {
            String currentValue = redisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            log.error("redis分布式解锁异常,{}",ex);
        }
    }

//    private static final String LOCK_SUCCESS = "OK";
//    private static final String SET_IF_NOT_EXIST = "NX";
//    private static final String SET_WITH_EXPIRE_TIME = "PX";
//
//    /**
//     * 尝试获取分布式锁
//     * @param jedis Redis客户端
//     * @param lockKey 锁
//     * @param requestId 请求标识
//     * @param expireTime 超期时间
//     * @return 是否获取成功
//     */
//    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
//
//        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
//
//        if (LOCK_SUCCESS.equals(result)) {
//            return true;
//        }
//        return false;
//
//    }
//    private static final Long RELEASE_SUCCESS = 1L;
//
//    /**
//     * 释放分布式锁
//     * @param jedis Redis客户端
//     * @param lockKey 锁
//     * @param requestId 请求标识
//     * @return 是否释放成功
//     */
//    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {
//
//        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
//
//        if (RELEASE_SUCCESS.equals(result)) {
//            return true;
//        }
//        return false;
//
//    }



}
