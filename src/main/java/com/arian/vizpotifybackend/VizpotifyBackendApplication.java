package com.arian.vizpotifybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ConfigurationPropertiesScan
@EnableRetry
public class VizpotifyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VizpotifyBackendApplication.class, args);
	}


}
