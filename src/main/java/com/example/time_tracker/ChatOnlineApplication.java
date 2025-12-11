package com.example.time_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class ChatOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatOnlineApplication.class, args);
	}

}
