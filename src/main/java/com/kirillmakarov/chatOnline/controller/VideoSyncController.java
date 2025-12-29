package com.kirillmakarov.chatOnline.controller;

import com.kirillmakarov.chatOnline.dto.VideoMessageDto;
import com.kirillmakarov.chatOnline.enums.VideoMessageAction;
import com.kirillmakarov.chatOnline.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VideoSyncController {


    private final RoomRepository roomRepository;

    @MessageMapping("/videoSelect/{roomId}")
    @SendTo("/topic/video/{roomId}")
    public VideoMessageDto handleVideoSelect(@DestinationVariable String roomId, VideoMessageDto msg){
        return msg;
    }

    @MessageMapping("/video.sync/{roomId}")
    @SendTo("/topic/video/{roomId}")
    public VideoMessageDto handleVideSync(@DestinationVariable String roomId, VideoMessageDto msg){
        roomRepository.findById(roomId).ifPresent(room -> {
            room.setPlaying(VideoMessageAction.PLAY.equals(msg.getAction()));
            room.setCurrentTime(msg.getCurrentTime());
        });
        System.out.println("Syncing Room " + roomId + ": " + msg.getAction() + " at " + msg.getCurrentTime());
        return msg;
    }

}
