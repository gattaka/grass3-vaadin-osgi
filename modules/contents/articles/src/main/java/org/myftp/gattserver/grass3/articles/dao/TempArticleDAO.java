package org.myftp.gattserver.grass3.articles.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.myftp.gattserver.grass3.articles.domain.TempArticle;
import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.springframework.stereotype.Component;

@Component("tempArticleDAO")
public class TempArticleDAO extends AbstractDAO<TempArticle> {

	public TempArticleDAO() {
		super(TempArticle.class);
	}

	public List<TempArticle> getByUserId(Long userId) {
		return findByRestriction(Restrictions.eq("user.id", userId), null, null);
	}

}
