package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import io.mvvm.halo.plugins.email.support.MailEnvironmentFetcher;
import org.springframework.util.ConcurrentLruCache;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * ThemeTemplateResolver.
 *
 * @author: pan
 **/
public class ThemeTemplateResolver implements TemplateResolver {
    private static final int CACHE_SIZE_LIMIT = 5;

    private final ConcurrentLruCache<String, String> engineCache;
    private MailEnvironmentFetcher fetcher;

    public ThemeTemplateResolver(MailEnvironmentFetcher fetcher) {
        this.fetcher = fetcher;
        engineCache = new ConcurrentLruCache<>(CACHE_SIZE_LIMIT, this::templateEngineGenerator);
    }

    private String templateEngineGenerator(String filePath) {
//        Path path = FilePathUtils.combinePath(haloProperties.getWorkDir().toString(), THEME_WORK_DIR, filePath);
//        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
//            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
//        } catch (IOException e) {
//            return "";
//        }
        return "";
    }

    @Override
    public Mono<String> processReactive(CommentTemplateType template, ReplyCommentContext variables) {
        return fetcher.fetchActiveTheme()
                .flatMap(themeName -> {
                    String context = engineCache.get(themeName + "/" + "mail-" + template.getDefinition().path());
                    return !StringUtils.hasLength(context) ? Mono.empty() : Mono.just(context);
                });

    }
}
