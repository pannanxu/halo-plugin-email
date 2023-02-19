package io.mvvm.halo.plugins.email.template;

import io.mvvm.halo.plugins.email.comment.CommentTemplateType;
import io.mvvm.halo.plugins.email.comment.ReplyCommentContext;
import io.mvvm.halo.plugins.email.support.MailEnvironmentFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ConcurrentLruCache;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * ThemeTemplateResolver.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class ThemeTemplateResolver implements TemplateResolver {
    private static final int CACHE_SIZE_LIMIT = 5;

    private final ConcurrentLruCache<String, String> engineCache;
    private final MailEnvironmentFetcher fetcher;
//    private final ThemeRootGetter themeRootGetter;

    // TODO ThemeRootGetter 暂未提供此 Bean
    public ThemeTemplateResolver(MailEnvironmentFetcher fetcher/*, ThemeRootGetter themeRootGetter*/) {
        this.fetcher = fetcher;
//        this.themeRootGetter = themeRootGetter;
        engineCache = new ConcurrentLruCache<>(CACHE_SIZE_LIMIT, this::templateEngineGenerator);
    }

    private String templateEngineGenerator(String filePath) {
//        String path = PathUtils.combinePath(themeRootGetter.get().toString(), filePath);
//        return FileUtils.readFilePathAsString(filePath);
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
