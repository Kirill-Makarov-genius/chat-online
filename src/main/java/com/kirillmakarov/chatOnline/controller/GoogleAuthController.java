package com.kirillmakarov.chatOnline.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleAuthController {


    @GetMapping("/google-link-success")
    public String linkSuccess(@RegisteredOAuth2AuthorizedClient("google")OAuth2AuthorizedClient authorizedClient,
                              Authentication authentication){
        return authentication.toString() + "\n" + authorizedClient.toString();
    }

}
