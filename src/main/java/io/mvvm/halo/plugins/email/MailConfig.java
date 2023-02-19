package io.mvvm.halo.plugins.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MailConfig.
 *
 * @author: pan
 **/
@Configuration
public class MailConfig {
    
    @Bean
    public MailHelper mailHelper() {
        return new MailHelper();
    }
    
}
