package com.keycloak.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/Hello-1")
	@PreAuthorize("hasRole('admin_client_role')")
	public String helloAdmin() {
		return "Hello Spring Boot with Keycloak - ADMIN";
	}

	@GetMapping("/Hello-2")
	@PreAuthorize("hasRole('admin_client_role') or hasRole('user_client_role')")
	public String helloUser() {
		return "Hello Spring Boot with Keycloak - USER";
	}

}
