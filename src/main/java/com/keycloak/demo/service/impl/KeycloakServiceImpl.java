package com.keycloak.demo.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.http.HttpStatus;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.keycloak.demo.controller.dto.UserDTO;
import com.keycloak.demo.service.IKeycloakService;
import com.keycloak.demo.util.KeycloakProvider;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KeycloakServiceImpl implements IKeycloakService {

	/**
	 * Método para listar todos los usarios de Keycloak
	 * @return List<UserRepresentation>
	 */
	@Override
	public List<UserRepresentation> findAllUsers() {
		return KeycloakProvider.getRealmResource()
				.users()
				.list();
	}

	/**
	 * Método para buscar un suario por el username
	 * @param String con el username a buscar
	 * @return List<UserRepresentation>
	 */
	@Override
	public List<UserRepresentation> searchUserByUsername(String username) {
		return KeycloakProvider.getRealmResource()
				.users()
				.searchByUsername(username, true);
	}

	/**
	 * Método para crear un usuario nuevo en Keycloak
	 * @param UsrerDTO con los datos del usario
	 * @return String indicando el resultado de la operación
	 */
	@Override
	public String createUser(@NonNull UserDTO userDTO) {
		int status = 0;
		UsersResource userResource = KeycloakProvider.getUserResource();
		
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setFirstName(userDTO.firstName());
		userRepresentation.setLastName(userDTO.lastName());
		userRepresentation.setEmail(userDTO.email());
		userRepresentation.setEmailVerified(true);
		userRepresentation.setUsername(userDTO.username());
		userRepresentation.setEnabled(true);
		
		Response response = userResource.create(userRepresentation);
		status = response.getStatus();
		if (status == HttpStatus.SC_CREATED) {
			String path = response.getLocation().getPath();
			String userId = path.substring(path.lastIndexOf("/") + 1);
	
			CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
			credentialRepresentation.setTemporary(false);
			credentialRepresentation.setType(OAuth2Constants.PASSWORD);
			credentialRepresentation.setValue(userDTO.password());
			
			userResource.get(userId).resetPassword(credentialRepresentation);
			
			RealmResource realmResource = KeycloakProvider.getRealmResource();
			
			List<RoleRepresentation> roleRepresentations = null;
			if (userDTO.roles() == null || userDTO.roles().isEmpty()) {
				roleRepresentations = List.of(realmResource.roles().get("user").toRepresentation());
			} else {
				roleRepresentations = realmResource.roles()
						.list()
						.stream()
						.filter(role -> userDTO.roles()
								.stream()
								.anyMatch(rolename -> rolename.equalsIgnoreCase(role.getName())))
						.toList();
			}
			realmResource.users().get(userId).roles().realmLevel().add(roleRepresentations);
			
			return "User created successfully!";
		} else if (status == HttpStatus.SC_CONFLICT) {
			log.error("User exists already!");
			return "User exists already!";
		} else {
			log.error("Error creating user, please contact with administrator!");
			return "Error creating user, please contact with administrator!";
		}
	}
	
	/**
	 * Método que elimina un usuario en keycloak
	 * @param String Id del usuario a eliminar
	 */
	@Override
	public void deleteUser(String userId) {
		KeycloakProvider.getUserResource().get(userId).remove();
	}

	/**
	 * Método para actualizar un usuario en Keycloak
	 * @param String con el id del usuario a actualizar
	 * @param UserDTO con los datos del usuario
	 */
	@Override
	public void updateUser(String userId, @NonNull UserDTO userDTO) {
		CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
		credentialRepresentation.setTemporary(false);
		credentialRepresentation.setType(OAuth2Constants.PASSWORD);
		credentialRepresentation.setValue(userDTO.password());
		
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setFirstName(userDTO.firstName());
		userRepresentation.setLastName(userDTO.lastName());
		userRepresentation.setEmail(userDTO.email());
		userRepresentation.setEmailVerified(true);
		userRepresentation.setUsername(userDTO.username());
		userRepresentation.setEnabled(true);
		userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
	
		UserResource userResource = KeycloakProvider.getUserResource().get(userId);
		userResource.update(userRepresentation);
	}

}
