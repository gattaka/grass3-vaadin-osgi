package org.myftp.gattserver.grass3.facades;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

import org.myftp.gattserver.grass3.model.dao.UserDAO;
import org.myftp.gattserver.grass3.model.domain.User;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.security.Role;
import org.myftp.gattserver.grass3.security.SecurityFacade;
import org.myftp.gattserver.grass3.util.Mapper;

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
public enum UserFacade {

	INSTANCE;

	private Mapper mapper = Mapper.INSTANCE;

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
		UserDAO userDAO = new UserDAO();

		User user = new User();
		user.setConfirmed(false);
		user.setEmail(email);
		user.setName(username);
		user.setPassword(SecurityFacade.getInstance()
				.makeHashFromPasswordString(password));
		user.setRegistrationDate(Calendar.getInstance().getTime());
		EnumSet<Role> roles = EnumSet.of(Role.USER);
		user.setRoles(roles);

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
		UserDAO dao = new UserDAO();
		User user = dao.findByID(userDTO.getId());
		user.setConfirmed(true);
		return dao.merge(user);
	}

	/**
	 * Zablokuje uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean banUser(UserInfoDTO userDTO) {
		UserDAO dao = new UserDAO();
		User user = dao.findByID(userDTO.getId());
		user.setConfirmed(false);
		return dao.merge(user);
	}

	/**
	 * Upraví role uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean changeUserRoles(UserInfoDTO userDTO) {
		UserDAO dao = new UserDAO();
		User user = dao.findByID(userDTO.getId());
		user.setRoles(userDTO.getRoles());
		return dao.merge(user);
	}

	/**
	 * Vrátí všechny uživatele
	 * 
	 * @return list uživatelů
	 */
	public List<UserInfoDTO> getUserInfoFromAllUsers() {
		UserDAO dao = new UserDAO();
		List<User> users = dao.findAll();
		List<UserInfoDTO> infoDTOs = new ArrayList<UserInfoDTO>();
		for (User user : users) {
			infoDTOs.add(mapper.map(user));
		}
		return infoDTOs;
	}
}
