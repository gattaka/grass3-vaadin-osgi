package cz.gattserver.grass3.articles.model.domain;

import java.util.Comparator;

public class ArticleJSCodeComparator implements Comparator<ArticleJSCode> {

	@Override
	public int compare(ArticleJSCode o1, ArticleJSCode o2) {
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
