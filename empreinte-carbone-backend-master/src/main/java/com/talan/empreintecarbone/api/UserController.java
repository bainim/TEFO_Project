package com.talan.empreintecarbone.api;

import com.talan.empreintecarbone.Utils.Utils;
import com.talan.empreintecarbone.constant.ConfigConstant;
import com.talan.empreintecarbone.dto.UserDto;
import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.repository.UserRepository;
import com.talan.empreintecarbone.service.UserService;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@CrossOrigin(origins = { ConfigConstant.LOCAL_URL, ConfigConstant.SERVER_URL }, maxAge = 3600)
@RestController
public class UserController {

	private final UserService userService;
	private final JavaMailSender javaMailSender;
	private final UserRepository userRepository;
	private final RealmResource realm;
	private final Keycloak keycloak;
	@Value("${spring.mail.username}")
	private String contactMail;

	public UserController(@Value("${keycloak-admin.url}") String url,
			@Value("${keycloak-admin.realm}") String realmValue, @Value("${keycloak-admin.client-id}") String clientId,
			@Value("${keycloak-admin.client-secret}") String clientSecret, UserService userService,
			JavaMailSender javaMailSender, UserRepository userRepository) {
		this.userService = userService;
		this.javaMailSender = javaMailSender;
		this.userRepository = userRepository;
		this.keycloak = KeycloakBuilder.builder().serverUrl(url).grantType(OAuth2Constants.CLIENT_CREDENTIALS)
				.realm(realmValue).clientId(clientId).clientSecret(clientSecret)
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();
		this.realm = keycloak.realm(realmValue);
	}

	@RolesAllowed("admin")
	@GetMapping(value = "/users")
	public List<User> listUser() {
		return userService.findAll();
	}

	@GetMapping(value = "/users/{username}")
	public boolean userExist(@PathVariable(value = "username") String username) {
		return userService.findByUsername(username) != null;
	}

	@PostMapping(value = "/signup")
	public ResponseEntity<?> saveUser(@RequestBody UserDto user) {
		User user1 = userService.findByUsername(user.getUsername());
		if (user1 != null) {
			return ResponseEntity.status(500).body(new Error("User already exists"));
		}
		User savedUser = userService.save(user);
		return ResponseEntity.ok(savedUser);
	}

	@PostMapping(value = "/generateTokenResetPassword/{email}")
	public ResponseEntity<?> generateTokenResetPassword(@PathVariable(value = "email") String email)
			throws MessagingException {
		User currentUser = userService.findByUsername(email);
		if (currentUser != null) {
			String tokenResetPassword = Utils.randomAlphaNumeric(10);
			currentUser.setTokenResetPassword(tokenResetPassword);
			userRepository.save(currentUser);
			MimeMessage msg = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setTo(currentUser.getUsername());
			helper.setSubject("Reset password");
			String emailContent = "<html>\n" + "<head>\n" + "<style>\n" + ".div_principale a:hover {\n"
					+ "	border-bottom: 1px solid #253c93; }\n" + "</style>\n" + "</head>\n" + "<body>\n"
					+ "<div style=\"width:80%;margin-left:10%;border: 1.5px solid #1b1a19;/* outer shadows  (note the rgba is red, green, blue, alpha) */-webkit-box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.4); -moz-box-shadow: 0px 1px 6px rgba(23, 69, 88, .5);\n"
					+ "\n" + "-webkit-border-radius: 12px;\n" + "-moz-border-radius: 7px; \n" + "border-radius: 7px;\n"
					+ "\n" + "background: -webkit-gradient(linear, left top, left bottom, \n"
					+ "color-stop(0%, white), color-stop(15%, white), color-stop(100%, #D7E9F5)); \n"
					+ "background: -moz-linear-gradient(top, white 0%, white 55%, #D5E4F3 130%); \">\n"
					+ "<img src=\"https://talan.com/fileadmin/Medias/logo-talan.png\" style=\"width: 90%;height: 250;margin-left: 5%;\">\n"
					+ "<div style=\"margin-left:7%;\">\n" + "<p class=\"p\"><h3>Bonjour ,</h3>\n"
					+ "Ceci est votre code de réinitialisation de mot de passe : " + tokenResetPassword + "<br><br>\n"
					+ "Cordialement.<br>\n" + "</div>\n" + "</div>\n" + "</body>\n" + "</html>";
			helper.setText(emailContent, true);
			helper.setFrom(contactMail);
			javaMailSender.send(msg);
			return ResponseEntity.ok("Success");
		}
		return ResponseEntity.badRequest().body("Email inexistant");
	}

	private void updatePassword(String id, String pass) {
		CredentialRepresentation cred = new CredentialRepresentation();
		cred.setType(CredentialRepresentation.PASSWORD);
		cred.setValue(pass);
		cred.setTemporary(false);
		realm.users().get(id).resetPassword(cred);
	}

	@PostMapping(value = "/resetPassword/{token}")
	public ResponseEntity<?> resetPassword(@PathVariable(value = "token") String token,
			@RequestBody UserDto loginUser) {
		User user = userService.findByUsername(loginUser.getUsername());
		if (user != null) {
			if (StringUtils.equals(user.getTokenResetPassword(), token)) {
				updatePassword(user.getId().toString(), loginUser.getPassword());
				return ResponseEntity.ok("Success");
			}
			return ResponseEntity.status(401).body(new Error("Code de réinitialisation invalide"));
		}
		return ResponseEntity.status(404).body(new Error("Utilisateur invalide"));
	}

	@GetMapping(value = "/verifyEmail/{username}")
	public ResponseEntity<?> verifyEmail(@PathVariable(value = "username") String username) {
		User user = userService.findByUsername(username);
		if (user != null) {
			userService.verifyEmail(user);
			return ResponseEntity.ok("Success");
		}
		return ResponseEntity.status(404).body(new Error("Utilisateur invalide"));
	}

	@PutMapping("/updateUserConnexionDateAndVersion/{version}")
	public ResponseEntity<?> updateUserConnexionDateAndVersion(@PathVariable String version,
			Authentication authentication) {

		User currentUser = userService.findOne(authentication.getName());
		if (currentUser != null) {
			userService.updateUserConnexionDateAndVersion(currentUser, version);
			return ResponseEntity.ok("Success");
		}
		return ResponseEntity.status(404).body(new Error("Utilisateur invalide"));

	}

	@PostMapping(value = "/changePassword")
	public ResponseEntity<?> changePassword(@RequestBody UserDto loginUser,Authentication authentication) {
		User user = userService.findOne(authentication.getName());
		if (user != null && loginUser.getPassword() != null) {
			updatePassword(user.getId().toString(), loginUser.getPassword());
			return ResponseEntity.ok("Success");
		}
		return ResponseEntity.status(404).body(new Error("Utilisateur invalide"));
	}
}
