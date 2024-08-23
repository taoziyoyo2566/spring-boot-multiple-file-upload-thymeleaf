package com.shjguo.upload.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {
  private static final Logger logger = LoggerFactory.getLogger(FilesStorageServiceImpl.class);
  @Value("${spring.file.uploads}")
  private String uploadDir;

  private Path root ;
  @PostConstruct
  public void init() {
      try {
        logger.info("upload dir: {}",uploadDir);

          Files.createDirectories(Path.of(uploadDir));
      } catch (IOException e) {
          logger.error("upload directory create failed!");
          throw new RuntimeException(e);
      }
      root = Paths.get(uploadDir);
    logger.info("upload dir: {}",root);
  }

  @Async
  public void saveFile(MultipartFile file) {
    try {

      String filename = sanitizeFilename(Objects.requireNonNull(file.getOriginalFilename()));
      Path targetLocation = this.root.resolve(filename);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      CompletableFuture.completedFuture("File saved successfully at " + targetLocation);
    } catch (Exception e) {
      throw new RuntimeException("Failed to store file " + e.getMessage(), e);
    }
  }

  private String sanitizeFilename(String filename) {
    return filename.replaceAll("[^.a-zA-Z0-9_-]", "");
  }

  @Override
  public void save(MultipartFile file) {
    try {
      Files.copy(file.getInputStream(), this.root.resolve(Objects.requireNonNull(file.getOriginalFilename())));
    } catch (Exception e) {
      if (e instanceof FileAlreadyExistsException) {
        throw new RuntimeException("Filename already exists.");
      }

      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public Resource load(String filename) {
    try {
      Path file = root.resolve(filename);
      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read the file!");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public boolean delete(String filename) {
    try {
      Path file = root.resolve(filename);
      return Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(root.toFile());
  }

  @Override
  public List<Path> loadAll() throws RuntimeException {
    try (Stream<Path> stream = Files.walk(this.root,1).filter(path -> !path.equals(this.root)).map(this.root::relativize)) {
      return stream.collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Error reading files", e);
    }
  }
}
