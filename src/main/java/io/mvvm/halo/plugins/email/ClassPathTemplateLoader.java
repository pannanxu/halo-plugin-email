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
    public Mono<String> load(EmailTemplateExtension extension) {
        String template = extension.getSpec().getTemplate();
        try {
            ClassPathResource resource = new ClassPathResource(template.replace("classpath:", ""),
                    getPluginClassLoader(extension.getSpec().getPluginId()));
            InputStream in = resource.getInputStream();
            return Mono.just(new BufferedReader(new InputStreamReader(in))
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator())));
        } catch (IOException e) {
            log.error("加载 classpath 路径下 {} 文件失败: {}", template, e.getMessage(), e);
            return Mono.error(e);
        }
    }

    protected ClassLoader getPluginClassLoader(String pluginId) {
        log.debug("加载 {} 插件 classpath 下的模板", pluginId);
        return EmailPluginManager.getOperator(pluginId).getPluginWrapper().getPluginClassLoader();
    }

}