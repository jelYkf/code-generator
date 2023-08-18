package com.rune.config;

import freemarker.core.TemplateClassResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/**
 * @author sedate
 * @date 2023/7/17 16:21
 * @description
 */
@Configuration
public class FreeMarkerConfig {

    @Bean
    public freemarker.template.Configuration configuration() {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());

        // 安全处理 https://ackcent.com/blog/in-depth-freemarker-template-injection/
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
        return configuration;
    }

}
