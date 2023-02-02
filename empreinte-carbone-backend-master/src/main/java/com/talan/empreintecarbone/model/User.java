package com.talan.empreintecarbone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@ToString
@Setter
@Getter
public class User {

	@Id
	@Type(type = "pg-uuid")
	private UUID id;
	@Column(unique = true)
	private String username;
	@Column
	private String tokenResetPassword;
	@Column(nullable = true)
	private LocalDateTime lastConnexionDate;
	@Column(nullable = true)
	private String appVersion;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private Collection<Trip> trips;

}
