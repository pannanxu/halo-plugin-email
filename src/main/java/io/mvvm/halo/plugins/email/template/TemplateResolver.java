package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.comment.CommentContext;
import io.mvvm.halo.plugins.email.template.loader.TemplateLoader;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import reactor.core.publisher.Mono;

/**
 * TemplateResolver.
 *
 * @author: pan
 **/
@Component
public class TemplateResolver {

    private final TemplateLoader loader;

    private final ISpringWebFluxTemplateEngine engine = new SpringWebFluxTemplateEngine();

    public TemplateResolver(TemplateLoader loader) {
        this.loader = loader;
    }

    public Mono<String> process(String path, CommentContext ctx) {
        return loader.load(path).map(content -> {
            Context context = new Context();
            context.setVariable("ctx", ctx);
            return engine.process(content, context);
        });
    }

}
