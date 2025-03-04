//package com.spzx.gateway.handler;
//
//import java.util.Optional;
//import jakarta.annotation.Resource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//import springfox.documentation.swagger.web.SecurityConfiguration;
//import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
//import springfox.documentation.swagger.web.SwaggerResourcesProvider;
//import springfox.documentation.swagger.web.UiConfiguration;
//import springfox.documentation.swagger.web.UiConfigurationBuilder;
//
//@RestController
//@RequestMapping("/swagger-resources")
//public class SwaggerHandler
//{
//    @Resource(required = false)
//    private SecurityConfiguration securityConfiguration;
//
//    @Resource(required = false)
//    private UiConfiguration uiConfiguration;
//
//    private final SwaggerResourcesProvider swaggerResources;
//
//    @Resource
//    public SwaggerHandler(SwaggerResourcesProvider swaggerResources)
//    {
//        this.swaggerResources = swaggerResources;
//    }
//
//    @GetMapping("/configuration/security")
//    public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration()
//    {
//        return Mono.just(new ResponseEntity<>(
//                Optional.ofNullable(securityConfiguration).orElse(SecurityConfigurationBuilder.builder().build()),
//                HttpStatus.OK));
//    }
//
//    @GetMapping("/configuration/ui")
//    public Mono<ResponseEntity<UiConfiguration>> uiConfiguration()
//    {
//        return Mono.just(new ResponseEntity<>(
//                Optional.ofNullable(uiConfiguration).orElse(UiConfigurationBuilder.builder().build()), HttpStatus.OK));
//    }
//
//    @SuppressWarnings("rawtypes")
//    @GetMapping("")
//    public Mono<ResponseEntity> swaggerResources()
//    {
//        return Mono.just((new ResponseEntity<>(swaggerResources.get(), HttpStatus.OK)));
//    }
//}
