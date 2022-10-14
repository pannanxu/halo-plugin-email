package io.mvvm.halo.plugins.email.router;

import io.mvvm.halo.plugins.email.IEMailService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: pan
 **/
@Configuration
public class EmailRouter {

    @Resource
    private IEMailService mailService;

//    @Bean
//    RouterFunction<ServerResponse> testConnectionRouter() {
//        return SpringdocRouteBuilder.route()
//                .GET("/testConnection", this::testConnection, builder -> {
//                    builder.operationId("testConnection")
//                            .description("testConnection..")
//                            .tag("Email");
//                })
//                .build();
//    }
//
//    Mono<ServerResponse> testConnection(ServerRequest request) {
//        return ServerResponse.ok().body(mailService.testConnection(), Boolean.class);
//    }



}