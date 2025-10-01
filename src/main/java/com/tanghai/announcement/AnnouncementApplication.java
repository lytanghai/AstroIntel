package com.tanghai.announcement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnnouncementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnouncementApplication.class, args);
	}

//	@EventListener(ApplicationReadyEvent.class)
//	public void startWebSocket(BybitXauUsdClient client) {
//		client.connect();
//	}
}
