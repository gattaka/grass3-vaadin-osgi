package org.myftp.gattserver.grass3.model.dto;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import org.myftp.gattserver.grass3.security.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami. Obsahuje pouze
 * "povrchové" informace a vynechává tak různé reference jako reference na
 * oblíbené odkazy. Snižuje tak přenost z DB. Toto rozdělení je lepší než tam
 * nechávat null hodnoty.
 * 
 * @author gatt
 * 
 */
public class UserInfoDTO implements UserDetails {

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
		return false;
	}

	public boolean isAccountNonLocked() {
		return this.isConfirmed();
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public String getUsername() {
		return this.getName();
	}

	public String getPassword() {
		return password;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.getRoles();
	}

}
