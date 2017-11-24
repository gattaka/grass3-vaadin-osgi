package cz.gattserver.grass3.articles.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.articles.model.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

	@Query(value = "select a from ARTICLE a join a.contentNode c where c.draft = true and (?2 = true or c.author.id = ?1) order by c.creationDate desc")
	List<Article> findDraftsForUser(Long userId, boolean admin);

}
