package org.myftp.gattserver.grass3.pg.facade;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.facades.IContentNodeFacade;
import org.myftp.gattserver.grass3.model.dao.ContentNodeRepository;
import org.myftp.gattserver.grass3.model.domain.ContentNode;
import org.myftp.gattserver.grass3.model.dto.ContentNodeDTO;
import org.myftp.gattserver.grass3.model.dto.NodeDTO;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.pg.dao.PhotoGalleryRepository;
import org.myftp.gattserver.grass3.pg.domain.Photogallery;
import org.myftp.gattserver.grass3.pg.dto.PhotogalleryDTO;
import org.myftp.gattserver.grass3.pg.util.PhotogalleryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("photogalleryFacade")
public class PhotogalleryFacadeImpl implements IPhotogalleryFacade {

	@Resource(name = "contentNodeFacade")
	private IContentNodeFacade contentNodeFacade;

	@Resource(name = "photogalleryMapper")
	private PhotogalleryMapper photogalleriesMapper;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Autowired
	private PhotoGalleryRepository photogalleryRepository;

	/**
	 * Upraví článek
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param publicated
	 *            je článek publikován ?
	 * @param articleDTO
	 *            původní článek
	 * @return {@code true} pokud se úprava zdařila, jinak {@code false}
	 */
	public boolean modifyArticle(String name, String text,
			Collection<String> tags, boolean publicated, ArticleDTO articleDTO,
			String contextRoot) {

		// článek
		Article article = photogalleryRepository.findOne(articleDTO.getId());

		// nasetuj do něj vše potřebné
		IContext context = processArticle(text, contextRoot);
		article.setOutputHTML(context.getOutput());
		article.setPluginCSSResources(context.getCSSResources());
		article.setPluginJSResources(context.getJSResources());
		article.setText(text);
		article.setSearchableOutput(HTMLTrimmer.trim(context.getOutput()));

		// ulož ho
		if (photogalleryRepository.save(article) == null)
			return false;

		// content node
		if (contentNodeFacade.modify(articleDTO.getContentNode(), name, tags,
				publicated) == false)
			return false;

		return true;
	}

	/**
	 * Uloží článek
	 * 
	 * @param name
	 *            název článku
	 * @param text
	 *            obsah článku
	 * @param tags
	 *            klíčová slova článku
	 * @param publicated
	 *            je článek publikován ?
	 * @param category
	 *            kategorie do kteér se vkládá
	 * @param author
	 *            uživatel, který článek vytvořil
	 * @return identifikátor článku pokud vše dopadlo v pořádku, jinak
	 *         {@code null}
	 */
	public Long saveArticle(String name, String text, Collection<String> tags,
			boolean publicated, NodeDTO category, UserInfoDTO author,
			String contextRoot) {

		// vytvoř nový článek
		Article article = new Article();

		// nasetuj do něj vše potřebné
		IContext context = processArticle(text, contextRoot);
		article.setOutputHTML(context.getOutput());
		article.setPluginCSSResources(context.getCSSResources());
		article.setPluginJSResources(context.getJSResources());
		article.setText(text);
		article.setSearchableOutput(HTMLTrimmer.trim(context.getOutput()));

		// ulož ho a nasetuj jeho id
		article = photogalleryRepository.save(article);
		if (article == null)
			return null;

		// vytvoř odpovídající content node
		ContentNodeDTO contentNodeDTO = contentNodeFacade.save(
				PhotogalleryContentService.ID, article.getId(), name, tags,
				publicated, category, author);

		if (contentNodeDTO == null)
			return null;

		// ulož do článku referenci na jeho contentnode
		ContentNode contentNode = contentNodeRepository.findOne(contentNodeDTO
				.getId());

		article.setContentNode(contentNode);
		if (photogalleryRepository.save(article) == null)
			return null;

		return article.getId();
	}

	/**
	 * Získá článek dle jeho identifikátoru pro jeho celé zobrazení
	 * 
	 * @param id
	 *            identifikátor
	 * @return DTO článku
	 */
	public ArticleDTO getArticleForDetail(Long id) {
		Article article = photogalleryRepository.findOne(id);
		if (article == null)
			return null;
		ArticleDTO articleDTO = photogalleriesMapper.mapArticleForDetail(article);
		return articleDTO;
	}

	/**
	 * Získá všechny články pro přegenerování
	 */
	public List<ArticleDTO> getAllArticlesForReprocess() {
		List<Article> articles = photogalleryRepository.findAll();
		if (articles == null)
			return null;
		List<ArticleDTO> articleDTOs = photogalleriesMapper
				.mapArticlesForReprocess(articles);
		return articleDTOs;
	}

	@Override
	public boolean deletePhotogallery(PhotogalleryDTO photogallery) {
		photogalleryRepository.delete(photogallery.getId());
		ContentNodeDTO contentNodeDTO = photogallery.getContentNode();
		if (contentNodeFacade.delete(contentNodeDTO) == false)
			return false;
	}

	@Override
	public boolean modifyPhotogallery(String name, Collection<String> tags,
			boolean publicated, PhotogalleryDTO photogallery, String contextRoot) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Long savePhotogallery(String name, Collection<String> tags,
			boolean publicated, NodeDTO category, UserInfoDTO author,
			String contextRoot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PhotogalleryDTO getPhotogalleryForDetail(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PhotogalleryDTO> getAllPhotogalleriesForReprocess() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PhotogalleryDTO> getAllPhotogalleriesForOverview() {
		List<Photogallery> photogalleries = photogalleryRepository.findAll();
		if (photogalleries == null)
			return null;
		List<PhotogalleryDTO> photogalleryDTOs = photogalleriesMapper
				.mapArticlesForOverview(photogalleries);
		return photogalleryDTOs;
	}

	@Override
	public List<PhotogalleryDTO> getAllPhotogalleriesForSearch() {
		// TODO Auto-generated method stub
		return null;
	}

}
