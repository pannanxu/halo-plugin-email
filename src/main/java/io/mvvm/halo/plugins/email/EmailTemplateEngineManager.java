package io.mvvm.halo.plugins.email;

import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import run.halo.app.theme.engine.SpringWebFluxTemplateEngine;

/**
 * @description:
 * @author: pan
 **/
@Component
public class EmailTemplateEngineManager {

    private final ISpringWebFluxTemplateEngine engine;

    public EmailTemplateEngineManager() {
        this.engine = new SpringWebFluxTemplateEngine();
    }

    public ISpringWebFluxTemplateEngine getTemplateEngine() {
        return this.engine;
    }

}