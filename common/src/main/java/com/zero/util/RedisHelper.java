package com.zero.util;

import com.zero.vo.HealthCheckVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/3
 */
public class RedisHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RedisHelper.class);
    @Resource
    private ShardedJedisPool shardedJedisPool;
    private static RedisHelper redisHelper;

    @PostConstruct
    public void init() {
        redisHelper = this;
        redisHelper.shardedJedisPool = this.shardedJedisPool;
    }

    private RedisHelper() {

    }

    private static ShardedJedisPool getShardedJedisPool() {
        return redisHelper.shardedJedisPool;
    }

    public static Long delete(String key) throws Exception {
        try (ShardedJedis jedis = getShardedJedisPool().getResource()) {
            return jedis.del(getRedisKey(key));
        }
    }

    public static String set(String key, String value) throws Exception {
        try (ShardedJedis jedis = getShardedJedisPool().getResource()) {
            return jedis.set(getRedisKey(key), value);
        }
    }

    public static long expire(String key, int seconds) throws Exception {
        try (ShardedJedis jedis = getShardedJedisPool().getResource()) {
            return jedis.expire(getRedisKey(key), seconds);
        }
    }

    public static String get(String key) throws Exception {
        try (ShardedJedis jedis = getShardedJedisPool().getResource();) {
            return jedis.get(getRedisKey(key));
        }
    }

    public static String emailKeyWrapper(String key) {
        return String.format("email_%s", key);
    }

    private static String getRedisKey(String key) {
        return String.format("user_%s", key);
    }

    public static HealthCheckVo checkRedisConnection() {
        HealthCheckVo healthCheckVo = new HealthCheckVo();
        healthCheckVo.setServiceName("redis");
        try {
            long startTimeMillis = System.currentTimeMillis();
            RedisHelper.set(String.format("%scheckRedisConnection", getRedisKey("")),
                    DateHelper.format(new Date(startTimeMillis), "yyyy-MM-dd HH:mm:ss"));
            healthCheckVo.setNormal(true);
            healthCheckVo.setCostTime(String.format("%sms", System.currentTimeMillis() - startTimeMillis));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            healthCheckVo.setNormal(false);
        }
        return healthCheckVo;
    }
}
