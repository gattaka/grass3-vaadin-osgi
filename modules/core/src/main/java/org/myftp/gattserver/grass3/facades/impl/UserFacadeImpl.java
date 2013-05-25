package org.myftp.gattserver.grass3.facades.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dao.UserDAO;
import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.util.Mapper;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 
 * Fasáda na které je funkcionalita pro view aplikace. Má několik funkcí:
 * 
 * <ol>
 * <li>přebírá funkcionalitu kolem přípravy dat k zobrazení ve view ze samotných
 * view tříd a ty se tak starají pouze o využití těchto dat, nikoliv jejich
 * předzpracování</li>
 * <li>odděluje view od vazby na DAO třídy a tím model vrstvu</li>
 * <li>připravuje data do DTO tříd, takže nedochází k "propadnutí" proxy objektů
 * (například) od hibernate, čímž je opět lépe oddělena view vrstva od vrstvy
 * modelu</li>
 * </ol>
 * 
 * <p>
 * Fasády komunikují s view pomocí parametrů metod a DTO objektů, při jejich
 * vydávání je zřejmé, že data v DTO odpovídají DB entitě. Při jejich přijímání
 * (DTO je předáno fasádě) není ovšem dáno, že fasády využije vše z DTO. To se
 * řídí okolnostmi. Mezi DTO a entitou není přesný vztah 1:1.
 * </p>
 * 
 * @author gatt
 * 
 */
@Component("userFacade")
public class UserFacadeImpl implements IUserFacade {

	@Resource(name = "mapper")
	private Mapper mapper;

	@Resource(name = "grassPasswordEncoder")
	private PasswordEncoder encoder;

	@Resource(name = "userDAO")
	private UserDAO userDAO;

	/**
	 * Zkusí najít uživatele dle jména a hesla
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public UserInfoDTO getUserByLogin(String username, String password) {
		List<User> loggedUser = userDAO.findByName(username);
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

		// u save se session uzavírá sama

		if (userDAO.save(user) == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Aktivuje uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean activateUser(UserInfoDTO userDTO) {
		User user = userDAO.findByID(userDTO.getId());
		user.setConfirmed(true);
		// session uzavře merge
		return userDAO.merge(user);
	}

	/**
	 * Zablokuje uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean banUser(UserInfoDTO userDTO) {
		User user = userDAO.findByID(userDTO.getId());
		user.setConfirmed(false);
		// session uzavře merge
		return userDAO.merge(user);
	}

	/**
	 * Upraví role uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean changeUserRoles(UserInfoDTO userDTO) {
		User user = userDAO.findByID(userDTO.getId());
		user.setRoles(userDTO.getRoles());
		// session uzavře merge
		return userDAO.merge(user);
	}

	/**
	 * Vrátí všechny uživatele
	 * 
	 * @return list uživatelů
	 */
	public List<UserInfoDTO> getUserInfoFromAllUsers() {
		List<User> users = userDAO.findAll();
		List<UserInfoDTO> infoDTOs = new ArrayList<UserInfoDTO>();
		for (User user : users) {
			infoDTOs.add(mapper.map(user));
		}

		userDAO.closeSession();
		return infoDTOs;
	}

	/**
	 * Vrátí uživatele dle jména
	 * 
	 * @param username
	 * @return
	 */
	public UserInfoDTO getUser(String username) {
		List<User> loggedUser = userDAO.findByName(username);
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
		return userDAO.hasContentInFavourites(contentNodeDTO.getId(),
				user.getId());
	}

	/**
	 * Přidá obsah do oblíbených uživatele
	 */
	public boolean addContentToFavourites(ContentNodeDTO contentNodeDTO,
			UserInfoDTO user) {
		return userDAO.addContentToFavourites(contentNodeDTO.getId(),
				user.getId());
	}

	/**
	 * Odebere obsah z oblíbených uživatele
	 */
	public boolean removeContentFromFavourites(ContentNodeDTO contentNodeDTO,
			UserInfoDTO user) {
		return userDAO.removeContentFromFavourites(contentNodeDTO.getId(),
				user.getId());
	}
}
