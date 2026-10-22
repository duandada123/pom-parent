package com.sinogale.dksdkdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket dataWorksApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("DataWorks SDK 接口文档")
                        .description("脚本创建、提交、发布与日志查询。提交和发布为异步操作，"
                                + "success=true 表示本次请求处理成功，最终结果请查看业务状态。")
                        .version("1.0.0")
                        .build())
                .directModelSubstitute(LocalDate.class, String.class)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sinogale.dksdkdemo.controller"))
                .paths(PathSelectors.ant("/api/dataworks/**"))
                .build();
    }
}
