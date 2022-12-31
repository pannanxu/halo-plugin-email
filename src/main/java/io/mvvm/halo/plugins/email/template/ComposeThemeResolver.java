package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 优先匹配主题中的模板，如果没有匹配到则采用插件 resource 中的模板.
 *
 * @author: pan
 **/
@Component
public class ComposeThemeResolver implements TemplateResolver {

    private final TemplateResolver resource;
    private final TemplateResolver theme;

    public ComposeThemeResolver(MailPluginCacheTemplateResolver resource,
                                ThemeTemplateResolver theme) {
        this.resource = resource;
        this.theme = theme;
    }

    @Override
    public Mono<String> processReactive(CommentTemplateType template, ReplyCommentContext variables) {
        return theme.processReactive(template, variables)
                .switchIfEmpty(Mono.defer(() -> resource.processReactive(template, variables)));
    }
}
