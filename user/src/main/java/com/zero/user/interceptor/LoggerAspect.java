package com.zero.user.interceptor;

import com.zero.exception.BaseException;
import com.zero.user.annotation.AopCutAnnotation;
import com.zero.user.util.AsyncHttpClient;
import com.zero.user.util.SessionHelper;
import com.zero.util.JsonUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * http://blog.csdn.net/xiaoxian8023/article/details/17285809<br/>
 *
 * @Description: 全局日志类
 */
@Aspect
public class LoggerAspect {
    private static final Logger LOG = LoggerFactory.getLogger(LoggerAspect.class);
    @Value("${post.trace.data}")
    private String postTraceDataUrl;
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

    @Around(value = "execution(* com.zero.user.controller.*.*(..)) && @annotation(log)")
    public Object doAround(ProceedingJoinPoint pjp, AopCutAnnotation log) throws Throwable {
        StopWatch clock = new StopWatch();
        clock.start();
        // 调用的方法名
        String method = pjp.getSignature().getName();
        // 获取目标对象(形如：com.action.admin.LoginAction@1a2467a)
        Object target = pjp.getTarget();
        // 获取目标对象的类名(形如：com.action.admin.LoginAction)
        String targetName = pjp.getTarget().getClass().getName();
        // 执行完方法的返回值：调用proceed()方法，就会触发切入点方法执行
        Object result = pjp.proceed();// result的值就是被拦截方法的返回值
        clock.stop();
        Map<String, String> map = new HashMap<>();
        map.put("app_name", log.appName());
        map.put("app_type", "web");
        map.put("class_name", target.toString());
        map.put("targetName", targetName);
        map.put("function_name", method);
        map.put("use_time", String.valueOf(clock.getTotalTimeMillis()));
        AsyncHttpClient.postTraceData(map);// 异步数据上报接口
	    Thread.sleep(20000);
        return result;
    }
}