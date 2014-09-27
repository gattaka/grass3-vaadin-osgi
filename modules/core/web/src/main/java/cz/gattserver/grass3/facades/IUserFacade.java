package cz.gattserver.grass3.facades;

import java.util.List;

import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.UserInfoDTO;

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
public interface IUserFacade {

	/**
	 * Zkusí najít uživatele dle jména a hesla
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public UserInfoDTO getUserByLogin(String username, String password);

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
			String password);

	/**
	 * Aktivuje uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean activateUser(UserInfoDTO userDTO);

	/**
	 * Zablokuje uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean banUser(UserInfoDTO userDTO);

	/**
	 * Upraví role uživatele
	 * 
	 * @param user
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public boolean changeUserRoles(UserInfoDTO userDTO);

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
	 * @return
	 */
	public UserInfoDTO getUser(String username);

	/**
	 * Zjistí zda daný obsah je v oblíbených daného uživatele
	 */
	public boolean hasInFavourites(ContentNodeDTO contentNodeDTO,
			UserInfoDTO user);

	/**
	 * Přidá obsah do oblíbených uživatele
	 */
	public boolean addContentToFavourites(ContentNodeDTO contentNodeDTO,
			UserInfoDTO user);

	/**
	 * Odebere obsah z oblíbených uživatele
	 */
	public boolean removeContentFromFavourites(ContentNodeDTO contentNode,
			UserInfoDTO user);

	/**
	 * Odebere obsah z oblíbených všech uživatelů
	 */
	public boolean removeContentFromAllUsersFavourites(
			ContentNodeDTO contentNode);

}
