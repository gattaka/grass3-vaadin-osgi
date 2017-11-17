package cz.gattserver.grass3.facades;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.dto.ContentTagCountTO;
import cz.gattserver.grass3.model.dto.ContentTagOverviewDTO;

public interface ContentTagFacade {

	/**
	 * Získá tagy pro přehled seřazený dle názvu vzestupně
	 * 
	 * @return list tagů bez jejich vazeb na své obsahy
	 */
	List<ContentTagOverviewDTO> getContentTagsForOverviewOrderedByName();

	/**
	 * Získá tag pro přehled
	 * 
	 * @param id
	 *            id tagu
	 * @return tag bez jeho vazby na své obsahy
	 */
	ContentTagOverviewDTO getContentTagById(Long id);

	/**
	 * Získá tag dle názvu
	 * 
	 * @param name
	 *            název tagu
	 * @return tag bez jeho vazby na své obsahy
	 */
	ContentTagOverviewDTO getContentTagByName(String name);

	/**
	 * Bere řetězec tagů, parsuje je a ukládá do nich (nebo vytvoří nové)
	 * referenci na tento obsah - <b>mění {@link ContentNode} entitu v DB</b>
	 * 
	 * @param tags
	 *            řetězec tagů oddělených mezerami
	 * @param contentNodeId
	 *            id obsahu, který je otagován těmito tagy
	 */
	void saveTags(Collection<String> tags, Long contentNodeId);

	/**
	 * Bere řetězec tagů, parsuje je a ukládá do nich (nebo vytvoří nové)
	 * referenci na tento obsah - <b>mění {@link ContentNode} entitu v DB</b>
	 * 
	 * @param tags
	 *            řetězec tagů oddělených mezerami
	 * @param contentNode
	 *            obsah, který je otagován těmito tagy
	 */
	void saveTags(Collection<String> tags, ContentNode contentNode);

	/**
	 * Získá počet obsahů s daným tagem
	 * 
	 * @param tagId
	 *            id tagu jehož počet obsahů chci získat
	 * @return počet obsahů s daným tagem
	 */
	int getContentNodesCount(Long tagId);

	/**
	 * Získá mapu počtů obsahů dle jednotlivých tagů seřazenou dle počtu
	 * 
	 * @return mapa počtů dle tagů
	 */
	Map<Long, Integer> getContentNodesCounts();

	/**
	 * Získá info o tagu s nejnižším počtem obsahů
	 * 
	 * @return počet obsahů
	 */
	ContentTagCountTO getTagContentNodesLowestCount();

	/**
	 * Získá info o tagu s nejvyšším počtem obsahů
	 * 
	 * @return počet obsahů
	 */
	ContentTagCountTO getTagContentNodesHighestCount();

}
