package io.mvvm.halo.plugins.email;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Extension;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.Watcher;
import run.halo.app.plugin.BasePlugin;

/**
 * @author guqing
 * @since 2.0.0
 */
@Slf4j
@Component
public class EMailPlugin extends BasePlugin {
    private final SchemeManager schemeManager;
    private final ReactiveExtensionClient client;
    private final IEMailService mailService;
    private final ApplicationEventPublisher publisher;

    public EMailPlugin(PluginWrapper wrapper, SchemeManager schemeManager,
                       ReactiveExtensionClient client,
                       IEMailService mailService,
                       ApplicationEventPublisher publisher) {
        super(wrapper);
        this.schemeManager = schemeManager;
        this.client = client;
        this.mailService = mailService;
        this.publisher = publisher;
    }

    @Override
    public void start() {

        schemeManager.register(EmailTemplateExtension.class);
        schemeManager.register(EmailConfigExtension.class);

        client.watch(new Watcher() {
            @Override
            public void onAdd(Extension extension) {
                log.info("Watcher onAdd: {} ", extension.toString());
                Watcher.super.onAdd(extension);

                mailService.send(new EMailRequestPayload(EMallSendEndpoint.ExtensionAdd.name(), extension));

                if (extension instanceof EmailConfigExtension configExtension) {
                    publisher.publishEvent(configExtension);
                }

            }

            @Override
            public void onUpdate(Extension oldExtension, Extension newExtension) {
                log.info("Watcher onUpdate: {}, {} ", oldExtension.toString(), newExtension.toString());
                Watcher.super.onUpdate(oldExtension, newExtension);

                mailService.send(new EMailRequestPayload(EMallSendEndpoint.ExtensionUpdate.name(), newExtension));

                if (newExtension instanceof EmailConfigExtension extension) {
                    publisher.publishEvent(extension);
                }

            }


            @Override
            public void dispose() {
                log.info("Watcher dispose");
            }
        });
    }

    @Override
    public void stop() {
        schemeManager.unregister(schemeManager.get(EmailTemplateExtension.class));
        schemeManager.unregister(schemeManager.get(EmailConfigExtension.class));
    }
}