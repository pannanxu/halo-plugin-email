package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.support.MailEnvironmentFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * MailEndpoint.
 *
 * @author: pan
 **/
@Component
public class MailEndpoint {

    private final MailService mailService;
    private final MailEnvironmentFetcher environmentFetcher;

    public MailEndpoint(MailService mailService, MailEnvironmentFetcher environmentFetcher) {
        this.mailService = mailService;
        this.environmentFetcher = environmentFetcher;
    }

    @Bean
    public RouterFunction<ServerResponse> testConnectionRouter() {
        return route(GET("/apis/io.mvvm.halo.plugins.email/testConnection"),
                request -> testConnection().flatMap(result -> ServerResponse.ok().bodyValue(result)));
    }

    Mono<Object> testConnection() {
        return environmentFetcher.fetchMailServer()
                .switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("暂无配置，请前往插件设置中配置后重试"))))
                .flatMap(setting -> {
                    try {
                        Boolean connection = mailService.connection(setting);
                        return Mono.just(connection ? "配置成功" : "配置失败");
                    } catch (Exception ex) {
                        return Mono.error(ex);
                    }
                });
    }

}
