package io.mvvm.halo.plugins.email;

import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.engine.SpringWebFluxTemplateEngine;

/**
 * @description:
 * @author: pan
 **/
@Component
public class EMailTemplateEngineManager {

    private final ISpringWebFluxTemplateEngine engine;
    private final ITemplateResolver templateResolver;

    public EMailTemplateEngineManager(ReactiveExtensionClient client) {
        this.templateResolver = new EMailTemplateResolver(client);
        this.engine = templateEngineGenerator();
    }

    public ISpringWebFluxTemplateEngine getTemplateEngine() {
        return this.engine;
    }

    private ISpringWebFluxTemplateEngine templateEngineGenerator() {
        var engine = new SpringWebFluxTemplateEngine();
        engine.addTemplateResolver(this.templateResolver);
        return engine;
    }

}