package cz.gattserver.grass3.facades;

import java.util.Collection;
import java.util.List;

import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.dto.ContentTagOverviewDTO;

public interface ContentTagFacade {

	/**
	 * Získá tagy pro přehled
	 * 
	 * @return list tagů bez jejich vazeb na své obsahy
	 */
	public List<ContentTagOverviewDTO> getContentTagsForOverview();

	/**
	 * Získá tag pro přehled
	 * 
	 * @param id
	 *            id tagu
	 * @return tag bez jeho vazby na své obsahy
	 */
	public ContentTagOverviewDTO getContentTagById(Long id);

	/**
	 * Získá tag pro přehled
	 * 
	 * @param name
	 *            název tagu
	 * @return tag bez jeho vazby na své obsahy
	 */
	public ContentTagOverviewDTO getContentTagByName(String name);

	/**
	 * Bere řetězec tagů, parsuje je a ukládá do nich (nebo vytvoří nové)
	 * referenci na tento obsah - <b>mění {@link ContentNode} entitu v DB</b>
	 * 
	 * @param tags
	 *            řetězec tagů oddělených mezerami
	 * @param contentNodeId
	 *            id obsahu, který je otagován těmito tagy
	 */
	public void saveTags(Collection<String> tags, Long contentNodeId);

	/**
	 * Bere řetězec tagů, parsuje je a ukládá do nich (nebo vytvoří nové)
	 * referenci na tento obsah - <b>mění {@link ContentNode} entitu v DB</b>
	 * 
	 * @param tags
	 *            řetězec tagů oddělených mezerami
	 * @param contentNode
	 *            obsah, který je otagován těmito tagy
	 */
	public void saveTags(Collection<String> tags, ContentNode contentNode);

	/**
	 * Přepočítá počty obsahů u jednotlivých tagů a tím nastaví jejich váhu
	 */
	public void processContentNodesCounts();

}
