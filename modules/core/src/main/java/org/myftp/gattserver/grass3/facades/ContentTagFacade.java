package org.myftp.gattserver.grass3.facades;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.model.dao.ContentNodeDAO;
import org.myftp.gattserver.grass3.model.dao.ContentTagDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.ContentTag;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.ContentTagDTO;
import org.myftp.gattserver.grass3.util.Mapper;

public enum ContentTagFacade {

	INSTANCE;

	private Mapper mapper = Mapper.INSTANCE;

	public List<ContentTagDTO> getAllContentTags() {
		ContentTagDAO dao = new ContentTagDAO();

		List<ContentTag> contentTags = dao.findAll();
		if (contentTags == null)
			return null;
		List<ContentTagDTO> contentTagDTOs = mapper
				.mapContentTagCollection(contentTags);

		dao.closeSession();
		return contentTagDTOs;
	}

	/**
	 * Bere řetězec tagů, parsuje je a ukládá do nich (nebo vytvoří nové)
	 * referenci na tento obsah - <b>mění {@link ContentNode} entitu v DB</b>
	 * 
	 * @param tagNames
	 *            řetězec tagů oddělených mezerami
	 * @param contentNodeDTO
	 *            obsah, který je oanotován těmito tagy
	 * @return množina tagů, jako objektů, odpovídající těm ze vstupního řetězce
	 */
	public boolean saveTags(String tagNames, ContentNodeDTO contentNodeDTO) {

		// TODO regex ! jinak to bude dělat mezery součástí
		String[] tagArray = tagNames.split(",");
		ContentTagDAO contentTagDAO = new ContentTagDAO();
		Set<ContentTag> tags = new HashSet<ContentTag>();

		ContentNodeDAO contentNodeDAO = new ContentNodeDAO();
		ContentNode contentNode = contentNodeDAO.findByID(contentNodeDTO
				.getId());
		Set<ContentTag> contentTags = contentNode.getContentTags();
		contentNodeDAO.closeSession();

		/**
		 * Fáze 1:
		 * 
		 * Bere postupně stringy tagů a ptá se zda existuje tag, pokud ano,
		 * přidá k němu (nebo to zkusí - je to množina) tento obsah
		 */
		for (String tag : tagArray) {

			if (tag.isEmpty())
				continue;

			// existuje už takový tag ?
			ContentTag contentTag = contentTagDAO.findContentTagByName(tag);
			contentTagDAO.closeSession();

			if (contentTag == null) {
				// ne ? - vytvoř a přidej do něj tento obsah
				contentTag = new ContentTag();
				contentTag.setName(tag);

				Set<ContentNode> contentNodeIds = new HashSet<ContentNode>();
				contentNodeIds.add(contentNode);

				contentTag.setContentNodes(contentNodeIds);
				Long tagId = (Long) contentTagDAO.save(contentTag);
				contentTag.setId(tagId);
				
				// pokud je to null, tak došlo k chybě ...
				if (tagId == null)
					return false;
				
			} else {
				
				// existuje !
				contentTag.getContentNodes().add(contentNode);
				if (contentTagDAO.merge(contentTag) == false)
					return false;
				
			}

			// přidej ho do seznamu
			tags.add(contentTag);

		}

		/**
		 * Fáze 2:
		 * 
		 * Má množinu aktuálně použitých tagů a množinu starých - je potřeba
		 * zjistit, které staré tagy se už nepoužívají a ty odhlásit !
		 */
		Set<ContentTag> tagsToRemove = new HashSet<ContentTag>();
		for (ContentTag contentTag : contentTags) {
			if (tags.contains(contentTag) == false) {
				/**
				 * Tento tag již není použit - odhlaš ho od daného tagu - pokud
				 * jsem byl poslední, kdo byl u tohoto tagu, je vhodné tag úplně
				 * smazat
				 */
				contentTag.getContentNodes().remove(contentNode);
				if (contentTagDAO.merge(contentTag) == false)
					return false;

				if (contentTag.getContentNodes().isEmpty()) {
					/**
					 * Jsem poslední - tag je zbytečné držet v DB, nemůžu ho ale
					 * smazat teď hned, protože na něj mám vazbu já sám a
					 * nejprve se tedy musí uložit moje změny - takže ho zatím
					 * dám do seznamu tagů na smazání
					 */
					tagsToRemove.add(contentTag);
				}
			}
		}

		/**
		 * Přepíše starou množinu tagů
		 */
		contentNode.setContentTags(tags);

		/**
		 * Ulož změny v contentNode
		 */
		if (new ContentNodeDAO().merge(contentNode) == false)
			return false;

		/**
		 * Pokud byly nějaké tagy ke smazání, teď už můžeš
		 */
		for (ContentTag contentTag : tagsToRemove) {
			if (contentTagDAO.delete(contentTag) == false)
				return false;
		}

		return true;
	}

}
