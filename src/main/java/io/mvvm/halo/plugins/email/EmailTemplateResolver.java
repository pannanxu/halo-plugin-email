package io.mvvm.halo.plugins.email;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;
import run.halo.app.extension.ReactiveExtensionClient;

import java.util.Map;

/**
 * @description: 从数据库加载通知模板
 * @author: pan
 **/
@Slf4j
public class EmailTemplateResolver extends StringTemplateResolver {

    private final ReactiveExtensionClient client;

    public EmailTemplateResolver(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration,
                                                        String ownerTemplate, String template,
                                                        Map<String, Object> templateResolutionAttributes) {
        return client.get(EmailTemplateExtension.class, template)
                .filter(notify -> null != notify.getSpec()
                        && notify.getSpec().isEnable())
                .map(notify -> new StringTemplateResource(notify.getSpec().getTemplate()))
                .blockOptional()
                .orElse(null);
    }

}