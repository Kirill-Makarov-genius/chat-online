package com.kirillmakarov.chatOnline.controller;

import com.kirillmakarov.chatOnline.dto.RoomDto;
import com.kirillmakarov.chatOnline.entity.Room;
import com.kirillmakarov.chatOnline.enums.RoomStatus;
import com.kirillmakarov.chatOnline.repository.RoomRepository;
import com.kirillmakarov.chatOnline.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/local-video")
@RequiredArgsConstructor
public class LocalVideoController {

    private final RoomRepository roomRepository;

    @GetMapping("/{roomId}")
    public ResponseEntity<ResourceRegion> streamLocal(
            @PathVariable String roomId,
            @RequestHeader HttpHeaders headers) throws IOException {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        File file = new File(room.getLocalPath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 1. Используем FileSystemResource
        Resource video = new FileSystemResource(file);
        long contentLength = file.length();

        // 2. Получаем диапазон из заголовков (Range)
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);

        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);

            // Ограничиваем чанк (например, 1MB), чтобы видео грузилось плавно
            long rangeLength = Math.min(1024 * 1024L, end - start + 1);
            ResourceRegion region = new ResourceRegion(video, start, rangeLength);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(region);
        } else {
            // Если Range нет, отдаем первый мегабайт (браузер потом сам запросит остальное)
            long rangeLength = Math.min(1024 * 1024L, contentLength);
            ResourceRegion region = new ResourceRegion(video, 0, rangeLength);

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(region);
        }
    }
}
