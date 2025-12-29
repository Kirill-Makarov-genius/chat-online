package com.kirillmakarov.chatOnline.service;

import com.kirillmakarov.chatOnline.dto.RoomDto;
import com.kirillmakarov.chatOnline.entity.Room;
import com.kirillmakarov.chatOnline.entity.User;
import com.kirillmakarov.chatOnline.exception.NotUniqueIdRoom;
import com.kirillmakarov.chatOnline.exception.RoomNotFoundException;
import com.kirillmakarov.chatOnline.exception.UserNotFoundException;
import com.kirillmakarov.chatOnline.mapper.RoomMapper;
import com.kirillmakarov.chatOnline.repository.RoomRepository;
import com.kirillmakarov.chatOnline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMapper roomMapper;
    public RoomDto createRoom(String roomName, String fileId, String fileName, String creatorUsername){

        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new UserNotFoundException("User - " + creatorUsername + " not found"));

        Room room = new Room();
        room.setName(roomName);
        room.setCreator(creator);
        room.setFileId(fileId);
        room.setFileName(fileName);
        room.setCurrentTime(0.0);
        room.setPlaying(false);

        saveRoomWithUniqueId(room);

        return roomMapper.toDto(room);

    }


    public RoomDto findRoomById(String roomId){
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room with id - " + roomId + " not found"));

        return roomMapper.toDto(room);
    }
    @Cacheable(value = "roomCache", key="#roomId")
    public RoomDto findRoomByIdForCached(String roomId){
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room with id - " + roomId + " not found"));

        return roomMapper.toDto(room);
    }

    private Room saveRoomWithUniqueId(Room room){
        int attempts = 0;
        while (attempts < 5){
            try{
                room.setId(UUID.randomUUID().toString().substring(0, 8));
                return roomRepository.saveAndFlush(room);
            } catch (DataIntegrityViolationException e) {
                attempts++;
            }
        }
        throw new NotUniqueIdRoom("Room with this id already created, try again");
    }



}
