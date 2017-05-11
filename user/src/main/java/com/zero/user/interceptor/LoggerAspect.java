package com.zero.user.interceptor;

import com.zero.exception.BaseException;
import com.zero.user.util.SessionHelper;
import com.zero.util.JsonUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * http://blog.csdn.net/xiaoxian8023/article/details/17285809<br/>
 * 
 * @Description: 全局日志类
 */
@Aspect
public class LoggerAspect {
    private static final Logger LOG = LoggerFactory.getLogger(LoggerAspect.class);
    private static final String HOSTNAME = com.zero.user.util.IpUtil
            .getHostName(com.zero.user.util.IpUtil.getInetAddress());
    @Resource
    private HttpServletRequest request;

    // http://stackoverflow.com/questions/29653664/how-to-correctly-use-spring-aop-to-select-the-execution-of-a-method-annotated-wi
    @Pointcut("within(@org.springframework.stereotype.Controller *) &&@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    // @Pointcut("execution(public * com.zero.controller.*.*(..))")
    private void logController() {
    };

    /**
     * 定义拦截器的切面
     */
    @Pointcut("execution(boolean com.zero.user.interceptor..preHandle(..))")
    private void logInterceptor() {
    };

    @Pointcut("logController() || logInterceptor()")
    private void logControllerAndInterceptor() {
    };

    @AfterReturning(value = "logController()", returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {
        try {
            LOG.info("{} || request={} || response={}", HOSTNAME, parseRequest(), JsonUtil.toJSon(returnValue));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @AfterThrowing(value = "logControllerAndInterceptor()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        try {
            if (e instanceof BaseException) {
                BaseException baseExamException = (BaseException) e;
                LOG.info("{} || request={} || exceptionCode={}, exceptionMessage={}", HOSTNAME, parseRequest(),
                        baseExamException.getCodeEnum().getCodeEnum(), baseExamException.getMessage());
            } else {
                LOG.info("{} || request={} || exceptionMessage={}", HOSTNAME, parseRequest(), e.getMessage());
            }
        } catch (Exception e1) {
            LOG.error(e.getMessage(), e);
        }
    }

    private StringBuilder parseRequest() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getRequestURI()).append(", ");
        sb.append("IP->").append(com.zero.user.util.IpUtil.getIpAddress(request));
        Map<String, String[]> parameters = request.getParameterMap();
        if (!parameters.isEmpty()) {
            sb.append(", parameters->[");
            final String template = "%s=%s, ";
            for (Map.Entry<String, String[]> entity : parameters.entrySet()) {
                sb.append(String.format(template, entity.getKey(),
                        StringUtils.arrayToCommaDelimitedString(entity.getValue())));
            }
            sb.delete(sb.length() - 2, sb.length()).append("]");
        }
        String sessionId = request.getParameter("sessionId");
        if (StringUtils.hasText(sessionId)) {
            sb.append(", [sessionId=");
            sb.append(sessionId);
            sb.append(", userId=").append(SessionHelper.getUserId(sessionId));
            sb.append("]");
        }
        return sb;
    }
}