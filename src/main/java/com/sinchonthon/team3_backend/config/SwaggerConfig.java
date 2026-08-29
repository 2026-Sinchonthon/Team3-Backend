package com.sinchonthon.team3_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("신촌핑")
                        .description("찐 신촌 자취생들만 아는 동네 생활 노하우를 지도 위에 핑 찍어 나누는 생존 꿀팁 가이드")
                        .version("v1.0.0"));
    }
}
