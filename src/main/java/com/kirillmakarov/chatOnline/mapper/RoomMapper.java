package com.kirillmakarov.chatOnline.mapper;

import com.kirillmakarov.chatOnline.dto.RoomDto;
import com.kirillmakarov.chatOnline.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomDto toDto(Room room){
        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .fileId(room.getFileId())
                .fileName(room.getFileName())
                .creatorName(room.getCreator().getNickname())
                .currentTime(room.getCurrentTime())
                .isPlaying(room.isPlaying())
                .createdAt(room.getCreatedAt())
                .build();
    }

}
