package com.talan.empreintecarbone.service.impl;

import com.talan.empreintecarbone.dto.UserDto;
import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.repository.UserRepository;
import com.talan.empreintecarbone.service.RouteService;
import com.talan.empreintecarbone.service.UserService;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
@Service
public class UserServiceImpl implements UserService {

	@Value("${keycloak-admin.url}")
	private String url;
	@Value("${keycloak-admin.realm}")
	private String realm;
	@Value("${keycloak-admin.client-id}")
	private String clientId;
	@Value("${keycloak-admin.client-secret}")
	private String clientSecret;

	private final UserRepository userRepository;
	private final RouteService routeService;

	public UserServiceImpl(UserRepository userRepository, RouteService routeService) {
		this.userRepository = userRepository;
		this.routeService = routeService;
	}

	@Override
	public List<User> findAll() {
		List<User> list = new ArrayList<>();
		userRepository.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public User findOne(String id) {
		Optional<User> user = userRepository.findById(UUID.fromString(id));
		return user.orElse(null);
	}

	@Override
	public User findByUsername(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		return user.orElse(null);
	}

	@Override
	public User save(UserDto user) {
		/* Keycloak client set user */
		Keycloak keycloak = KeycloakBuilder.builder().serverUrl(url).grantType(OAuth2Constants.CLIENT_CREDENTIALS)
				.realm(realm).clientId(clientId).clientSecret(clientSecret)
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

		CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());
		UsersResource users = keycloak.realm(realm).users();
		UserRepresentation kcUser = new UserRepresentation();
		kcUser.setUsername(user.getUsername());
		kcUser.setEmail(user.getUsername());
		kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
		// kcUser.setRequiredActions(Collections.singletonList("VERIFY_EMAIL"));
		kcUser.setEnabled(true);
		kcUser.setEmailVerified(false);

		Response response = users.create(kcUser);
		String userId = CreatedResponseUtil.getCreatedId(response); // get user id
		UserResource userResource = users.get(userId);
		userResource.executeActionsEmail(Collections.singletonList("VERIFY_EMAIL"));

		/* save user in db */
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setId(UUID.fromString(userId));
		User savedUser = userRepository.save(newUser);

		// create a route "remote" in the DB
		routeService.createRemoteRoute(savedUser);
		return savedUser;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	private static CredentialRepresentation createPasswordCredentials(String password) {
		CredentialRepresentation passwordCredentials = new CredentialRepresentation();
		passwordCredentials.setTemporary(false);
		passwordCredentials.setType(CredentialRepresentation.PASSWORD);
		passwordCredentials.setValue(password);
		return passwordCredentials;
	}

	@Override
	public void verifyEmail(User user) {
		/* Keycloak client set user */
		Keycloak keycloak = KeycloakBuilder.builder().serverUrl(url).grantType(OAuth2Constants.CLIENT_CREDENTIALS)
				.realm(realm).clientId(clientId).clientSecret(clientSecret)
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
		UserResource kcUser = keycloak.realm(realm).users().get(user.getId().toString());
		kcUser.sendVerifyEmail();
	}

	@Override
	public void updateUserConnexionDateAndVersion(User user, String version) {
		user.setLastConnexionDate(LocalDateTime.now(ZoneId.of("Europe/Paris")));
		user.setAppVersion(version);
		userRepository.save(user);

	}

}
