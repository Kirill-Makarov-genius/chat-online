package com.kirillmakarov.chatOnline.dto;

import com.google.auto.value.AutoValue;
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
    String fileId;
    String fileName;
    String creatorName;
    double currentTime;
    boolean isPlaying;
    LocalDateTime createdAt;


}
