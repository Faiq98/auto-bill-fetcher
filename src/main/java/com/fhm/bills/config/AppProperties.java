package com.fhm.bills.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String baseUrl;
    private String downloadUrl;
    private String username;
    private String password;
    private String key;
    private boolean headless;
    private String mailTo;
}
