package org.myftp.gattserver.grass3.model.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.myftp.gattserver.grass3.security.Role;

@Entity
@Table(name = "USER_ACCOUNTS", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class User {

	/**
	 * Jméno uživatele
	 */
	private String name;

	/**
	 * Heslo uživatele
	 */
	private String password;

	/**
	 * Role uživatele
	 */
	private Set<Role> roles = new HashSet<Role>();

	/**
	 * Datum registrace
	 */
	private Date registrationDate;

	/**
	 * Datum posledního přihlášení
	 */
	private Date lastLoginDate;

	/**
	 * Oblíbené obsahy
	 */
	private Set<ContentNode> favourites;

	/**
	 * Email
	 */
	private String email;

	/**
	 * Je uživatelův účet potvrzen ?
	 */
	private Boolean confirmed = false;

	/**
	 * DB identifikátor
	 */
	private Long id;

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
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

	@ElementCollection
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REGISTRATION_DATE")
	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_LOGIN_DATE")
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
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

	@ManyToMany
	public Set<ContentNode> getFavourites() {
		return favourites;
	}

	public void setFavourites(Set<ContentNode> favourites) {
		this.favourites = favourites;
	}

}
