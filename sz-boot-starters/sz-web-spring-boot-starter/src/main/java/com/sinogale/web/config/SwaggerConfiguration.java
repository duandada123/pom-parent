package com.sinogale.web.config;

/**
 * @ClassName SwaggerConfiguration
 * @Author duanchao
 * @Date 2022/4/3 20:45
 **/
/*import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ConditionalOnProperty(
        prefix = "sz.web",
        name = {"enable"},
        havingValue = "true"
)
@EnableConfigurationProperties({SwaggerProperties.class})
public class SwaggerConfiguration {

    @Bean
    public Docket createRestApi(SwaggerProperties properties) {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> parameters = new ArrayList();
        tokenPar.name("token")
                .description("令牌")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();
        parameters.add(tokenPar.build());
        return (new Docket(DocumentationType.SWAGGER_2))
                .apiInfo(this.apiInfo(properties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()))
                .paths(PathSelectors.any()).build().globalOperationParameters(parameters);
    }

    protected ApiInfo apiInfo(SwaggerProperties properties) {
        return (new ApiInfoBuilder()).title(properties.getTitle()).version(properties.getVersion()).description(properties.getDescription()).build();
    }
}*/
