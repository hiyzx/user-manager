package com.zero.user.swagger;

import static springfox.documentation.builders.PathSelectors.regex;

@org.springframework.context.annotation.Configuration
@springfox.documentation.swagger2.annotations.EnableSwagger2
public class SwaggerConfig {
    private String version;

    public SwaggerConfig(String version) {
        this.version = version;
    }

    @org.springframework.context.annotation.Bean
    public springfox.documentation.spring.web.plugins.Docket swaggerSpringfoxDocket() {
        org.springframework.util.StopWatch watch = new org.springframework.util.StopWatch();
        watch.start();
        springfox.documentation.spring.web.plugins.Docket swaggerSpringMvcPlugin = new springfox.documentation.spring.web.plugins.Docket(springfox.documentation.spi.DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .genericModelSubstitutes(org.springframework.http.ResponseEntity.class).select().paths(regex(".*?")).build();
        watch.stop();
        return swaggerSpringMvcPlugin;
    }

    private springfox.documentation.service.ApiInfo apiInfo() {
        return new springfox.documentation.builders.ApiInfoBuilder().title("ssm-base API").description(String.format("ssm-base %s API 说明书", version))
                .termsOfServiceUrl(null).license("仅供内部参考").licenseUrl(null).build();
    }
}
