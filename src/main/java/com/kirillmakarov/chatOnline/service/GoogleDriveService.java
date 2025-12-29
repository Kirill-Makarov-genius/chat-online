package com.kirillmakarov.chatOnline.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;

@Service
public class GoogleDriveService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    public Drive getDriveService(OAuth2AuthorizedClient authorizedClient) {
        // 1. Correctly get Instant from Spring Security
        Instant expiresAtInstant = authorizedClient.getAccessToken().getExpiresAt();

        // 2. Convert Instant to java.util.Date for Google's library
        Date expirationDate = (expiresAtInstant != null) ? Date.from(expiresAtInstant) : null;

        String accessTokenValue = authorizedClient.getAccessToken().getTokenValue();
        GoogleCredentials credentials;

        // 3. Check for Refresh Token to enable auto-refreshing
        if (authorizedClient.getRefreshToken() != null) {
            credentials = UserCredentials.newBuilder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(authorizedClient.getRefreshToken().getTokenValue())
                    .setAccessToken(new AccessToken(accessTokenValue, expirationDate))
                    .build();
        } else {
            // Fallback if no refresh token exists
            credentials = GoogleCredentials.create(new AccessToken(accessTokenValue, expirationDate));
        }

        return new Drive.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Chat and Watch Online")
                .build();
    }
}
