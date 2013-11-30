package org.myftp.gattserver.grass3.facades.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dao.ContentNodeRepository;
import org.myftp.gattserver.grass3.model.dao.UserRepository;
import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.model.util.Mapper;
import org.myftp.gattserver.grass3.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("userFacade")
public class UserFacadeImpl implements IUserFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

	@Resource(name = "grassPasswordEncoder")
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	/**
	 * Zkusí najít uživatele dle jména a hesla
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public UserInfoDTO getUserByLogin(String username, String password) {
		List<User> loggedUser = userRepository.findByName(username);
		if (loggedUser != null
				&& loggedUser.size() == 1
				&& loggedUser.get(0).getPassword()
						.equals(encoder.encodePassword(password, null))) {

			User user = loggedUser.get(0);
			if (user != null)
				return mapper.map(user);
		}
		return null;
	}

	/**
	 * Zaregistruje nového uživatele
	 * 
	 * @param email
	 * @param username
	 * @param password
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean registrateNewUser(String email, String username,
			String password) {

		User user = new User();
		user.setConfirmed(false);
		user.setEmail(email);
		user.setName(username);
		user.setPassword(encoder.encodePassword(password, null));
		user.setRegistrationDate(Calendar.getInstance().getTime());
		EnumSet<Role> roles = EnumSet.of(Role.USER);
		user.setRoles(roles);

		return userRepository.save(user) != null;
	}

	/**
	 * Aktivuje uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean activateUser(UserInfoDTO userDTO) {
		User user = userRepository.findOne(userDTO.getId());
		user.setConfirmed(true);
		return userRepository.save(user) != null;
	}

	/**
	 * Zablokuje uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean banUser(UserInfoDTO userDTO) {
		User user = userRepository.findOne(userDTO.getId());
		user.setConfirmed(false);
		return userRepository.save(user) != null;
	}

	/**
	 * Upraví role uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean changeUserRoles(UserInfoDTO userDTO) {
		User user = userRepository.findOne(userDTO.getId());
		user.setRoles(userDTO.getRoles());
		return userRepository.save(user) != null;
	}

	/**
	 * Vrátí všechny uživatele
	 * 
	 * @return list uživatelů
	 */
	public List<UserInfoDTO> getUserInfoFromAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserInfoDTO> infoDTOs = new ArrayList<UserInfoDTO>();
		for (User user : users) {
			infoDTOs.add(mapper.map(user));
		}
		return infoDTOs;
	}

	/**
	 * Vrátí uživatele dle jména
	 * 
	 * @param username
	 * @return
	 */
	public UserInfoDTO getUser(String username) {
		List<User> loggedUser = userRepository.findByName(username);
		if (loggedUser != null && loggedUser.size() == 1) {
			User user = loggedUser.get(0);
			if (user != null)
				return mapper.map(user);
		}
		return null;
	}

	/**
	 * Zjistí zda daný obsah je v oblíbených daného uživatele
	 */
	public boolean hasInFavourites(ContentNodeDTO contentNodeDTO,
			UserInfoDTO user) {
		return userRepository.findByIdAndFavouritesId(user.getId(),
				contentNodeDTO.getId()) != null;
	}

	/**
	 * Přidá obsah do oblíbených uživatele
	 */
	public boolean addContentToFavourites(ContentNodeDTO contentNodeDTO,
			UserInfoDTO user) {

		User userEntity = userRepository.findOne(user.getId());
		userEntity.getFavourites().add(
				contentNodeRepository.findOne(contentNodeDTO.getId()));

		return userRepository.save(userEntity) != null;
	}

	private boolean removeContentFromFavourites(User user,
			ContentNodeDTO contentNode) {
		user.getFavourites().remove(
				contentNodeRepository.findOne(contentNode.getId()));

		return userRepository.save(user) != null;
	}

	/**
	 * Odebere obsah z oblíbených uživatele
	 */
	public boolean removeContentFromFavourites(ContentNodeDTO contentNodeDTO,
			UserInfoDTO user) {
		User userEntity = userRepository.findOne(user.getId());
		return removeContentFromFavourites(userEntity, contentNodeDTO);
	}

	/**
	 * Odebere obsah z oblíbených všech uživatelů
	 */
	public boolean removeContentFromAllUsersFavourites(
			ContentNodeDTO contentNode) {

		// vymaž z oblíbených
		List<User> users = userRepository.findByFavouritesId(contentNode
				.getId());
		if (users == null)
			return false;
		for (User user : users)
			removeContentFromFavourites(user, contentNode);

		return true;
	}
}
