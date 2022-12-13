package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.MailBeanContext;
import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import reactor.core.publisher.Mono;

/**
 * ComposeThemeResolver.
 *
 * @author: pan
 **/
public class ComposeThemeResolver implements TemplateResolver {

    private final TemplateResolver resource;
    private final TemplateResolver theme;

    public ComposeThemeResolver() {
        this.resource = new ResourceTemplateResolver();
        this.theme = new ThemeTemplateResolver(MailBeanContext.environmentFetcher);
    }

    @Override
    public Mono<String> processReactive(CommentTemplateType template, ReplyCommentContext variables) {
        return theme.processReactive(template, variables)
                .switchIfEmpty(Mono.defer(() -> resource.processReactive(template, variables)));
    }
}
