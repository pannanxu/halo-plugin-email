package io.mvvm.halo.plugins.email;

import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import run.halo.app.theme.engine.SpringWebFluxTemplateEngine;

/**
 * @description:
 * @author: pan
 **/
public class EmailTemplateEngineManager {

    private final ISpringWebFluxTemplateEngine engine;

    public EmailTemplateEngineManager() {
        this.engine = new SpringWebFluxTemplateEngine();
    }

    public ISpringWebFluxTemplateEngine getTemplateEngine() {
        return this.engine;
    }

}