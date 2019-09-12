package com.turo.app.base.package.name.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

import java.util.concurrent.CompletableFuture

@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())
            .genericModelSubstitutes(ResponseEntity::class.java, CompletableFuture::class.java)
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfo(
            "test-service-name-msvc REST API",
            "A simple example of a REST API.",
            "API TOS",
            "Terms of service",
            Contact(
                "Turo Engineering",
                "https://github.com/turo/test-service-name-msvc",
                "engineering+test-service-name-msvc@turo.com"
            ),
            "License of API",
            "API license URL",
            emptyList()
        )
    }
}
