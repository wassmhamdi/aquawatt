package com.siga.camndaapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @author MHAMDI Wassim 03/03/2025
 * SIGA'S Product
 */
@Slf4j
@Configuration
public class MessageConfig {
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages.properties");  // Base name without .properties
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false); // Optional: prevents falling back if key is missing

        log.info("source --------*********-------- {} ", source);
        return source;

    }
}
