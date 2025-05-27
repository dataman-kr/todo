//package com.example.todo.config
//
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Bean
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
//import org.springframework.core.io.Resource
//import org.springframework.core.io.ClassPathResource
//import org.springframework.web.servlet.resource.PathResourceResolver
//import java.io.IOException
//
///**
// * Swagger configuration for serving the openapi.yml file and configuring Swagger UI.
// */
//@Configuration
//class SwaggerConfig : WebMvcConfigurer {
//
//    /**
//     * Configure resource handlers to serve the openapi.yml file.
//     */
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        registry.addResourceHandler("/openapi.yml")
//            .addResourceLocations("classpath:/")
//            .resourceChain(true)
//            .addResolver(object : PathResourceResolver() {
//                @Throws(IOException::class)
//                override fun getResource(resourcePath: String, location: Resource): Resource? {
//                    val requestedResource = location.createRelative(resourcePath)
//                    return if (requestedResource.exists() && requestedResource.isReadable) requestedResource
//                    else ClassPathResource("/openapi.yml")
//                }
//            })
//
//        // Add handler for Swagger UI resources
//        registry.addResourceHandler("/swagger-ui/**")
//            .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
//
//        // Add handler for Swagger UI resources under /api/v1 prefix
//        registry.addResourceHandler("/api/v1/swagger-ui/**")
//            .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
//    }
//
//    /**
//     * Configure view controllers to forward requests to the appropriate views.
//     * This is used to forward requests from /swagger-ui.html to /api/v1/swagger-ui/index.html
//     */
//    override fun addViewControllers(registry: ViewControllerRegistry) {
//        // Forward requests to /swagger-ui.html to /api/v1/swagger-ui/index.html
//        registry.addViewController("/swagger-ui.html")
//            .setViewName("redirect:/api/v1/swagger-ui/index.html")
//
//        // Forward requests to /swagger-ui/index.html to /api/v1/swagger-ui/index.html
//        registry.addViewController("/swagger-ui/index.html")
//            .setViewName("redirect:/api/v1/swagger-ui/index.html")
//    }
//}
