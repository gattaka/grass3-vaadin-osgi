package cz.gattserver.grass3.facades;

import java.util.List;
import java.util.Set;

import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.security.Role;

/**
 * @author Hynek
 */
public interface UserFacade {

	/**
	 * Zkusí najít uživatele dle jména a hesla
	 * 
	 * @param username
	 *            jméno uživatele (login)
	 * @param password
	 *            heslo uživatele
	 * @return uživatel nebo <code>null</code>
	 */
	public UserInfoDTO getUserByLogin(String username, String password);

	/**
	 * Zaregistruje nového uživatele
	 * 
	 * @param email
	 *            email uživatele
	 * @param username
	 *            jméno uživatele (login)
	 * @param password
	 *            heslo uživatele
	 * @return db id, které bylo nového uživateli přiděleno
	 */
	public Long registrateNewUser(String email, String username, String password);

	/**
	 * Aktivuje uživatele
	 * 
	 * @param userId
	 *            id uživatele
	 */
	public void activateUser(Long userId);

	/**
	 * Zablokuje uživatele
	 * 
	 * @param userId
	 *            id uživatele
	 */
	public void banUser(Long userId);

	/**
	 * Upraví role uživatele
	 * 
	 * @param userId
	 *            id uživatele
	 * @param roles
	 *            role, které mu budou nastaveny (nejedná se o přidání ale
	 *            pevnou změnu výčtu rolí)
	 */
	public void changeUserRoles(Long userId, Set<Role> roles);

	/**
	 * Vrátí všechny uživatele
	 * 
	 * @return list uživatelů
	 */
	public List<UserInfoDTO> getUserInfoFromAllUsers();

	/**
	 * Vrátí uživatele dle jména
	 * 
	 * @param username
	 *            jméno uživatele
	 * @return nalezný uživatel
	 */
	public UserInfoDTO getUser(String username);

	/**
	 * Vrátí uživatele dle id
	 * 
	 * @param userId
	 *            id hledaného uživatele
	 * @return nalezený uživatel
	 */
	public UserInfoDTO getUser(Long userId);

	/**
	 * Zjistí zda daný obsah je v oblíbených daného uživatele
	 * 
	 * @param contentNodeId
	 *            id obsahu, který bude hledán v oblíbených
	 * @param userId
	 *            id uživatele, kterému bude prohledán seznam oblíbených
	 * @return <code>true</code>, pokud má daný uživatel v oblíbených daný obsah
	 */
	public boolean hasInFavourites(Long contentNodeId, Long userId);

	/**
	 * Přidá obsah do oblíbených uživatele
	 * 
	 * @param contentNodeId
	 *            id obsahu, který bude přidán do oblíbených
	 * @param userId
	 *            id uživatele, kterému bude obsah přidán do oblíbených
	 */
	public void addContentToFavourites(Long contentNodeId, Long userId);

	/**
	 * Odebere obsah z oblíbených uživatele
	 * 
	 * @param contentNodeId
	 *            id obsahu, který bude odebrán z oblíbených
	 * @param userId
	 *            id uživatele, kterému bude obsah odebrán z oblíbených
	 */
	public void removeContentFromFavourites(Long contentNodeId, Long userId);

	/**
	 * Odebere obsah z oblíbených všech uživatelů
	 * 
	 * @param contentNodeId
	 *            id obsahu, který bude odebrán z oblíbených
	 */
	public void removeContentFromAllUsersFavourites(Long content);

}
