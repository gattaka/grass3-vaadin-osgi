package cz.gattserver.grass3.facades.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.facades.UserFacade;
import cz.gattserver.grass3.model.dao.ContentNodeRepository;
import cz.gattserver.grass3.model.dao.UserRepository;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.model.util.CoreMapper;
import cz.gattserver.grass3.security.Role;

@Transactional
@Component
public class UserFacadeImpl implements UserFacade {

	@Autowired
	private CoreMapper mapper;

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
		if (loggedUser != null && loggedUser.size() == 1
				&& loggedUser.get(0).getPassword().equals(encoder.encodePassword(password, null))) {

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
	 * @return <code>true</code> pokud se přidání zdařilo, jinak <code>false</code>
	 */
	public void registrateNewUser(String email, String username, String password) {
		User user = new User();
		user.setConfirmed(false);
		user.setEmail(email);
		user.setName(username);
		user.setPassword(encoder.encodePassword(password, null));
		user.setRegistrationDate(Calendar.getInstance().getTime());
		EnumSet<Role> roles = EnumSet.of(Role.USER);
		user.setRoles(roles);
		userRepository.save(user);
	}

	/**
	 * Aktivuje uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak <code>false</code>
	 */
	public void activateUser(Long user) {
		User u = userRepository.findOne(user);
		u.setConfirmed(true);
		userRepository.save(u);
	}

	/**
	 * Zablokuje uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak <code>false</code>
	 */
	public void banUser(Long user) {
		User u = userRepository.findOne(user);
		u.setConfirmed(false);
		userRepository.save(u);
	}

	/**
	 * Upraví role uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak <code>false</code>
	 */
	public void changeUserRoles(Long user, Set<Role> roles) {
		User u = userRepository.findOne(user);
		u.setRoles(roles);
		userRepository.save(u);
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
	public boolean hasInFavourites(Long content, Long user) {
		return userRepository.findByIdAndFavouritesId(user, content) != null;
	}

	/**
	 * Přidá obsah do oblíbených uživatele
	 */
	public void addContentToFavourites(Long contentId, Long user) {
		User userEntity = userRepository.findOne(user);
		userEntity.getFavourites().add(contentNodeRepository.findOne(contentId));
		userRepository.save(userEntity);
	}

	private void removeContentFromFavourites(User user, Long contentId) {
		user.getFavourites().remove(contentNodeRepository.findOne(contentId));
		userRepository.save(user);
	}

	/**
	 * Odebere obsah z oblíbených uživatele
	 */
	public void removeContentFromFavourites(Long contentId, Long user) {
		User userEntity = userRepository.findOne(user);
		removeContentFromFavourites(userEntity, contentId);
	}

	/**
	 * Odebere obsah z oblíbených všech uživatelů
	 */
	public void removeContentFromAllUsersFavourites(Long contentId) {
		// vymaž z oblíbených
		List<User> users = userRepository.findByFavouritesId(contentId);
		if (users != null) {
			for (User user : users)
				removeContentFromFavourites(user, contentId);
		}
	}
}
