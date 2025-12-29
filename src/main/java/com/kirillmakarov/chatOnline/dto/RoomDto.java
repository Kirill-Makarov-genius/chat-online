package com.kirillmakarov.chatOnline.dto;

import com.google.auto.value.AutoValue;
import com.kirillmakarov.chatOnline.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {

    String id;
    String name;
    RoomStatus status;
    String localPath;
    String fileId;
    String fileName;
    String creatorName;
    double currentTime;
    boolean isPlaying;
    LocalDateTime createdAt;


}
