package org.myftp.gattserver.grass3.util;

import org.myftp.gattserver.grass3.model.domain.Quote;
import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.dto.QuoteDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;

/**
 * Mapper pro různé typy.
 * 
 * @author gatt
 * 
 */
public enum Mapper {

	INSTANCE;

	/**
	 * Převede {@link User} na {@link UserInfoDTO}
	 * 
	 * @param user
	 * @return
	 */
	public UserInfoDTO map(User user) {
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

	/**
	 * Převede {@link Quote} na {@link QuoteDTO}
	 * 
	 * @param quote
	 * @return
	 */
	public QuoteDTO map(Quote quote) {
		QuoteDTO quoteDTO = new QuoteDTO();

		quoteDTO.setId(quote.getId());
		quoteDTO.setName(quote.getName());

		return quoteDTO;
	}

}
