package org.myftp.gattserver.jpatest;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "TEST_USER", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class User implements Serializable {

	private static final long serialVersionUID = 1370519912799856102L;

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
	 * Datum registrace
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REGISTRATION_DATE")
	private Date registrationDate;

	/**
	 * Datum posledního přihlášení
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_LOGIN_DATE")
	private Date lastLoginDate;

	/**
	 * Email
	 */
	@Column(nullable = false)
	private String email;

	/**
	 * Je uživatelův účet potvrzen ?
	 */
	private Boolean confirmed = false;

	/**
	 * Oblíbené obsahy
	 */
	@ManyToMany
	private Set<ContentNode> favourites;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	public Set<ContentNode> getFavourites() {
		return favourites;
	}

	public void setFavourites(Set<ContentNode> favourites) {
		this.favourites = favourites;
	}

	
}
