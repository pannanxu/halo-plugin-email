package io.mvvm.halo.plugins.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @description: 加载 classpath 下的邮件模板
 * @author: pan
 **/
@Slf4j
public class ClassPathTemplateLoader implements TemplateLoader {

    @Override
    public boolean check(EmailTemplateExtension extension) {
        return extension.getSpec().getTemplate().startsWith("classpath:");
    }

    @Override
    public Mono<String> load(String template) {
        return Mono.just(loadClassPathTemplateContent(template));
    }

    private String loadClassPathTemplateContent(String template) {
        try {
            ClassPathResource resource = new ClassPathResource(template.replace("classpath:", ""), getPluginClassLoader());
            InputStream in = resource.getInputStream();
            return new BufferedReader(new InputStreamReader(in))
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("加载 classpath 路径下 " + template + "文件失败: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    protected ClassLoader getPluginClassLoader() {
        return this.getClass().getClassLoader();
    }

}