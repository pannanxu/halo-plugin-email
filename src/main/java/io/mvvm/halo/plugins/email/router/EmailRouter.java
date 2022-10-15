package io.mvvm.halo.plugins.email.router;

import io.mvvm.halo.plugins.email.EmailPluginConst;
import io.mvvm.halo.plugins.email.IEMailService;
import jakarta.annotation.Resource;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @description:
 * @author: pan
 **/
@Configuration
public class EmailRouter {

    @Resource
    private IEMailService mailService;

    @Bean
    RouterFunction<ServerResponse> testConnectionRouter() {
        return SpringdocRouteBuilder.route()
                .GET("/apis/api.plugin.halo.run/v1alpha1/plugins/" + EmailPluginConst.pluginId + "/io.mvvm.halo.plugins.email/testConnection", this::testConnection, builder -> {
                    builder.operationId("testConnection")
                            .description("testConnection..")
                            .tag("Email");
                })
                .build();
    }

    Mono<ServerResponse> testConnection(ServerRequest request) {
        return ServerResponse.ok().body(mailService.testConnection(), Boolean.class);
    }

}