package cz.gattserver.grass3.facades;

import cz.gattserver.grass3.model.dto.UserInfoDTO;

public interface ISecurityFacade {

	public boolean login(String username, String password);

	public UserInfoDTO getCurrentUser();
}
