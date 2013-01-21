package org.myftp.gattserver.grass3.articles.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.myftp.gattserver.grass3.articles.domain.Article;
import org.myftp.gattserver.grass3.articles.domain.TempArticle;
import org.myftp.gattserver.grass3.model.service.IEntityService;

/**
 * Sdružuje třídy entit a hromadně je jako služba registruje u model bundle
 * 
 * @author gatt
 * 
 */
public class ArticlesEntityService implements IEntityService {

	/**
	 * Mělo by být immutable
	 */
	List<Class<?>> domainClasses = new ArrayList<Class<?>>();

	public ArticlesEntityService() {
		domainClasses.add(Article.class);
		domainClasses.add(TempArticle.class);

		// nakonec zamkni
		domainClasses = Collections.unmodifiableList(domainClasses);
	}

	public List<Class<?>> getDomainClasses() {
		return domainClasses;
	}

}
