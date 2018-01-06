package cz.gattserver.grass3.articles.services;

import java.util.List;

import cz.gattserver.grass3.articles.interfaces.ArticleTO;
import cz.gattserver.grass3.articles.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass3.articles.interfaces.ArticlePayloadTO;

public interface ArticleService {

	/**
	 * Uloží nový článek
	 * 
	 * @param payload
	 *            obsahové informace článku
	 * @param nodeId
	 *            id kategorie, do které je článek ukládán
	 * @param authorId
	 *            id uživatele, který článek vytvořil
	 * @return id uloženého článku, pokud se operace zdařila
	 */
	public long saveArticle(ArticlePayloadTO payload, long nodeId, long authorId);

	/**
	 * Upraví článek
	 * 
	 * @param articleId
	 *            id upravovaného článku
	 * @param payload
	 *            obsahové informace článku
	 * @param partNumber
	 *            číslo části článku (číslováno od 0), je-li upravována pouze
	 *            jeho část, může být <code>null</code>, pokud je upravován celý
	 *            článek
	 */
	public void modifyArticle(long articleId, ArticlePayloadTO payload, Integer partNumber);

	/**
	 * Uloží koncept článku z vytváření nového článku
	 * 
	 * @param payload
	 *            obsahové informace článku
	 * @param nodeId
	 *            id kategorie, do které je článek ukládán
	 * @param authorId
	 *            id uživatele, který článek vytvořil
	 * @param asPreview
	 *            <code>true</code>, pokud je koncept vytvářen za účelem náhledu
	 * @return id uloženého konceptu, pokud se operace zdařila
	 */
	public long saveDraft(ArticlePayloadTO payload, long nodeId, long authorId, boolean asPreview);

	/**
	 * Uloží koncept článku z upravovaného článku
	 * 
	 * @param payload
	 *            obsahové informace článku
	 * @param nodeId
	 *            id kategorie, do které je článek ukládán
	 * @param authorId
	 *            id uživatele, který článek vytvořil
	 * @param partNumber
	 *            číslo části článku (číslováno od 0), je-li upravována pouze
	 *            jeho část, může být <code>null</code>, pokud je upravován celý
	 *            článek
	 * @param originArticleId
	 *            id článku, k jehož úpravě je ukládán tento koncept
	 * @param asPreview
	 *            <code>true</code>, pokud je koncept vytvářen za účelem náhledu
	 * @return id uloženého konceptu, pokud se operace zdařila
	 */
	public long saveDraftOfExistingArticle(ArticlePayloadTO payload, long nodeId, long authorId, Integer partNumber,
			long originArticleId, boolean asPreview);

	/**
	 * Upraví koncept článku z vytváření nového článku
	 * 
	 * @param drafId
	 *            id upravovaného konceptu
	 * @param payload
	 *            obsahové informace článku
	 * @param asPreview
	 *            <code>true</code>, pokud je koncept upravován za účelem
	 *            náhledu
	 */
	public void modifyDraft(long drafId, ArticlePayloadTO payload, boolean asPreview);

	/**
	 * Upraví koncept článku z upravovaného článku
	 * 
	 * @param drafId
	 *            id upravovaného konceptu
	 * @param payload
	 *            obsahové informace článku
	 * @param partNumber
	 *            číslo části článku (číslováno od 0), je-li upravována pouze
	 *            jeho část, může být <code>null</code>, pokud je upravován celý
	 *            článek
	 * @param originArticleId
	 *            id článku, k jehož úpravě je ukládán tento koncept
	 * @param asPreview
	 *            <code>true</code>, pokud je koncept upravován za účelem
	 *            náhledu
	 */
	public void modifyDraftOfExistingArticle(long drafId, ArticlePayloadTO payload, Integer partNumber,
			long originArticleId, boolean asPreview);

	/**
	 * Smaže článek
	 * 
	 * @param article
	 *            článek ke smazání
	 */
	public void deleteArticle(long id);

	/**
	 * Získá článek dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return DTO článku
	 */
	public ArticleTO getArticleForDetail(long id);

	/**
	 * Spustí přegenerování všech článků
	 * 
	 * @param contextRoot
	 *            kořenová adresa, od které mají být vytvoření linky na CSS a JS
	 *            zdroje, jež může článek na sobě mít
	 */
	public void reprocessAllArticles(String contextRoot);

	/**
	 * Získá všechny články a namapuje je pro použití při vyhledávání
	 * 
	 * @param userId
	 *            id uživatele, kterým je omezena viditelnost na rozpracované
	 *            články
	 * @return list článků, ve kterých je možné vyhledávat
	 */
	public List<ArticleTO> getAllArticlesForSearch(long userId);

	/**
	 * Získá všechny rozpracované články viditelné daným uživatelem
	 * 
	 * @param userId
	 *            id uživatele, kterým je omezena viditelnost na rozpracované
	 *            články
	 * @return list konceptů
	 */
	public List<ArticleDraftOverviewTO> getDraftsForUser(long userId);

}
