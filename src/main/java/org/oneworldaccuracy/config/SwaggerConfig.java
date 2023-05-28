package org.oneworldaccuracy.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${application.version}")
    private String version;

    @Bean
    public OpenAPI api(){
        return new OpenAPI()
                .info(new Info()
                        .title("Work Item Application")
                        .description("API that provides CRUD operation for work items.")
                        .version(version));
    }


    @Bean
    public GroupedOpenApi workItemEndPoints(){
        return  GroupedOpenApi
                .builder()
                .group("WorkItem")
                .pathsToMatch("/work-items/**").build();
    }
}
