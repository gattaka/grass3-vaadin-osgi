package cz.gattserver.grass3.security;

import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.IAuthorizable;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.service.ISectionService;

public interface ICoreACL {

	/**
	 * =======================================================================
	 * Sekce
	 * =======================================================================
	 */

	/**
	 * Může uživatel zobrazit danou sekci ?
	 */
	public boolean canShowSection(ISectionService section, UserInfoDTO user);

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
	public boolean canShowContent(IAuthorizable content, UserInfoDTO user);

	/**
	 * Může uživatel vytvářet obsah ?
	 */
	public boolean canCreateContent(UserInfoDTO user);

	/**
	 * Může uživatel upravit daný obsah ?
	 */
	public boolean canModifyContent(IAuthorizable content, UserInfoDTO user);

	/**
	 * Může uživatel smazat daný obsah ?
	 */
	public boolean canDeleteContent(IAuthorizable content, UserInfoDTO user);

	/**
	 * =======================================================================
	 * Kategorie
	 * =======================================================================
	 */

	/**
	 * Může uživatel založit kategorii ?
	 */
	public boolean canCreateCategory(UserInfoDTO user);

	/**
	 * Může uživatel upravit kategorii ?
	 */
	public boolean canModifyCategory(UserInfoDTO user);

	/**
	 * Může uživatel přesunout kategorii ?
	 */
	public boolean canMoveCategory(UserInfoDTO user);

	/**
	 * Může uživatel smazat kategorii ?
	 */
	public boolean canDeleteCategory(UserInfoDTO user);

	/**
	 * =======================================================================
	 * Různé
	 * =======================================================================
	 */

	/**
	 * Může se uživatel přihlásit ?
	 */
	public boolean canLogin(UserInfoDTO user);

	/**
	 * Může se uživatel odhlásit ?
	 */
	public boolean canLogout(UserInfoDTO user);

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
