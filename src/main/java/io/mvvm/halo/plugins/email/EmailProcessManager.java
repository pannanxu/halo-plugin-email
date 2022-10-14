package io.mvvm.halo.plugins.email;

import io.mvvm.halo.plugins.email.process.CommentExtensionTemplateProcess;
import io.mvvm.halo.plugins.email.process.PostExtensionTemplateProcess;
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
    private final EMailTemplateEngineManager engineManager;

    public EmailProcessManager(EMailTemplateEngineManager engineManager) {
        this.map = new ConcurrentHashMap<>();
        this.engineManager = engineManager;
        init();
    }

    private void init() {
        registry(new PostExtensionTemplateProcess(engineManager));
        registry(new CommentExtensionTemplateProcess(engineManager));
    }

    public synchronized void registry(ExtensionTemplateProcess process) {
        Set<ExtensionTemplateProcess> processes = map.getOrDefault(process.getEndpoint(), new CopyOnWriteArraySet<>());
        processes.add(process);
        map.put(process.getEndpoint(), processes);
    }

    public Set<ExtensionTemplateProcess> getProcess(String endpoint) {
        return map.getOrDefault(endpoint, new LinkedHashSet<>());
    }

    public Flux<ExtensionTemplateProcess> getProcessFlux(String endpoint) {
        Set<ExtensionTemplateProcess> process = getProcess(endpoint);
        System.out.println(process);
        return Flux.fromIterable(process);
    }
}