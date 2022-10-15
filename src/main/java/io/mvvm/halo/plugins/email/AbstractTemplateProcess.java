package io.mvvm.halo.plugins.email;

import lombok.Setter;
import org.thymeleaf.context.Context;

/**
 * @description:
 * @author: pan
 **/
public abstract class AbstractTemplateProcess implements ExtensionTemplateProcess {

    @Setter
    protected EMailTemplateEngineManager engineManager;

    protected String process(String template, Context context) {
        return engineManager.getTemplateEngine().process(template, context);
    }
}