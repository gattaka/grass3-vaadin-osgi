package cz.gattserver.grass3.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.model.repositories.ContentNodeRepository;
import cz.gattserver.grass3.model.repositories.UserRepository;
import cz.gattserver.grass3.model.util.CoreMapper;
import cz.gattserver.grass3.security.Role;
import cz.gattserver.grass3.services.UserService;

@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private CoreMapper mapper;

	@Resource(name = "grassPasswordEncoder")
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Override
	public long registrateNewUser(String email, String username, String password) {
		Validate.notBlank(email, "'email' nesmí být prázdný");
		Validate.notBlank(username, "'username' nesmí být prázdný");
		Validate.notBlank(password, "'password' nesmí být prázdný");

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
	public void activateUser(long userId) {
		userRepository.updateConfirmed(userId, true);
	}

	@Override
	public void banUser(long userId) {
		userRepository.updateConfirmed(userId, false);
	}

	@Override
	public void changeUserRoles(long userId, Set<Role> roles) {
		Validate.notNull(roles, "'roles' nesmí být prázdný");
		User u = userRepository.findOne(userId);
		u.setRoles(roles);
		userRepository.save(u);
	}

	@Override
	public List<UserInfoTO> getUserInfoFromAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserInfoTO> infoDTOs = new ArrayList<UserInfoTO>();
		for (User user : users) {
			infoDTOs.add(mapper.map(user));
		}
		return infoDTOs;
	}

	@Override
	public UserInfoTO getUserById(long userId) {
		User user = userRepository.findOne(userId);
		return mapper.map(user);
	}

	@Override
	public UserInfoTO getUser(String username) {
		Validate.notBlank(username, "'username' uživatele nesmí být null");
		User user = userRepository.findByName(username);
		return mapper.map(user);
	}

	@Override
	public boolean hasInFavourites(long contentNodeId, long userId) {
		return userRepository.findByIdAndFavouritesId(userId, contentNodeId) != null;
	}

	@Override
	public void addContentToFavourites(long contentNodeId, long userId) {
		User userEntity = userRepository.findOne(userId);
		userEntity.getFavourites().add(contentNodeRepository.findOne(contentNodeId));
		userRepository.save(userEntity);
	}

	private void removeContentFromFavourites(User user, Long contentNodeId) {
		user.getFavourites().remove(contentNodeRepository.findOne(contentNodeId));
		userRepository.save(user);
	}

	@Override
	public void removeContentFromFavourites(long contentNodeId, long userId) {
		User userEntity = userRepository.findOne(userId);
		removeContentFromFavourites(userEntity, contentNodeId);
	}

	@Override
	public void removeContentFromAllUsersFavourites(long contentNodeId) {
		List<User> users = userRepository.findByFavouritesId(contentNodeId);
		for (User user : users)
			removeContentFromFavourites(user, contentNodeId);
	}
}
