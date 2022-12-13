package io.mvvm.halo.plugins.email;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.infra.SystemConfigurableEnvironmentFetcher;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.HaloPluginManager;

/**
 * @author guqing
 * @since 2.0.0
 */
@Slf4j
@Component
public class MailPlugin extends BasePlugin {
    public MailPublisher mailPublisher;
    public MailService mailService;

    public MailPlugin(PluginWrapper wrapper,
                      ReactiveExtensionClient client,
                      MailPublisher mailPublisher,
                      MailService mailService) {
        super(wrapper);
        this.mailPublisher = mailPublisher;
        this.mailService = mailService;
        if (getWrapper().getPluginManager() instanceof HaloPluginManager manager) {
            MailBeanContext.environmentFetcher = manager.getRootApplicationContext().getBean(SystemConfigurableEnvironmentFetcher.class);
            MailBeanContext.externalUrlSupplier = manager.getRootApplicationContext().getBean(ExternalUrlSupplier.class);
            MailBeanContext.client = client;
        }
    }

    @Override
    public void start() {
        MailBeanContext.client.watch(new MailWatcher(mailPublisher));
    }

    @Override
    public void stop() {
    }
}