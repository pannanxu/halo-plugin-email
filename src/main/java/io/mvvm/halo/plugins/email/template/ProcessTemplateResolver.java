package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import reactor.core.publisher.Mono;
import run.halo.app.theme.engine.SpringWebFluxTemplateEngine;

/**
 * ProcessTemplateResolver.
 *
 * @author: pan
 **/
public class ProcessTemplateResolver implements TemplateResolver {
    private final ISpringWebFluxTemplateEngine engine = new SpringWebFluxTemplateEngine();
    private final TemplateResolver resolver;

    public ProcessTemplateResolver(TemplateResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Mono<String> processReactive(CommentTemplateType template, ReplyCommentContext variables) {
        Context context = new Context();
        context.setVariable("ctx", variables);
        return resolver.processReactive(template, variables)
                .map(temp -> engine.process(temp, context));
    }

}
