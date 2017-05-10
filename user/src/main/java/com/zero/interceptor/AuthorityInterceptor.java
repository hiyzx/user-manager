package com.zero.interceptor;

import com.zero.exception.BaseException;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
public class AuthorityInterceptor extends org.springframework.web.servlet.handler.HandlerInterceptorAdapter {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AuthorityInterceptor.class);

    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler)
            throws Exception {
        String sessionId = request.getParameter("sessionId");
        if (org.springframework.util.StringUtils.hasText(sessionId) && com.zero.util.SessionHelper.getUserId(sessionId) != null) {
            com.zero.util.SessionHelper.heartBeat(sessionId);
            return true;
        } else {
            LOG.error("url={} need login", request.getRequestURI());
            throw new BaseException(com.zero.enums.CodeEnum.NOT_LOGIN, "未登录");
        }
    }
}
