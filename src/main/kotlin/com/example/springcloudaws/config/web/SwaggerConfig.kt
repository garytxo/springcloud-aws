package com.example.springcloudaws.config.web
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import jakarta.servlet.ServletContext

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun notificationApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("default")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun metaData(servletContext: ServletContext): OpenAPI {
        val server = Server().url(servletContext.contextPath)
        return OpenAPI()
            .servers(listOf(server))
            .info(
                Info().title("Test SpringCloud AWS")
                    .description("Test SpringCloud AWS REST API")
            )
    }
}
