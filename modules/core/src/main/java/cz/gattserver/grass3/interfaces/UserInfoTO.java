package cz.gattserver.grass3.interfaces;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import cz.gattserver.grass3.security.Role;

/**
 * @author gatt
 */
public class UserInfoTO implements UserDetails {

	private static final long serialVersionUID = -3792334399923911589L;

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
	private Set<Role> roles = EnumSet.noneOf(Role.class);

	/**
	 * Datum registrace
	 */
	private LocalDateTime registrationDate;

	/**
	 * Datum posledního přihlášení
	 */
	private LocalDateTime lastLoginDate;

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

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public boolean isEnabled() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return isConfirmed();
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public String getUsername() {
		return getName();
	}

	public String getPassword() {
		return password;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getRoles();
	}

	public boolean hasRole(Role role) {
		return getRoles().contains(role);
	}

	public boolean isAdmin() {
		return hasRole(Role.ADMIN);
	}

	@Override
	public String toString() {
		return "Name: " + String.valueOf(name) + " Roles: " + roles.toString();
	}

}
