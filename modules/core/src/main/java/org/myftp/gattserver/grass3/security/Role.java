package org.myftp.gattserver.grass3.security;

public enum Role {

	ADMIN("Admin"), USER("Uživatel"), FRIEND("Host"), AUTHOR("Autor");

	private String roleName;

	private Role(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}
}
