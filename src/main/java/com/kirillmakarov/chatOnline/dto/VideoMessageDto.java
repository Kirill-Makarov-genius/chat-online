package com.kirillmakarov.chatOnline.dto;

import com.kirillmakarov.chatOnline.enums.VideoMessageAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoMessageDto {

    VideoMessageAction action;
    String fileId;
    double currentTime;
    boolean isPlaying;
    String sender;

}
