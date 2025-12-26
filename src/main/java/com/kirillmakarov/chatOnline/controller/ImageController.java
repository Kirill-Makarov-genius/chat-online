package com.kirillmakarov.chatOnline.controller;

import com.kirillmakarov.chatOnline.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file){
        String fileName = fileStorageService.storeFile(file);

        String fileUrl = "/api/images/" + fileName;

        return ResponseEntity.ok(Map.of("url", fileUrl));
    }


    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName, HttpServletRequest request){
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        String contentType = null;
        try{
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        if (contentType == null){
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
