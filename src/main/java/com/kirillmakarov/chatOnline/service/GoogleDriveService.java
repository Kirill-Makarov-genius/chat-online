package com.kirillmakarov.chatOnline.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

@Service
public class GoogleDriveService {

    public Drive getDriveService(OAuth2AuthorizedClient authorizedClient){
        // Get a token from oauth in spring
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        // Create google credentials to interact with Google services
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null));

        // Creating instance of tool to interact with Google Drive API
        return new Drive.Builder(
                // Instance that makes the network calls
                new NetHttpTransport(),
                // Tool for reading JSON data for send to Google
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Chat and Watch Online")
                .build();
    }

}
