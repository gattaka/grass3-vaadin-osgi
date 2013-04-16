package org.myftp.gattserver.grass3.security;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.myftp.gattserver.grass3.config.CoreConfiguration;
import org.myftp.gattserver.grass3.facades.IUserFacade;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.springframework.stereotype.Component;

/**
 * Access control list, bere uživatele a operaci a vyhodnocuje, zda povolit nebo
 * zablokovat
 * 
 * @author gatt
 * 
 */
@Component("coreACL")
public final class CoreACL implements ICoreACL {

	@Resource(name = "userFacade")
	IUserFacade userFacade;

	/**
	 * =======================================================================
	 * Sekce
	 * =======================================================================
	 */

	/**
	 * Může uživatel zobrazit danou sekci ?
	 */
	public boolean canShowSection(ISectionService section, UserInfoDTO user) {

		// záleží na viditelnosti definované sekcí
		return section.isVisibleForRoles(user == null ? null : user.getRoles());
	}

	/**
	 * Může uživatel upravovat "hlášky"
	 */
	public boolean canModifyQuotes(UserInfoDTO user) {

		// pokud je uživatel přihlášen a je to administrátor
		return user != null && user.getRoles().contains(Role.ADMIN);
	}

	/**
	 * =======================================================================
	 * Obsahy
	 * =======================================================================
	 */

	/**
	 * Může uživatel zobrazit daný obsah ?
	 */
	public boolean canShowContent(ContentNodeDTO content, UserInfoDTO user) {

		if (user == null) {

			// pokud je obsah publikován, můžeš zobrazit
			if (content.isPublicated())
				return true;

		} else {

			// pokud je admin, může zobrazit kterýkoliv obsah
			if (user.getRoles().contains(Role.ADMIN))
				return true;

			// pokud jsi autor, můžeš zobrazit svůj obsah
			if (content.getAuthor().getId().equals(user.getId()))
				return true;
		}

		// jinak false
		return false;
	}

	/**
	 * Může uživatel vytvářet obsah ?
	 */
	public boolean canCreateContent(UserInfoDTO user) {

		if (user == null) {

			// host nemůže zakládat obsah
			return false;

		} else {

			// pokud má uživatel oprávnění AUTHOR, pak může
			if (user.getRoles().contains(Role.AUTHOR))
				return true;

		}

		return false;

	}

	/**
	 * Může uživatel upravit daný obsah ?
	 */
	public boolean canModifyContent(ContentNodeDTO content, UserInfoDTO user) {

		if (user == null) {

			// host nemůže upravovat obsah
			return false;

		} else {

			// pokud je admin, může upravit kterýkoliv obsah
			if (user.getRoles().contains(Role.ADMIN))
				return true;

			// pokud jsi autor, můžeš upravit svůj obsah
			if (content.getAuthor().getId().equals(user.getId()))
				return true;
		}

		// jinak false
		return false;
	}

	/**
	 * Může uživatel smazat daný obsah ?
	 */
	public boolean canDeleteContent(ContentNodeDTO content, UserInfoDTO user) {
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
	public boolean canCreateCategory(UserInfoDTO user) {

		if (user == null) {

			// host nemůže
			return false;

		} else {

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
	public boolean canModifyCategory(UserInfoDTO user) {
		return canCreateCategory(user);
	}

	/**
	 * Může uživatel přesunout kategorii ?
	 */
	public boolean canMoveCategory(UserInfoDTO user) {
		return canModifyCategory(user);
	}

	/**
	 * Může uživatel smazat kategorii ?
	 */
	public boolean canDeleteCategory(UserInfoDTO user) {
		return canModifyCategory(user);
	}

	/**
	 * =======================================================================
	 * Různé
	 * =======================================================================
	 */

	/**
	 * Může se uživatel přihlásit ?
	 */
	public boolean canLogin(UserInfoDTO user) {
		return user == null;
	}

	/**
	 * Může se uživatel odhlásit ?
	 */
	public boolean canLogout(UserInfoDTO user) {
		return user != null;
	}

	/**
	 * Může daný uživatel zobrazit detaily o uživateli X ?
	 */
	public boolean canShowUserDetails(UserInfoDTO anotherUser, UserInfoDTO user) {

		// TODO tohle je spíš otázka kódu, než oprávnění
		// nelze zobrazit detail od žádného uživatele
		if (anotherUser == null)
			return false;

		// host nemůže zobrazovat nic
		if (user == null)
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

		if (user == null) {

			// jenom host se může registrovat
			try {
				CoreConfiguration configuration = new ConfigurationUtils<CoreConfiguration>(
						new CoreConfiguration(), CoreConfiguration.CONFIG_PATH)
						.loadExistingOrCreateNewConfiguration();

				return configuration.isRegistrations();

			} catch (JAXBException e) {
				e.printStackTrace();
				return false;
			}

		}
		// jinak false
		return false;
	}

	/**
	 * Může zobrazit stránku s nastavením ?
	 */
	public boolean canShowSettings(UserInfoDTO user) {
		// uživatel musí být přihlášen aby mohl aspoň nějaké nastavení zobrazit
		return user != null;
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
	public boolean canAddContentToFavourites(ContentNodeDTO contentNodeDTO,
			UserInfoDTO user) {
		// uživatel musí být přihlášen a obsah nesmí být v jeho oblíbených
		return user != null
				&& userFacade.hasInFavourites(contentNodeDTO, user) == false;
	}

	/**
	 * Může odebrat obsah ze svých oblíbených ?
	 */
	public boolean canRemoveContentFromFavourites(
			ContentNodeDTO contentNodeDTO, UserInfoDTO user) {
		return user != null
				&& userFacade.hasInFavourites(contentNodeDTO, user) == true;
	}

}
