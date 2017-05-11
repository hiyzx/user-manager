package com.zero.user.interceptor;

import com.zero.enums.CodeEnum;
import com.zero.exception.BaseException;
import com.zero.user.util.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/4/29
 */
public class AuthorityInterceptor extends org.springframework.web.servlet.handler.HandlerInterceptorAdapter {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AuthorityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String sessionId = request.getParameter("sessionId");
        if (org.springframework.util.StringUtils.hasText(sessionId) && SessionHelper.getUserId(sessionId) != null) {
            SessionHelper.heartBeat(sessionId);
            return true;
        } else {
            LOG.error("url={} need login", request.getRequestURI());
            throw new BaseException(CodeEnum.NOT_LOGIN, "未登录");
        }
    }
}
