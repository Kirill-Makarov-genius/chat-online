package com.kirillmakarov.chatOnline.controller;


import com.google.api.services.drive.Drive;
import com.kirillmakarov.chatOnline.service.GoogleDriveService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoStreamController {

    private final GoogleDriveService googleDriveService;

    @GetMapping("/{fileId}")
    public ResponseEntity<ResourceRegion> streamVideo(@PathVariable String fileId,
                                                      @RegisteredOAuth2AuthorizedClient("google")OAuth2AuthorizedClient authorizedClient,
                                                      @RequestHeader HttpHeaders headers) throws IOException{

        Drive drive = googleDriveService.getDriveService(authorizedClient);

        // Get metadata to know the total size
        long contentLength = drive.files().get(fileId).setFields("size").execute().getSize();

        // We are using Resource to get data by chunks
        // Create a Resource that pulls from Google Drive
        // executeMediaAsInputStream() doesn't download the whole file, it only creates connection to file
        Resource videoResource = new InputStreamResource(
                drive.files().get(fileId).executeMediaAsInputStream()
        );

        // Check
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);
        if (range != null){
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            ResourceRegion region = new ResourceRegion(videoResource, start, end - start + 1);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(region);
        }
        else {
            ResourceRegion region = new ResourceRegion(videoResource, 0, contentLength);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(region);
        }




    }

}





















