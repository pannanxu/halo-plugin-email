package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.process.CommentExtensionTemplateProcess;
import lombok.Getter;
import org.pf4j.PluginWrapper;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: pan
 **/
@Getter
public class RootEmailPluginOperator implements PluginEmailOperator {

    private final PluginWrapper pluginWrapper;
    private final Set<ExtensionTemplateProcess> templateProcess = new CopyOnWriteArraySet<>();
    private final Set<TemplateLoader> templateLoader = new CopyOnWriteArraySet<>();
    private final Set<EmailTemplateOption> templateOptions;

    public RootEmailPluginOperator(PluginWrapper pluginWrapper) {
        this.pluginWrapper = pluginWrapper;
        templateProcess.add(new CommentExtensionTemplateProcess());
        templateLoader.add(new ClassPathTemplateLoader());
        templateOptions = Arrays.stream(EmailTemplateOptionEnum.values())
                .map(EmailTemplateOptionEnum::getOption)
                .collect(Collectors.toSet());
    }

}