package cz.gattserver.grass3.security;

import org.springframework.security.core.GrantedAuthority;

public interface Role extends GrantedAuthority {

	String getRoleName();

}
