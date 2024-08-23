package com.shjguo.upload.service;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {

  public void save(MultipartFile file);
  public void saveFile(MultipartFile file);
  public Resource load(String filename);

  public boolean delete(String filename);
  
  public void deleteAll();

//  public Stream<Path> loadAll();
  public List<Path> loadAll();
}
