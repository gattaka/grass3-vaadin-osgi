package cz.gattserver.grass3.security;

import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.Authorizable;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.service.SectionService;

public interface CoreACL {

	/**
	 * =======================================================================
	 * Sekce
	 * =======================================================================
	 */

	/**
	 * Může uživatel zobrazit danou sekci ?
	 */
	public boolean canShowSection(SectionService section, UserInfoDTO user);

	/**
	 * Může uživatel upravovat "hlášky"
	 */
	public boolean canModifyQuotes(UserInfoDTO user);

	/**
	 * =======================================================================
	 * Obsahy
	 * =======================================================================
	 */

	/**
	 * Může uživatel zobrazit daný obsah ?
	 */
	// řešeno jako DB dotaz

	/**
	 * Může uživatel vytvářet obsah ?
	 */
	public boolean canCreateContent(UserInfoDTO user);

	/**
	 * Může uživatel upravit daný obsah ?
	 */
	public boolean canModifyContent(Authorizable content, UserInfoDTO user);

	/**
	 * Může uživatel smazat daný obsah ?
	 */
	public boolean canDeleteContent(Authorizable content, UserInfoDTO user);

	/**
	 * =======================================================================
	 * Kategorie
	 * =======================================================================
	 */

	/**
	 * Může uživatel založit kategorii ?
	 */
	public boolean canCreateNode(UserInfoDTO user);

	/**
	 * Může uživatel upravit kategorii ?
	 */
	public boolean canModifyNode(UserInfoDTO user);

	/**
	 * Může uživatel přesunout kategorii ?
	 */
	public boolean canMoveNode(UserInfoDTO user);

	/**
	 * Může uživatel smazat kategorii ?
	 */
	public boolean canDeleteNode(UserInfoDTO user);

	/**
	 * =======================================================================
	 * Různé
	 * =======================================================================
	 */

	/**
	 * Je uživatel přihlášen?
	 */
	public boolean isLoggedIn(UserInfoDTO user);

	/**
	 * Může daný uživatel zobrazit detaily o uživateli X ?
	 */
	public boolean canShowUserDetails(UserInfoDTO anotherUser, UserInfoDTO user);

	/**
	 * Může se uživatel zaregistrovat ?
	 */
	public boolean canRegistrate(UserInfoDTO user);

	/**
	 * Může zobrazit stránku s nastavením ?
	 */
	public boolean canShowSettings(UserInfoDTO user);

	/**
	 * Může zobrazit stránku s nastavením aplikace ?
	 */
	public boolean canShowApplicationSettings(UserInfoDTO user);

	/**
	 * Může zobrazit stránku s nastavením kategorií ?
	 */
	public boolean canShowCategoriesSettings(UserInfoDTO user);

	/**
	 * Může zobrazit stránku s nastavením uživatelů ?
	 */
	public boolean canShowUserSettings(UserInfoDTO user);

	/**
	 * Může přidat obsah do svých oblíbených ?
	 */
	public boolean canAddContentToFavourites(ContentNodeDTO contentNodeDTO, UserInfoDTO user);

	/**
	 * Může odebrat obsah ze svých oblíbených ?
	 */
	public boolean canRemoveContentFromFavourites(ContentNodeDTO contentNode, UserInfoDTO user);

}
