package cz.gattserver.grass3.facades.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

	@Override
	public UserInfoDTO getUserByLogin(String username, String password) {
		User user = userRepository.findByName(username);
		if (user != null && user.getPassword().equals(encoder.encode(password))) {
			return mapper.map(user);
		}
		return null;
	}

	@Override
	public Long registrateNewUser(String email, String username, String password) {
		Validate.notNull(email, "'email' nesmí být null");
		Validate.notNull(username, "'username' nesmí být null");
		Validate.notNull(password, "'password' nesmí být null");

		User user = new User();
		user.setConfirmed(false);
		user.setEmail(email);
		user.setName(username);
		user.setPassword(encoder.encode(password));
		user.setRegistrationDate(LocalDateTime.now());
		EnumSet<Role> roles = EnumSet.of(Role.USER);
		user.setRoles(roles);
		user = userRepository.save(user);

		return user.getId();
	}

	@Override
	public void activateUser(Long userId) {
		Validate.notNull(userId, "'userId' nesmí být null");
		userRepository.updateConfirmed(userId, true);
	}

	@Override
	public void banUser(Long userId) {
		Validate.notNull(userId, "'userId' nesmí být null");
		userRepository.updateConfirmed(userId, false);
	}

	@Override
	public void changeUserRoles(Long userId, Set<Role> roles) {
		Validate.notNull(userId, "'userId' nesmí být null");
		Validate.notNull(roles, "'roles' nesmí být null");
		User u = userRepository.findOne(userId);
		u.setRoles(roles);
		userRepository.save(u);
	}

	@Override
	public List<UserInfoDTO> getUserInfoFromAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserInfoDTO> infoDTOs = new ArrayList<UserInfoDTO>();
		for (User user : users) {
			infoDTOs.add(mapper.map(user));
		}
		return infoDTOs;
	}

	@Override
	public UserInfoDTO getUser(Long id) {
		Validate.notNull(id, "'id' uživatele nesmí být null");
		User user = userRepository.findOne(id);
		return user == null ? null : mapper.map(user);
	}

	@Override
	public UserInfoDTO getUser(String username) {
		Validate.notNull(username, "'username' uživatele nesmí být null");
		User user = userRepository.findByName(username);
		return user == null ? null : mapper.map(user);
	}

	@Override
	public boolean hasInFavourites(Long contentId, Long userId) {
		Validate.notNull(contentId, "'contentId' záznamu obsahu nesmí být null");
		Validate.notNull(userId, "'userId' uživatele nesmí být null");
		return userRepository.findByIdAndFavouritesId(userId, contentId) != null;
	}

	@Override
	public void addContentToFavourites(Long contentId, Long userId) {
		Validate.notNull(contentId, "'contentId' záznamu obsahu nesmí být null");
		Validate.notNull(userId, "'userId' uživatele nesmí být null");
		User userEntity = userRepository.findOne(userId);
		userEntity.getFavourites().add(contentNodeRepository.findOne(contentId));
		userRepository.save(userEntity);
	}

	private void removeContentFromFavourites(User user, Long contentId) {
		user.getFavourites().remove(contentNodeRepository.findOne(contentId));
		userRepository.save(user);
	}

	@Override
	public void removeContentFromFavourites(Long contentId, Long userId) {
		Validate.notNull(contentId, "'contentId' záznamu obsahu nesmí být null");
		Validate.notNull(userId, "'userId' uživatele nesmí být null");
		User userEntity = userRepository.findOne(userId);
		removeContentFromFavourites(userEntity, contentId);
	}

	@Override
	public void removeContentFromAllUsersFavourites(Long contentId) {
		Validate.notNull(contentId, "'contentId' záznamu obsahu nesmí být null");
		List<User> users = userRepository.findByFavouritesId(contentId);
		if (users != null) {
			for (User user : users)
				removeContentFromFavourites(user, contentId);
		}
	}
}
