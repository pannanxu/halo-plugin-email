package io.mvvm.halo.plugins.email;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * @description: 邮件通知配置
 * @author: pan
 **/
@GVK(group = "io.mvvm.halo.plugins.email", kind = "EmailConfigExtension",
        version = "v1alpha1", singular = "emailConfigExtension", plural = "emailConfigExtensions")
@Data
@EqualsAndHashCode(callSuper = true)
public class EmailConfigExtension extends AbstractExtension {

    private boolean enable;
    private String formName;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String protocol = "smtp";
    private boolean startTls = false;

}