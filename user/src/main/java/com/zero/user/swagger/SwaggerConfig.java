package com.zero.user.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static springfox.documentation.builders.PathSelectors.regex;

@org.springframework.context.annotation.Configuration
@springfox.documentation.swagger2.annotations.EnableSwagger2
public class SwaggerConfig {
    private String version;

    public SwaggerConfig(String version) {
        this.version = version;
    }

    @Bean
    public Docket swaggerSpringfoxDocket() {
        StopWatch watch = new StopWatch();
        watch.start();
        Docket swaggerSpringMvcPlugin = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .genericModelSubstitutes(ResponseEntity.class).select().paths(regex(".*?")).build();
        watch.stop();
        return swaggerSpringMvcPlugin;
    }

    private ApiInfo apiInfo() {
        return new springfox.documentation.builders.ApiInfoBuilder().title("ssm-base API")
                .description(String.format("ssm-base %s API 说明书", version)).termsOfServiceUrl(null).license("仅供内部参考")
                .licenseUrl(null).build();
    }
}
