package cz.gattserver.grass3.facades;

import cz.gattserver.grass3.model.dto.UserInfoDTO;

public interface SecurityFacade {

	public boolean login(String username, String password, boolean remember);

	public UserInfoDTO getCurrentUser();
}
