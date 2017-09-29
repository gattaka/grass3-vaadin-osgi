package cz.gattserver.grass3.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.config.CoreConfiguration;
import cz.gattserver.grass3.config.ConfigurationService;
import cz.gattserver.grass3.facades.UserFacade;
import cz.gattserver.grass3.model.dto.ContentNodeDTO;
import cz.gattserver.grass3.model.dto.Authorizable;
import cz.gattserver.grass3.model.dto.UserInfoDTO;
import cz.gattserver.grass3.service.SectionService;

/**
 * Access control list, bere uživatele a operaci a vyhodnocuje, zda povolit nebo
 * zablokovat
 * 
 * @author gatt
 * 
 */
@Component
public final class CoreACLImpl implements CoreACL {

	@Autowired
	UserFacade userFacade;

	@Autowired
	ConfigurationService configurationService;

	/**
	 * =======================================================================
	 * Sekce
	 * =======================================================================
	 */

	/**
	 * Může uživatel zobrazit danou sekci ?
	 */
	public boolean canShowSection(SectionService section, UserInfoDTO user) {
		return section.isVisibleForRoles(user.getRoles());
	}

	/**
	 * Může uživatel upravovat "hlášky"
	 */
	public boolean canModifyQuotes(UserInfoDTO user) {
		return isLoggedIn(user) && user.getRoles().contains(Role.ADMIN);
	}

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
	public boolean canCreateContent(UserInfoDTO user) {
		if (isLoggedIn(user)) {
			// pokud má uživatel oprávnění AUTHOR, pak může
			if (user.getRoles().contains(Role.AUTHOR))
				return true;
		}
		return false;
	}

	/**
	 * Může uživatel upravit daný obsah ?
	 */
	public boolean canModifyContent(Authorizable content, UserInfoDTO user) {
		if (isLoggedIn(user)) {
			// pokud je admin, může upravit kterýkoliv obsah
			if (user.getRoles().contains(Role.ADMIN))
				return true;

			// pokud jsi autor, můžeš upravit svůj obsah
			if (content.getAuthor().getId().equals(user.getId()))
				return true;
		}
		return false;
	}

	/**
	 * Může uživatel smazat daný obsah ?
	 */
	public boolean canDeleteContent(Authorizable content, UserInfoDTO user) {
		return canModifyContent(content, user);
	}

	/**
	 * =======================================================================
	 * Kategorie
	 * =======================================================================
	 */

	/**
	 * Může uživatel založit kategorii ?
	 */
	public boolean canCreateNode(UserInfoDTO user) {
		if (isLoggedIn(user)) {
			// pokud je admin, můžeš
			if (user.getRoles().contains(Role.ADMIN))
				return true;
		}
		// jinak false
		return false;
	}

	/**
	 * Může uživatel upravit kategorii ?
	 */
	public boolean canModifyNode(UserInfoDTO user) {
		return canCreateNode(user);
	}

	/**
	 * Může uživatel přesunout kategorii ?
	 */
	public boolean canMoveNode(UserInfoDTO user) {
		return canModifyNode(user);
	}

	/**
	 * Může uživatel smazat kategorii ?
	 */
	public boolean canDeleteNode(UserInfoDTO user) {
		return canModifyNode(user);
	}

	/**
	 * =======================================================================
	 * Různé
	 * =======================================================================
	 */

	/**
	 * Je uživatel přihlášen?
	 */
	@Override
	public boolean isLoggedIn(UserInfoDTO user) {
		return user.getId() != null;
	}

	/**
	 * Může daný uživatel zobrazit detaily o uživateli X ?
	 */
	public boolean canShowUserDetails(UserInfoDTO anotherUser, UserInfoDTO user) {
		// nelze zobrazit detail od žádného uživatele
		if (user.getId() == null || anotherUser == null)
			return false;

		// uživatel může vidět detaily o sobě
		if (user.getId().equals(anotherUser.getId()))
			return true;

		// administrator může vidět detaily od všech uživatelů
		if (user.getRoles().contains(Role.ADMIN))
			return true;

		return false;
	}

	/**
	 * Může se uživatel zaregistrovat ?
	 */
	public boolean canRegistrate(UserInfoDTO user) {
		if (!isLoggedIn(user)) {
			// jenom host se může registrovat
			CoreConfiguration configuration = new CoreConfiguration();
			configurationService.loadConfiguration(configuration);
			return configuration.isRegistrations();
		}
		// jinak false
		return false;
	}

	/**
	 * Může zobrazit stránku s nastavením ?
	 */
	public boolean canShowSettings(UserInfoDTO user) {
		return isLoggedIn(user);
	}

	/**
	 * Může zobrazit stránku s nastavením aplikace ?
	 */
	public boolean canShowApplicationSettings(UserInfoDTO user) {
		return user.getRoles().contains(Role.ADMIN);
	}

	/**
	 * Může zobrazit stránku s nastavením kategorií ?
	 */
	public boolean canShowCategoriesSettings(UserInfoDTO user) {
		return user.getRoles().contains(Role.ADMIN);
	}

	/**
	 * Může zobrazit stránku s nastavením uživatelů ?
	 */
	public boolean canShowUserSettings(UserInfoDTO user) {
		return user.getRoles().contains(Role.ADMIN);
	}

	/**
	 * Může přidat obsah do svých oblíbených ?
	 */
	public boolean canAddContentToFavourites(ContentNodeDTO contentNodeDTO, UserInfoDTO user) {
		return isLoggedIn(user) && userFacade.hasInFavourites(contentNodeDTO.getId(), user.getId()) == false;
	}

	/**
	 * Může odebrat obsah ze svých oblíbených ?
	 */
	public boolean canRemoveContentFromFavourites(ContentNodeDTO contentNodeDTO, UserInfoDTO user) {
		return isLoggedIn(user) && userFacade.hasInFavourites(contentNodeDTO.getId(), user.getId()) == true;
	}

}
