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
        Arrays.stream(EmailTemplateOptionEnum.values()).forEach(e -> registry(e.getOption()));
    }

    public void registry(EmailTemplateOption config) {
        CONFIG_MAP.remove(config.name());
        CONFIG_MAP.put(config.name(), config);
    }

    public Flux<EmailTemplateOption> getOptions() {
        return Flux.fromIterable(CONFIG_MAP.values());
    }
}