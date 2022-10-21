package io.mvvm.halo.plugins.email;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: pan
 **/
@Component
public class EmailTemplateOptionManager {

    private static final Map<String, EmailTemplateOption> CONFIG_MAP = new ConcurrentHashMap<>();

    public EmailTemplateOptionManager() {
        Arrays.stream(EmailTemplateOptionEnum.values()).forEach(e -> register(e.getOption()));
    }

    public void register(EmailTemplateOption config) {
        unregister(config.name());
        CONFIG_MAP.put(config.name(), config);
    }

    public void unregister(String name) {
        CONFIG_MAP.remove(name);
    }

    public Flux<EmailTemplateOption> getOptions() {
        return Flux.fromIterable(CONFIG_MAP.values());
    }
}