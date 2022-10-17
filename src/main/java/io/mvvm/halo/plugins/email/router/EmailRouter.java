package io.mvvm.halo.plugins.email.router;

import io.mvvm.halo.plugins.email.EmailPluginConst;
import io.mvvm.halo.plugins.email.EmailTemplateOption;
import io.mvvm.halo.plugins.email.EmailTemplateOptionManager;
import io.mvvm.halo.plugins.email.IEMailService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @description:
 * @author: pan
 **/
@Configuration
public class EmailRouter {

    @Resource
    private IEMailService mailService;
    @Resource
    private EmailTemplateOptionManager templateOptionManager;

    @Bean
    RouterFunction<ServerResponse> testConnectionRouter() {
        return route(GET(buildRoute("testConnection")),
                request -> ServerResponse.ok().body(mailService.testConnection(), Boolean.class));
    }

    @Bean
    RouterFunction<ServerResponse> templateOptionRouter() {
        return route(GET(buildRoute("/templateOptions")),
                request -> ServerResponse.ok().body(templateOptionManager.getOptions(), EmailTemplateOption.class));
    }

    String buildRoute(String suffix) {
        return "/api/api.plugin.halo.run/v1alpha1/plugins/" + EmailPluginConst.pluginId + "/io.mvvm.halo.plugins.email" + suffix;
    }

}