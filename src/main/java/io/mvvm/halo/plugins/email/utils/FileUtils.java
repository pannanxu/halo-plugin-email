package io.mvvm.halo.plugins.email.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * FileUtils.
 *
 * @author: pan
 **/
@Slf4j
public class FileUtils {

    public static String readFilePathAsString(String path) {
        try {
            return Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("读取文件内容失败 [ %s ]".formatted(path), e);
        }
    }

    public static String readResourceAsString(ClassPathResource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("读取文件内容失败", e);
        }
    }

}
