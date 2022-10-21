package io.mvvm.halo.plugins.email.process;

import io.mvvm.halo.plugins.email.AbstractTemplateProcess;
import io.mvvm.halo.plugins.email.EmailTemplateEngineManager;
import io.mvvm.halo.plugins.email.EmallSendEndpoint;
import io.mvvm.halo.plugins.email.EmailMessage;
import io.mvvm.halo.plugins.email.EmailTemplateOptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;
import run.halo.app.core.extension.Post;

/**
 * @description:
 * @author: pan
 **/
@Slf4j
public class PostExtensionTemplateProcess extends AbstractTemplateProcess {

    public PostExtensionTemplateProcess(EmailTemplateEngineManager engineManager) {
        setEngineManager(engineManager);
    }

    @Override
    public String getEndpoint() {
        return EmallSendEndpoint.ExtensionAdd.name();
    }

    @Override
    public Flux<EmailMessage> process(Object extension) {
        if (extension instanceof Post comment) {
            Context context = new Context();
            context.setVariable("post", comment);
            String content = contentParse(EmailTemplateOptionEnum.Comment.getOption().name(), context);
            return Flux.just(new EmailMessage("2369710264@qq.com", "收到新的评论", content));
        }
        return Flux.empty();
    }
}