package com.kirillmakarov.chatOnline.repository;

import com.kirillmakarov.chatOnline.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    @Override
    Optional<Room> findById(String roomId);
}
