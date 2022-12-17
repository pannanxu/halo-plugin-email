package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.MailPlugin;
import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

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
        InputStream in = null;
        try {
            ClassPathResource resource = new ClassPathResource(template.getDefinition().path(), classLoader);
            in = resource.getInputStream();
        } catch (IOException e) {
            log.error("加载 classpath 路径下 {} 文件失败: {}", template, e.getMessage());
            return null;
        }
        return Mono.just(new BufferedReader(new InputStreamReader(in))
                .lines()
                .collect(Collectors.joining(System.lineSeparator())));
    }
}
