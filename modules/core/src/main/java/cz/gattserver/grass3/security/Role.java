package cz.gattserver.grass3.security;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

	ADMIN("Admin"), USER("Uživatel"), FRIEND("Host"), AUTHOR("Autor");

	private String roleName;

	private Role(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getAuthority() {
		return toString();
	}
}
