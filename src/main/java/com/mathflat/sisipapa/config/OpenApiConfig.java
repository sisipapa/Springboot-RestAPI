package com.mathflat.sisipapa.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "학생 점수관리 API 명세서",
                description = "Spring Boot를 이용한 학생 점수관리 웹 애플리케이션 API입니다.",
                version = "v1",
                contact = @Contact(name = "sisipapa", email = "sisipapa239@gmail.com"),
                license = @License(name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html")
        )
)
@Configuration
public class OpenApiConfig {
    /**
     * postsOpenApi.
     * @return GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi postsOpenApi() {
        String[] paths = {"/students/**", "/subjects/**"};
        return GroupedOpenApi.builder().setGroup("학생 점수관리 API").pathsToMatch(paths).build();
    }
}