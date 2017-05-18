package com.zero.util;

import com.zero.vo.HealthCheckVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Date;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/3
 */
public class RedisHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RedisHelper.class);
    private static org.springframework.context.ApplicationContext context;
    private static ShardedJedisPool pool;

    static {
        try {
            context = new org.springframework.context.support.ClassPathXmlApplicationContext(
                    "applicationContext-redis.xml");
        } catch (Exception var1) {
            throw new IllegalArgumentException("applicationContext-redis.xml没有被找到");
        }

        if (context == null) {
            throw new IllegalArgumentException("context不允许为空");
        } else {
            pool = (ShardedJedisPool) context.getBean("shardedJedisPool");
            if (pool == null) {
                throw new IllegalArgumentException("bean name 'shardedJedisPool' is not defined");
            }
        }
    }

    public RedisHelper() {
    }

    public static Long delete(String key) throws Exception {
        ShardedJedis jedis = pool.getResource();

        Long rtn;
        try {
            rtn = jedis.del(getRedisKey(key));
        } catch (Exception e) {
            throw e;
        } finally {
            jedis.close();
        }

        return rtn;
    }

    public static String set(String key, String value) throws Exception {
        ShardedJedis jedis = pool.getResource();
        String rtn;
        try {
            rtn = jedis.set(getRedisKey(key), value);
        } catch (Exception e) {
            throw e;
        } finally {
            jedis.close();
        }

        return rtn;
    }

    public static long expire(String key, int seconds) throws Exception {
        ShardedJedis jedis = pool.getResource();

        long rtn;
        try {
            rtn = jedis.expire(getRedisKey(key), seconds);
        } catch (Exception e) {
            throw e;
        } finally {
            jedis.close();
        }

        return rtn;
    }

    public static String get(String key) throws Exception {
        ShardedJedis jedis = pool.getResource();

        String rtn;
        try {
            rtn = jedis.get(getRedisKey(key));
        } catch (Exception e) {
            throw e;
        } finally {
            jedis.close();
        }

        return rtn;
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
                    DateUtil.format(new Date(startTimeMillis), "yyyy-MM-dd HH:mm:ss"));
            healthCheckVo.setNormal(true);
            healthCheckVo.setCostTime(String.format("%sms", System.currentTimeMillis() - startTimeMillis));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            healthCheckVo.setNormal(false);
        }
        return healthCheckVo;
    }
}
