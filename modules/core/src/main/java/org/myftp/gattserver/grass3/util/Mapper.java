package org.myftp.gattserver.grass3.util;

import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;

/**
 * Mapper pro různé typy.
 * 
 * @author gatt
 * 
 */
public enum Mapper {

	INSTANCE;

	public UserInfoDTO mapUserToUserInfoDTO(User user) {

		UserInfoDTO userInfoDTO = new UserInfoDTO();

		userInfoDTO.setConfirmed(user.isConfirmed());
		userInfoDTO.setEmail(user.getEmail());
		userInfoDTO.setId(user.getId());
		userInfoDTO.setLastLoginDate(user.getLastLoginDate());
		userInfoDTO.setName(user.getName());
		userInfoDTO.setPassword(user.getPassword());
		userInfoDTO.setRegistrationDate(user.getRegistrationDate());
		userInfoDTO.setRoles(user.getRoles());

		return userInfoDTO;

	}

}
