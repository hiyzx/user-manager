package com.zero.user.interceptor;

public class CrossDomainInterceptor extends org.springframework.web.servlet.handler.HandlerInterceptorAdapter {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CrossDomainInterceptor.class);

	@Override
	public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler)
			throws Exception {
		LOG.debug("{}", request.getRequestURL());
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Key");
		response.setHeader("access-control-allow-credentials", "true");
		response.setHeader("access-control-allow-methods", "GET,POST,PUT,DELETE,OPTIONS");
		return true;
	}
}
