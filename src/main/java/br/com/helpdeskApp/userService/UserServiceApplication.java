package br.com.helpdeskApp.userService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = { @Server(url = "${GATEWAY_URL:http://localhost:8080/api}", description = "Gateway API"), @Server(url = "${LOCAL_URL:http://localhost:8081}", description = "Local Server") })
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
