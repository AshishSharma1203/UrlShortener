package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("Ashish: Auth Service is running");
        System.out.println("JVM Timezone = " + java.util.TimeZone.getDefault());

    }

}
