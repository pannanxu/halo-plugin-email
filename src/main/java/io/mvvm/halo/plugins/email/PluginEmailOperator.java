package io.mvvm.halo.plugins.email;

import org.pf4j.PluginWrapper;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @description:
 * @author: pan
 **/
public interface PluginEmailOperator {

    PluginWrapper getPluginWrapper();

    Set<ExtensionTemplateProcess> getTemplateProcess();

    Set<EmailTemplateOption> getTemplateOptions();

    default Set<TemplateLoader> getTemplateLoader() {
        return new CopyOnWriteArraySet<>();
    }
}