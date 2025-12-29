package com.kirillmakarov.chatOnline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
@EnableAsync
@EnableCaching
public class ChatOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatOnlineApplication.class, args);
	}

}
