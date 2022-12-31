package io.mvvm.halo.plugins.email;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.BasePlugin;

/**
 * @author guqing
 * @since 2.0.0
 */
@Slf4j
@Component
public class MailPlugin extends BasePlugin {

    private final ReactiveExtensionClient client;
    private final MailWatcher mailWatcher;

    public MailPlugin(PluginWrapper wrapper,
                      ReactiveExtensionClient client,
                      MailWatcher mailWatcher) {
        super(wrapper);
        this.client = client;
        this.mailWatcher = mailWatcher;
    }

    @Override
    public void start() {
        client.watch(mailWatcher);
    }

    @Override
    public void stop() {
    }
}