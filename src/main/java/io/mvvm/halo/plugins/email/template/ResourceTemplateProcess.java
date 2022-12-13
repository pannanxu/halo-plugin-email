package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.MailPlugin;
import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassPathTemplateProcess.
 *
 * @author: pan
 **/
@Slf4j
public class ResourceTemplateProcess {

    private final ClassLoader classLoader;

    public ResourceTemplateProcess() {
        this.classLoader = MailPlugin.class.getClassLoader();
    }

    public ResourceTemplateProcess(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String process(String template, Map<String, Object> variables) {
        InputStream in = null;
        try {
            ClassPathResource resource = new ClassPathResource(template, classLoader);
            in = resource.getInputStream();
        } catch (IOException e) {
            log.error("加载 classpath 路径下 {} 文件失败: {}", template, e.getMessage());
            return null;
        }
        String context = new BufferedReader(new InputStreamReader(in))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
        return TemplateEngineProcess.process(context, variables);
    }

    public Mono<String> processReactive(CommentTemplateType template, ReplyCommentContext variables) {
        return Mono.just(template.getDefinition().path())
                .flatMap(this::readClassPathFileContext)
                .map(fileContext -> TemplateEngineProcess.process(fileContext, variables));
    }

    Mono<String> readClassPathFileContext(String filePath) {
        InputStream in = null;
        try {
            ClassPathResource resource = new ClassPathResource(filePath, classLoader);
            in = resource.getInputStream();
        } catch (IOException e) {
            log.error("加载 classpath 路径下 {} 文件失败: {}", filePath, e.getMessage());
            return Mono.empty();
        }
        String context = new BufferedReader(new InputStreamReader(in))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
        return Mono.just(context);
    }
}
