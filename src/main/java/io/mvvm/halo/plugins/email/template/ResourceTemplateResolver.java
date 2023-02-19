package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.MailPlugin;
import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import io.mvvm.halo.plugins.email.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

/**
 * ResourceTemplateResolver.
 *
 * @author: pan
 **/
@Slf4j
public class ResourceTemplateResolver implements TemplateResolver {
    private final ClassLoader classLoader;

    public ResourceTemplateResolver() {
        this.classLoader = MailPlugin.class.getClassLoader();
    }

    public ResourceTemplateResolver(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Mono<String> processReactive(CommentTemplateType template, ReplyCommentContext variables) {
        ClassPathResource resource = new ClassPathResource(template.getDefinition().path(), classLoader);
        return Mono.just(FileUtils.readResourceAsString(resource));
    }
}
