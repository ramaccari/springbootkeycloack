package com.keycloak.demo.controller.dto;

import java.util.Set;

import lombok.Builder;

@Builder
public record UserDTO(String username, String email, String firstName, String lastName, String password, Set<String> roles) {

}
