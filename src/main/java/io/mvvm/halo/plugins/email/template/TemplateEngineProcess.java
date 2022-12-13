package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
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
    
    public static String process(String template, ReplyCommentContext variables) {
        Context context = new Context();
        context.setVariable("ctx", variables);
        return TemplateEngineProcess.engine.process(template, context);
    }
}
