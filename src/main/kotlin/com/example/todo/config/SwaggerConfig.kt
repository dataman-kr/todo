package com.example.todo.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.core.io.Resource
import org.springframework.core.io.ClassPathResource
import org.springframework.web.servlet.resource.PathResourceResolver
import java.io.IOException

/**
 * Swagger configuration for serving the openapi.yml file and configuring Swagger UI.
 */
@Configuration
class SwaggerConfig : WebMvcConfigurer {

    /**
     * Configure resource handlers to serve the openapi.yml file.
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/openapi.yml")
            .addResourceLocations("classpath:/")
            .resourceChain(true)
            .addResolver(object : PathResourceResolver() {
                @Throws(IOException::class)
                override fun getResource(resourcePath: String, location: Resource): Resource? {
                    val requestedResource = location.createRelative(resourcePath)
                    return if (requestedResource.exists() && requestedResource.isReadable) requestedResource
                    else ClassPathResource("/openapi.yml")
                }
            })

        // Add handler for Swagger UI resources
        registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
    }
}
