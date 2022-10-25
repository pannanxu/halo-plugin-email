package io.mvvm.halo.plugins.email;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.SystemConfigurableEnvironmentFetcher;
import run.halo.app.infra.utils.JsonUtils;

import java.util.Map;

/**
 * @description:
 * @author: pan
 **/
public abstract class AbstractTemplateProcess implements ExtensionTemplateProcess {

    protected EmailTemplateEngineManager engineManager;
    protected SystemConfigurableEnvironmentFetcher environmentFetcher;
    protected ReactiveExtensionClient extensionClient;
    protected TemplateResolver templateResolver;

    public AbstractTemplateProcess() {
        this.engineManager = EmailPlugin.engineManager;
        this.environmentFetcher = EmailPlugin.environmentFetcher;
        this.extensionClient = EmailPlugin.client;
        this.templateResolver = EmailPlugin.templateResolver;
    }

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Deprecated
    protected String contentParse(String template, Context context) {
        return engineManager.getTemplateEngine().process(template, context);
    }

    protected Mono<String> templateToHtml(String template, Context context) {
        return templateResolver.getTemplate(template)
                .map(templateHtml -> engineManager.getTemplateEngine().process(templateHtml, context));
    }

    protected String subjectParse(String subjectExpress, Map<String, Object> variables) {
        return expressionParser.parseExpression(subjectExpress).getValue(variables, String.class);
    }

    protected <T> Mono<T> fetchSystemSetting(String key, Class<T> clazz) {
        return environmentFetcher.fetch(key, clazz);
    }

    protected Mono<EmailServerConfig> getEmailServerConfig() {
        return extensionClient.get(ConfigMap.class, EmailServerConfig.NAME)
                .map(ConfigMap::getData)
                .map(config -> JsonUtils.jsonToObject(config.get(EmailServerConfig.BASIC_GROUP), EmailServerConfig.class));
    }
}