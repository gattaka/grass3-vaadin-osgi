package cz.gattserver.grass3.campgames;

import cz.gattserver.grass3.security.Role;

public enum CampgamesRole implements Role {

	CAMPGAME_EDITOR("Campgames editor");

	private String roleName;

	private CampgamesRole(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getAuthority() {
		return toString();
	}

}
