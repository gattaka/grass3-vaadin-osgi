package cz.gattserver.grass3.articles.domain;

import java.util.Comparator;

public class ArticleJSResourceComparator implements
		Comparator<ArticleJSResource> {

	@Override
	public int compare(ArticleJSResource o1, ArticleJSResource o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (o2 == null) {
				return 1;
			} else {
				return o1.getExecutionOrder().compareTo(o2.getExecutionOrder());
			}
		}
	}

}
