package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.support.MailServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ConfigMap;
import run.halo.app.infra.utils.JsonUtils;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * MailEndpoint.
 *
 * @author: pan
 **/
@Configuration
public class MailEndpoint {

    private final MailService mailService;

    public MailEndpoint(MailService mailService) {
        this.mailService = mailService;

        // 启动时加载一次配置
        new Thread(() -> {
            while (true) {
                if (null != MailBeanContext.client) {
                    break;
                }
            }
            testConnection().subscribe();
        }, "Mail-test-connection").start();
    }

    @Bean
    public RouterFunction<ServerResponse> testConnectionRouter() {
        return route(GET("/apis/io.mvvm.halo.plugins.email/testConnection"),
                request -> testConnection().flatMap(result -> ServerResponse.ok().bodyValue(result)));
    }

    Mono<Boolean> testConnection() {
        return MailBeanContext.client.get(ConfigMap.class, MailServerConfig.NAME)
                .map(ConfigMap::getData)
                .map(config -> {
                    String basic = config.get(MailServerConfig.GROUP);
                    return JsonUtils.jsonToObject(basic, MailServerConfig.class);
                })
                .flatMap(config -> Mono.just(mailService.connection(config)));
    }

}
