package io.mvvm.halo.plugins.email.template;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import run.halo.app.theme.engine.SpringWebFluxTemplateEngine;

import java.util.Map;

/**
 * TemplateEngineProcess.
 *
 * @author: pan
 **/
public class TemplateEngineProcess {
    private static final ISpringWebFluxTemplateEngine engine = new SpringWebFluxTemplateEngine();

    public static String process(String template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return TemplateEngineProcess.engine.process(template, context);
    }
}
