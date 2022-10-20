package io.mvvm.halo.plugins.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 邮件服务配置
 * @author: pan
 **/
@Data
@EqualsAndHashCode
public class EmailServerConfig {

    private String adminEmail;
    @Schema(required = true)
    private boolean enable = false;
    private String formName;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String protocol = "smtp";
    private boolean startTls = false;

}