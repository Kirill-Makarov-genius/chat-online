package com.example.chatOnline.service;


import com.example.chatOnline.exception.FileTooLargeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final long MAX_FILE_SIZE;
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir){
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.MAX_FILE_SIZE = 10L * 1024 * 1024;
        try{
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't create the upload directory.", ex);
        }
    }

    public String storeFile(MultipartFile file){
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) throw new RuntimeException("Invalid file");
        if (file.getSize() > MAX_FILE_SIZE){
            throw new FileTooLargeException("The file is too big! Max size is 100mb");
        }
        // Generate UUID for uploaded file and add extension
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        // Save the file to the upload-dir
        try {
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store files " + newFileName, ex);
        }
    }

    public Resource loadFileAsResource(String fileName){
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()){
                return resource;
            }
            else {
                throw new RuntimeException("File not found - " + fileName);
            }

        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found exception - " + fileName, ex);
        }
    }
}


















