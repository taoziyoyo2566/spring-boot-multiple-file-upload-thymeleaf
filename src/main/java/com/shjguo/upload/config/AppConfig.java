package com.shjguo.upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "spring")
@Validated
public class AppConfig {

    @Value("${spring.file.uploads}")
    private Path uploads;
    @Value("${spring.description}")
    private String appDescription;

    public Path getUploads() {
        return uploads;
    }

    public void setUploads(Path uploads) {
        this.uploads = uploads;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }
}
