package com.zero.swagger;

public class SwaggerFilter extends org.springframework.web.filter.OncePerRequestFilter {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SwaggerFilter.class);

	private final boolean swaggerAllowed;

	public SwaggerFilter(boolean swaggerAllowed) {
		this.swaggerAllowed = swaggerAllowed;
	}

	@Override
	protected void doFilterInternal(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.FilterChain filterChain)
			throws javax.servlet.ServletException, java.io.IOException {
		LOG.info("path={}, swaggerAllowed={}", request.getServletPath(), swaggerAllowed);
		if (!swaggerAllowed) {
			response.sendError(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND, "Swagger is not enabled in this environment");
			return;
		}
		filterChain.doFilter(request, response);
	}
}
