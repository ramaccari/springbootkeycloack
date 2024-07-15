package com.keycloak.demo.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keycloak.demo.controller.dto.UserDTO;
import com.keycloak.demo.service.IKeycloakService;

@RestController
@RequestMapping("/keycloak/user")
@PreAuthorize("hasRole('admin_client_role')")
public class KeycloakController {
	
	@Autowired
	private IKeycloakService keycloakService;
	
	@GetMapping("/search")
	public ResponseEntity<List<UserRepresentation>> findAllUsers() {
		return ResponseEntity.ok(keycloakService.findAllUsers());
	}

	@GetMapping("/search/{username}")
	public ResponseEntity<List<UserRepresentation>> searchUserByUsername(@PathVariable String username) {
		return ResponseEntity.ok(keycloakService.searchUserByUsername(username));
	}
	
	@PostMapping("/create")
	public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) throws URISyntaxException {
		String response = keycloakService.createUser(userDTO);
		return ResponseEntity.created(new URI("/keycloak/user/create")).body(response);
	}

	@PutMapping("/update/{userId}")
	public ResponseEntity<String> updateUser(@PathVariable String userId, @RequestBody UserDTO userDTO) throws URISyntaxException {
		keycloakService.updateUser(userId, userDTO);
		return ResponseEntity.ok("User updated successfully!");
	}

	@DeleteMapping("/delete/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable String userId) throws URISyntaxException {
		keycloakService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}

}
