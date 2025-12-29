package com.kirillmakarov.chatOnline.controller;


import com.google.api.services.drive.Drive;
import com.kirillmakarov.chatOnline.service.GoogleDriveService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoStreamController {

    private final GoogleDriveService googleDriveService;


    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> streamVideo(
            @PathVariable String fileId,
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            @RequestHeader HttpHeaders headers) throws IOException {

        if (authorizedClient == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Drive drive = googleDriveService.getDriveService(authorizedClient);
        var metadata = drive.files().get(fileId).setFields("size, mimeType").execute();
        long totalSize = metadata.getSize();


        long start = 0;
        long end = totalSize - 1;
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);
        if (range != null) {
            start = range.getRangeStart(totalSize);
            end = range.getRangeEnd(totalSize);
        }


        long CHUNK_SIZE = 5 * 1024 * 1024L;
        long actualEnd = Math.min(start + CHUNK_SIZE - 1, end);
        long contentLength = actualEnd - start + 1;

        var getRequest = drive.files().get(fileId);
        getRequest.setAlt("media");
        var httpRequest = getRequest.buildHttpRequest();
        httpRequest.getHeaders().setRange("bytes=" + start + "-" + actualEnd);
        httpRequest.getHeaders().setAcceptEncoding("identity");

        var googleResponse = httpRequest.execute();
        InputStream videoStream = googleResponse.getContent();

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + actualEnd + "/" + totalSize)
                .contentLength(contentLength)
                .body(new InputStreamResource(videoStream));
    }
}
