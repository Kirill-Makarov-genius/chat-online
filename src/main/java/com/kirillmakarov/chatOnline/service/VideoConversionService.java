package com.kirillmakarov.chatOnline.service;


import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.kirillmakarov.chatOnline.dto.VideoMessageDto;
import com.kirillmakarov.chatOnline.enums.RoomStatus;
import com.kirillmakarov.chatOnline.enums.VideoMessageAction;
import com.kirillmakarov.chatOnline.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//@Service
@RequiredArgsConstructor
public class VideoConversionService {

    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

//    @Value("${app.storage.video-temp}")
    private String storagePath;

    @Async
    public void convertDriveVideo(String roomId, String fileId, String accessToken) {
        System.out.println("Starting conversion for Room: " + roomId);

        try {
            // 1. Create a "Thread-Safe" Drive service using the token
            Drive drive = new Drive.Builder(
                    new com.google.api.client.http.javanet.NetHttpTransport(),
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                    request -> request.getHeaders().setAuthorization("Bearer " + accessToken))
                    .setApplicationName("ChatOnline")
                    .build();

            // 2. Open the stream
            InputStream driveStream = drive.files().get(fileId)
                    .setAlt("media")
                    .executeMediaAsInputStream();

            // 3. Prepare Paths (Ensure they are absolute and clean)
            Path outputDir = Paths.get(storagePath).toAbsolutePath();
            Files.createDirectories(outputDir);
            Path outputPath = outputDir.resolve(roomId + ".mp4");

            System.out.println("FFmpeg output path: " + outputPath.toString());

            // 4. FFmpeg Command
            // We use "-i -" instead of "pipe:0" (more standard)
            // We add "-f matroska" to tell FFmpeg to expect a stream (if mkv)
            String[] command = {
                    "ffmpeg",
                    "-hide_banner", // Hide unnecessary version info
                    "-i", "-",      // Read from stdin
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-b:a", "128k",
                    "-movflags", "frag_keyframe+empty_moov+default_base_moof", // Better for partial writing
                    "-y",           // Overwrite output
                    outputPath.toString()
            };

            ProcessBuilder pb = new ProcessBuilder(command);

            // CRITICAL: Merge error stream so we see FFmpeg's complaints in the console
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // 5. Start a separate thread to read and print FFmpeg's logs
            // This will tell us EXACTLY why FFmpeg is closing the stream
            Thread.ofVirtual().start(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[FFmpeg Log]: " + line);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading FFmpeg logs: " + e.getMessage());
                }
            });

            // 6. Pipe data from Google Drive -> FFmpeg
            try (OutputStream ffmpegIn = process.getOutputStream();
                 driveStream) {
                System.out.println("Beginning byte transfer...");
                driveStream.transferTo(ffmpegIn);
                ffmpegIn.flush();
            } catch (IOException e) {
                System.err.println("Transfer interrupted (FFmpeg likely closed stdin): " + e.getMessage());
            }

            int exitCode = process.waitFor();
            System.out.println("FFmpeg exited with code: " + exitCode);

            if (exitCode == 0) {
                updateRoomStatus(roomId, RoomStatus.READY, outputPath.toString().replace("\\", "/"));
            } else {
                updateRoomStatus(roomId, RoomStatus.ERROR, null);
            }

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR in Conversion Logic: " + e.getMessage());
            e.printStackTrace();
            updateRoomStatus(roomId, RoomStatus.ERROR, null);
        }
    }

    private void updateRoomStatus(String roomId, RoomStatus roomStatus, String path){
        roomRepository.findById(roomId).ifPresent(room -> {
            room.setStatus(roomStatus);
            room.setLocalPath(path);
            roomRepository.save(room);

            messagingTemplate.convertAndSend("/topic/video/" + roomId,
                    new VideoMessageDto(VideoMessageAction.STATUS_UPDATE,
                            null,
                            0,
                            false,
                            "SYSTEM"));
        });
    }
}
