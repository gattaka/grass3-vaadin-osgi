package cz.gattserver.grass3.model.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

import cz.gattserver.grass3.security.Role;

@Entity(name = "USER_ACCOUNTS")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class User implements Serializable {

	private static final long serialVersionUID = 1370519912799856102L;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	/**
	 * Jméno uživatele
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * Heslo uživatele
	 */
	@Column(nullable = false)
	private String password;

	/**
	 * Role uživatele
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Set<Role> roles = EnumSet.noneOf(Role.class);

	/**
	 * Datum registrace
	 */
	@Column(name = "REGISTRATION_DATE")
	private LocalDateTime registrationDate;

	/**
	 * Datum posledního přihlášení
	 */
	@Column(name = "LAST_LOGIN_DATE")
	private LocalDateTime lastLoginDate;

	/**
	 * Oblíbené obsahy
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<ContentNode> favourites;

	/**
	 * Email
	 */
	@Column(nullable = false)
	private String email;

	/**
	 * Je uživatelův účet potvrzen ?
	 */
	private Boolean confirmed = false;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}

	public LocalDateTime getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(LocalDateTime lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public Set<ContentNode> getFavourites() {
		return favourites;
	}

	public void setFavourites(Set<ContentNode> favourites) {
		this.favourites = favourites;
	}

}
