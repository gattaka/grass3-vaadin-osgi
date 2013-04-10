package org.myftp.gattserver.grass3.model.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.domain.ContentTag;
import org.springframework.stereotype.Component;

@Component("contentTagDAO")
public class ContentTagDAO extends AbstractDAO<ContentTag> {

	public ContentTagDAO() {
		super(ContentTag.class);
	}

	/**
	 * Získá tag dle názvu
	 * 
	 * @param name
	 *            jméno tagu
	 * @return objekt tagu
	 */
	public ContentTag findContentTagByName(String name) {
		List<ContentTag> tags = findByRestriction(
				Restrictions.eq("name", name), null, null);
		if (tags == null)
			return null;
		if (tags.isEmpty())
			return null;
		return tags.get(0);
	}

	/**
	 * Upraví tagy dle contentNode
	 * 
	 * @param tags
	 *            množina tagů, které se mají uložit nebo upravit
	 * @param contentNodeId
	 *            identifikátor {@link ContentNode} ke kterému tyto tagy náleží
	 * @return {@code true} pokud se vše povedlo uložit a upravit, jinak
	 *         {@code false}
	 */
	public boolean saveTagsOnContentNode(Set<ContentTag> tags,
			Long contentNodeId) {

		Transaction tx = null;
		openSession();
		try {
			tx = session.beginTransaction();

			ContentNode contentNode = (ContentNode) session.load(
					ContentNode.class, contentNodeId);
			if (contentNode == null) {
				tx.rollback();
				closeSession();
				return false;
			}

			// Fáze #1
			// získej tagy, které se už nepoužívají a na nich proveď odebrání
			// ContentNode a případně smazání
			Set<ContentTag> tagsToDelete = new HashSet<ContentTag>();
			for (ContentTag oldTag : contentNode.getContentTags()) {
				if (tags.contains(oldTag))
					continue;

				if (oldTag.getContentNodes().remove(contentNode) == false) {
					// TODO ... pokud nebyl node v tagu, pak je někde chyba a
					// měl by se aspon vyhodit warning
				}

				// ulož změnu
				oldTag = (ContentTag) session.merge(oldTag);
				if (oldTag == null) {
					tx.rollback();
					closeSession();
					return false;
				}

				// pokud je tag prázdný (nemá nodes) pak se může smazat
				if (oldTag.getContentNodes().isEmpty()) {
					tagsToDelete.add(oldTag);
				}
			}

			// Fáze #2
			// vymaž tagy z node
			// do všech tagů přidej odkaz na node
			// tagy ulož nebo na nich proveď merge
			// zároveň je rovnou přidej do node
			contentNode.getContentTags().clear();
			for (ContentTag tag : tags) {
				if (tag.getContentNodes() == null)
					tag.setContentNodes(new HashSet<ContentNode>());
				tag.getContentNodes().add(contentNode);

				// je nový ? Pak ho ulož a zkontroluj, že dostal id
				if (tag.getId() == null) {
					tag.setId((Long) session.save(tag));

					if (tag.getId() == null) {
						tx.rollback();
						closeSession();
						return false;
					}
				} else {
					tag = (ContentTag) session.merge(tag);
					if (tag == null) {
						tx.rollback();
						closeSession();
						return false;
					}
				}

				// přidej tag k node
				contentNode.getContentTags().add(tag);

			}

			// merge contentNode
			contentNode = (ContentNode) session.merge(contentNode);
			if (contentNode == null) {
				tx.rollback();
				closeSession();
				return false;
			}

			// Fáze #3
			// smaž nepoužívané tagy
			for (ContentTag tagToDelete : tagsToDelete) {
				session.delete(tagToDelete);
			}

			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			closeSession();
			return false;
		}
		return true;
	}
}
