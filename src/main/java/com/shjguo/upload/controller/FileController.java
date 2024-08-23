package com.shjguo.upload.controller;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shjguo.upload.model.FileInfo;
import com.shjguo.upload.service.FilesStorageService;

@Controller
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FilesStorageService storageService;
    public FileController(FilesStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String homepage() {
        logger.info("home page");
        return "redirect:/files";
    }

    @GetMapping("/files/new")
    public String newFile(Model model) {
      logger.info("upload form");
        return "upload_form";
    }

    @PostMapping("/files/upload")
    public String uploadFiles(Model model, @RequestParam("files") MultipartFile[] files) {
        logger.info("upload files parameters {},{}",model,files);
        List<String> messages = new ArrayList<>();

        Arrays.stream(files).forEach(file -> {
            try {
                storageService.saveFile(file);
                messages.add(file.getOriginalFilename() + " [Successful]");
            } catch (Exception e) {
                logger.error("file name: {}",file.getOriginalFilename());
                logger.error("{} <Failed> - {}", file.getOriginalFilename(), e.getMessage());
                messages.add(file.getOriginalFilename() + " <Failed> - " + e.getMessage());
            }
        });

        model.addAttribute("messages", messages);
        return "upload_form";
    }

    @GetMapping("/files")
    public String getListFiles(Model model) {

        // Load all paths and immediately collect them to a list
        List<Path> paths = storageService.loadAll();
        // Map the paths to FileInfo objects
        List<FileInfo> fileInfos = paths.stream().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FileController.class, "getFile", path.getFileName().toString())
                    .build().toUri().toString();
            return new FileInfo(filename, url);
        }).collect(Collectors.toList());
        logger.debug("files: {}", fileInfos);
        model.addAttribute("files", fileInfos);
        return "files";

    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/files/delete/{filename:.+}")
    public String deleteFile(@PathVariable String filename, Model model, RedirectAttributes redirectAttributes) {
        try {
            boolean existed = storageService.delete(filename);

            if (existed) {
                redirectAttributes.addFlashAttribute("message", "Delete the file successfully: " + filename);
            } else {
                redirectAttributes.addFlashAttribute("message", "The file does not exist!");
            }
        } catch (Exception e) {
            logger.error("Could not delete the file: {}. Error: {}", filename, e.getMessage());
            redirectAttributes.addFlashAttribute("message",
                    "Could not delete the file: " + filename + ". Error: " + e.getMessage());
        }
        return "redirect:/files";
    }
}
