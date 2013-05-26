package org.myftp.gattserver.grass3.articles.dao;

import org.myftp.gattserver.grass3.articles.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
