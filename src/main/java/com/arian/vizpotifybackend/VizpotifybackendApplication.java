package com.arian.vizpotifybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VizpotifybackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VizpotifybackendApplication.class, args);
	}

}
