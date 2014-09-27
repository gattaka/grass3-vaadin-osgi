package cz.gattserver.grass3.articles.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.articles.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
