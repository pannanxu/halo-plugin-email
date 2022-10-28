package io.mvvm.halo.plugins.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * @description: 邮件通知模板
 * @author: pan
 **/
@GVK(group = "io.mvvm.halo.plugins.email", kind = "EmailTemplateExtension",
        version = "v1alpha1", singular = "emailTemplateExtension", plural = "emailTemplateExtensions")
@Data
@EqualsAndHashCode(callSuper = true)
public class EmailTemplateExtension extends AbstractExtension {

    private Spec spec;

    @Data
    public static class Spec {

        @Schema(required = true)
        private String pluginId;
        @Schema(required = true)
        private String template;
        @Schema(required = true)
        private boolean enable;

    }

}