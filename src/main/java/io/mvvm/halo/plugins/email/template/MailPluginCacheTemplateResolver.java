package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加载插件中 resource 中的模板并换成.
 *
 * @author: pan
 **/
@Component
public class MailPluginCacheTemplateResolver implements TemplateResolver {

    private final Map<String, String> cache = new ConcurrentHashMap<>(3);

    private final TemplateResolver resolver;

    public MailPluginCacheTemplateResolver() {
        this.resolver = new ResourceTemplateResolver();
    }

    @Override
    public Mono<String> processReactive(CommentTemplateType template, ReplyCommentContext variables) {
        String context = cache.get(template.getDefinition().path());
        if (!StringUtils.hasLength(context)) {
            return resolver.processReactive(template, variables)
                    .doOnNext(e -> cache.put(template.getDefinition().path(), e));
        }
        return Mono.just(context);
    }

}
