package cz.gattserver.grass3.songs;

import cz.gattserver.grass3.security.Role;

public enum SongsRole implements Role {

	SONGS_EDITOR("Songs editor");

	private String roleName;

	private SongsRole(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getAuthority() {
		return toString();
	}
}
