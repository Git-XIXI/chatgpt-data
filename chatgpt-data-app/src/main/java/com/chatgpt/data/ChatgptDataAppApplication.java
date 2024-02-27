package com.chatgpt.data;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Configurable
public class ChatgptDataAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatgptDataAppApplication.class, args);
	}

}
