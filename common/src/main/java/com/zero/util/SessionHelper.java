package com.zero.util;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
public class SessionHelper {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SessionHelper.class);
    private static final int SESSION_EXPIRED_SECONDS = ((Long) java.util.concurrent.TimeUnit.MINUTES.toSeconds(30)).intValue();

    public static void pushUserId(String sessionId, int userId) throws Exception {
        String key = sessionIdWrapper(sessionId);
        LOG.debug("push sessionId={}", sessionId);
        RedisHelper.set(key, String.valueOf(userId));
        RedisHelper.expire(key, SESSION_EXPIRED_SECONDS);
    }

    public static Integer getUserId(String sessionId) throws Exception {
        String key = sessionIdWrapper(sessionId);
        Integer rtn = null;
        try {
            String userId = RedisHelper.get(key);
            rtn = Integer.valueOf(userId);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return rtn;
    }

    public static void heartBeat(String sessionId) throws Exception {
        Integer userId = getUserId(sessionId);
        if (userId != null) {
            String key = sessionIdWrapper(sessionId);
            LOG.debug("sessionId={} userId={} heartbeat", sessionId, userId);
            RedisHelper.expire(key, SESSION_EXPIRED_SECONDS);
        }
    }

    public static void clearSessionId(String sessionId) throws Exception {
        RedisHelper.delete(sessionIdWrapper(sessionId));
        LOG.debug("delete sessionId={}", sessionId);
    }

    private static String sessionIdWrapper(String sessionId) {
        return String.format("login_%s", sessionId);
    }
}
