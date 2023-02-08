package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import reactor.core.publisher.Mono;

/**
 * 将邮件模板和上下文参数处理转换成待发送的html.
 *
 * @author: pan
 **/
@Component
public class ProcessTemplateResolver implements TemplateResolver {
    private final ISpringWebFluxTemplateEngine engine = new SpringWebFluxTemplateEngine();
    private final TemplateResolver resolver;

    public ProcessTemplateResolver(ComposeThemeResolver resolver) {
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
