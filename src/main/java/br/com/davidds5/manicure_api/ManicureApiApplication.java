package br.com.davidds5.manicure_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class ManicureApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManicureApiApplication.class, args);
	}

}
