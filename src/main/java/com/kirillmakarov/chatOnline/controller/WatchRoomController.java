package com.kirillmakarov.chatOnline.controller;


import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.kirillmakarov.chatOnline.dto.RoomDto;
import com.kirillmakarov.chatOnline.service.CustomUserDetailsService;
import com.kirillmakarov.chatOnline.service.GoogleDriveService;
import com.kirillmakarov.chatOnline.service.RoomService;
import com.kirillmakarov.chatOnline.service.VideoConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/watch")
@RequiredArgsConstructor
public class WatchRoomController {

    private final RoomService roomService;
    private final CustomUserDetailsService userService;
    private final GoogleDriveService googleDriveService;
    private final VideoConversionService videoConversionService;




    @GetMapping("/manage")
    public String manageMyRooms(Principal principal, Model model){
        String curUsername = principal.getName();

        List<RoomDto> listOfRooms = roomService.findAllRoomsByHostUsername(curUsername);

        model.addAttribute("listOfRooms", listOfRooms);

        return "manage-rooms";

    }

    @PostMapping("/delete")
    public String deleteRoom(@RequestParam String roomId, Principal principal){
        roomService.deleteRoomById(roomId, principal.getName());
        return "redirect:manage";
    }


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
            Principal principal) throws IOException{

        String userName = principal.getName();

        RoomDto roomDto = roomService.createRoom(roomName, fileId, fileName, userName);

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        // We pass the stream and the Room ID to the async service
//        videoConversionService.convertDriveVideo(roomDto.getId(), fileId, accessToken);

        return "redirect:/watch/room/" + roomDto.getId();
    }

    @GetMapping("/room/{roomId}")
    public String joinRoom(@PathVariable String roomId, Model model){
        RoomDto roomToJoin = roomService.findRoomById(roomId);
        model.addAttribute("room", roomToJoin);
        return "watch-room";
    }




}
