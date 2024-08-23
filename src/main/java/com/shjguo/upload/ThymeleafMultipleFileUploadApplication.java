package com.shjguo.upload;

import com.shjguo.upload.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.shjguo.upload.service.FilesStorageService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
public class ThymeleafMultipleFileUploadApplication {

    private static  final Logger logger = LoggerFactory.getLogger(ThymeleafMultipleFileUploadApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ThymeleafMultipleFileUploadApplication.class, args);
    }

//    @Override
//    public void run(String... arg) {
////    storageService.deleteAll();
////        storageService.init();
//        logger.info("project [{}] started ",appConfig.getAppDescription());
//        logger.info("spring boot file upload init started");
//        logger.debug("spring boot file upload path: {}",appConfig.getUploads());
//        try {
//            Files.createDirectories(appConfig.getUploads());
//        } catch (IOException e) {
//            logger.error("error message: {}",e.getLocalizedMessage());
//            logger.error("error message: {}",e.getMessage());
//            throw new RuntimeException("Could not initialize folder for upload!");
//        }
//        logger.info("spring boot file upload init success");
//    }
}
