package io.mvvm.halo.plugins.email;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @description:
 * @author: pan
 **/
@Slf4j
public class EmailPluginManager {

    private static final EmailTemplateEngineManager templateEngineManager = new EmailTemplateEngineManager();

    private static final Map<String, PluginEmailOperator> PLUGIN_MAP = new ConcurrentHashMap<>();

    private static final Map<String, EmailTemplateOption> OPTION_MAP = new ConcurrentHashMap<>();

    private static final Map<String, Set<ExtensionTemplateProcess>> PROCESS_MAP = new ConcurrentHashMap<>();

    private static final Set<TemplateLoader> TEMPLATE_LOADERS = new CopyOnWriteArraySet<>();

    public synchronized static void register(PluginEmailOperator operators) {
        log.debug("注册到邮件插件: " + operators.getPluginWrapper().getPluginId());
        PLUGIN_MAP.put(operators.getPluginWrapper().getPluginId(), operators);

        operators.getTemplateOptions().forEach(e -> OPTION_MAP.put(e.name(), e));

        operators.getTemplateProcess().forEach(e -> {
            if (PROCESS_MAP.containsKey(e.getEndpoint())) {
                PROCESS_MAP.get(e.getEndpoint()).add(e);
            } else {
                PROCESS_MAP.put(e.getEndpoint(), new CopyOnWriteArraySet<>() {{
                    add(e);
                }});
            }
        });

        TEMPLATE_LOADERS.addAll(operators.getTemplateLoader());
    }

    public static PluginEmailOperator getOperator(String pluginId) {
        PluginEmailOperator operator = PLUGIN_MAP.get(pluginId);
        if (null == operator) {
            throw new RuntimeException("插件 " + pluginId + " 暂未注册");
        }
        return operator;
    }

    public synchronized static void unregister(String pluginId) {
        log.debug("从邮件插件中卸载: " + pluginId);
        PluginEmailOperator operator = getOperator(pluginId);

        operator.getTemplateLoader().forEach(loader -> TEMPLATE_LOADERS.removeIf(e -> e == loader));

        operator.getTemplateProcess().forEach(process -> PROCESS_MAP.get(process.getEndpoint())
                .removeIf(extensionTemplateProcess -> extensionTemplateProcess == process));

        operator.getTemplateOptions().forEach(options -> OPTION_MAP.remove(options.name()));

        PLUGIN_MAP.remove(pluginId);
    }

    public static Collection<EmailTemplateOption> getOptions() {
        return OPTION_MAP.values();
    }

    public static Flux<ExtensionTemplateProcess> getProcess(String endpoint) {
        return Flux.fromIterable(PROCESS_MAP.getOrDefault(endpoint, new CopyOnWriteArraySet<>()));
    }

    public static Set<TemplateLoader> getTemplateLoaders() {
        return TEMPLATE_LOADERS;
    }

    public static EmailTemplateEngineManager getTemplateEngineManager() {
        return templateEngineManager;
    }
}