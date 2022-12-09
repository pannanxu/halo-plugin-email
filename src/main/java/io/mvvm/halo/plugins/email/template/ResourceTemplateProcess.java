package io.mvvm.halo.plugins.email.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

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

    public ResourceTemplateProcess(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String process(String template, Map<String, Object> variables) {
        InputStream in = null;
        try {
            ClassPathResource resource = new ClassPathResource(template, classLoader);
            in = resource.getInputStream();
        } catch (IOException e) {
            log.error("加载 classpath 路径下 {} 文件失败: {}", template, e.getMessage(), e);
            return null;
        }
        String context = new BufferedReader(new InputStreamReader(in))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
        return TemplateEngineProcess.process(context, variables);
    }
}
