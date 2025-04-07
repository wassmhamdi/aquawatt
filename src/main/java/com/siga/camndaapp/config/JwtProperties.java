package com.siga.camndaapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author MHAMDI Wassim 27/02/2025
 * SIGA'S Product
 */

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secretKey;
    private long refreshExpirationMs;
}
