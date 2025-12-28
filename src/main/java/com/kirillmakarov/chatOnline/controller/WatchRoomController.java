package com.kirillmakarov.chatOnline.controller;


import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.kirillmakarov.chatOnline.dto.RoomDto;
import com.kirillmakarov.chatOnline.service.CustomUserDetailsService;
import com.kirillmakarov.chatOnline.service.GoogleDriveService;
import com.kirillmakarov.chatOnline.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/watch")
@RequiredArgsConstructor
public class WatchRoomController {

    private final RoomService roomService;
    private final CustomUserDetailsService userService;
    private final GoogleDriveService googleDriveService;



    @GetMapping("/select-video")
    public String selectVideoPage(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            Model model) throws IOException{

        Drive drive = googleDriveService.getDriveService(authorizedClient);

        FileList result = drive.files().list()
                .setQ("mimeType contains 'video/' and trashed=false")
                .setFields("files(id, name, thumbnailLink)")
                .execute();

        model.addAttribute("files", result.getFiles());
        return "select-video";
    }

    @PostMapping("/create")
    public String createRoom(
            @RequestParam String fileId,
            @RequestParam String fileName,
            @RequestParam String roomName,
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            Principal principal){

        String userName = principal.getName();

        RoomDto roomDto = roomService.createRoom(roomName, fileId, fileName, userName);

        return "redirect:/watch/room/" + roomDto.getId();
    }

    @GetMapping("/room/{roomId}")
    public String joinRoom(@PathVariable String roomId, Model model){
        RoomDto roomToJoin = roomService.findRoomToJoin(roomId);
        model.addAttribute("room", roomToJoin);
        return "watch-room";
    }




}
