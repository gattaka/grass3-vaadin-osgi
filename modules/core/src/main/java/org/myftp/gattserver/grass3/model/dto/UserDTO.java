package org.myftp.gattserver.grass3.model.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;

public class UserDTO {

	/**
	 * Jméno uživatele
	 */
	private String name;

	/**
	 * Heslo uživatele
	 */
	private String password;

	/**
	 * Datum registrace
	 */
	private Date registrationDate;

	/**
	 * Datum posledního přihlášení
	 */
	private Date lastLoginDate;

	/**
	 * Email
	 */
	private String email;

	/**
	 * Je uživatelův účet potvrzen ?
	 */
	private boolean confirmed = false;

	/**
	 * Oblíbené obsahy
	 */
	private Set<ContentNodeDTO> favourites;

	/**
	 * DB identifikátor
	 */
	private Long id;

	private Set<Role> roles = new HashSet<Role>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public UserDTO(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Set<ContentNodeDTO> getFavourites() {
		return favourites;
	}

	public void setFavourites(Set<ContentNodeDTO> favourites) {
		this.favourites = favourites;
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserDTO) {
			UserDTO user = (UserDTO) obj;
			return user.getId() == id && user.getName().equals(name);
		} else
			return false;
	}

}
