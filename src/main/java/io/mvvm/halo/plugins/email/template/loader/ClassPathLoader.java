package io.mvvm.halo.plugins.email.template.loader;

import io.mvvm.halo.plugins.email.MailPlugin;
import io.mvvm.halo.plugins.email.utils.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * ClassPathLoader.
 *
 * @author: pan
 **/
@Component
public class ClassPathLoader implements TemplateLoader {

    private final ClassLoader loader = MailPlugin.class.getClassLoader();

    @Override
    public Mono<String> load(String path) {
        return Mono.fromSupplier(() -> {
            ClassPathResource resource = new ClassPathResource(path, loader);
            return FileUtils.readResourceAsString(resource);
        });
    }
}
