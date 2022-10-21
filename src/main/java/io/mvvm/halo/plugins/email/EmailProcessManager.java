package io.mvvm.halo.plugins.email;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @description:
 * @author: pan
 **/
@Component
public class EmailProcessManager {

    private final Map<String, Set<ExtensionTemplateProcess>> map;

    public EmailProcessManager() {
        this.map = new ConcurrentHashMap<>();
    }

    public synchronized void register(ExtensionTemplateProcess process) {
        Set<ExtensionTemplateProcess> processes = map.getOrDefault(process.getEndpoint(), new CopyOnWriteArraySet<>());
        processes.add(process);
        map.put(process.getEndpoint(), processes);
    }

    public <T> void unregister(String endpoint, Class<T> clazz) {
        Set<ExtensionTemplateProcess> processes = map.get(endpoint);
        if (null != processes) {
            processes.stream()
                    .filter(e -> e.getClass().isAssignableFrom(clazz))
                    .findFirst()
                    .ifPresent(processes::remove);
        }
    }

    public Set<ExtensionTemplateProcess> getProcess(String endpoint) {
        return map.getOrDefault(endpoint, new LinkedHashSet<>());
    }

    public Flux<ExtensionTemplateProcess> getProcessFlux(String endpoint) {
        Set<ExtensionTemplateProcess> process = getProcess(endpoint);
        return Flux.fromIterable(process);
    }
}