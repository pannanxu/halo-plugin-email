package io.mvvm.halo.plugins.email.process;

import io.mvvm.halo.plugins.email.AbstractTemplateProcess;
import io.mvvm.halo.plugins.email.EMailTemplateEngineManager;
import io.mvvm.halo.plugins.email.EMallSendEndpoint;
import io.mvvm.halo.plugins.email.EmailMessage;
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

    public PostExtensionTemplateProcess(EMailTemplateEngineManager engineManager) {
        setEngineManager(engineManager);
    }

    @Override
    public String getEndpoint() {
        return EMallSendEndpoint.ExtensionAdd.name();
    }

    @Override
    public Flux<EmailMessage> process(Object extension) {
        if (extension instanceof Post comment) {
            Context context = new Context();
            context.setVariable("post", comment);
            String process = process(ExtensionTemplateProcessEnum.Comment.getValue(), context);
            return Flux.just(new EmailMessage("2369710264@qq.com", "收到新的评论", process));
        }
        return Flux.empty();
    }
}