package com.kirillmakarov.chatOnline.entity;


import com.kirillmakarov.chatOnline.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    private RoomStatus status = RoomStatus.PROCESSING;

    private String localPath;

    @ManyToOne
    @JoinColumn(name="creator_id")
    private User creator;

    private String fileId;
    private String fileName;
    @Column(name="playback_time")
    private double currentTime;
    private boolean isPlaying;



    @CreationTimestamp
    private LocalDateTime createdAt;












}
