package cz.gattserver.grass3.facades;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.gattserver.grass3.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass3.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass3.model.domain.ContentNode;

public interface ContentTagFacade {

	/**
	 * Získá tagy pro přehled seřazený dle názvu vzestupně
	 * 
	 * @return list tagů bez jejich vazeb na své obsahy
	 */
	Set<ContentTagOverviewTO> getTagsForOverviewOrderedByName();

	/**
	 * Získá tag pro přehled
	 * 
	 * @param id
	 *            id tagu
	 * @return tag bez jeho vazby na své obsahy
	 */
	ContentTagOverviewTO getTagById(Long id);

	/**
	 * Získá tag dle názvu
	 * 
	 * @param name
	 *            název tagu
	 * @return tag bez jeho vazby na své obsahy
	 */
	ContentTagOverviewTO getTagByName(String name);

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
	int getTagContentsCount(Long tagId);

	/**
	 * Získá mapu id tagů s počty jejich obsahů, seřazených dle těchto počtů
	 * 
	 * @return mapa id tagů s počty
	 */
	Map<Long, Integer> getTagsContentsCountsMap();

	/**
	 * Získá list skupin počtů obsahů seřazenou vzestupně dle počtu. Zjistí tak,
	 * že počty obsahů tagů existují například ve skupinách:
	 * 
	 * <p>
	 * skupina tagů, které mají 1 obsah<br/>
	 * skupina tagů, které mají 2 obsahy<br/>
	 * skupina tagů, které mají 5 obsahů<br/>
	 * skupina tagů, které mají 7 obsahů<br/>
	 * skupina tagů, které mají 15 obsahů<br/>
	 * a tak dále
	 * </p>
	 *
	 * 
	 * @return list skupin počtů seřazený vzestupně dle počtu
	 */
	List<Integer> getTagsContentsCountsGroups();

	/**
	 * Vytvoří přehled tagů pro sestavení tagCloud
	 * 
	 * @param maxFontSize
	 *            velikost fontu, která má být přiřazena tagům s největším
	 *            počtem obsahů -- v závislosti na velikosti rozsahu velikosti
	 *            fontu je možné, že největší tag může mít hodnotu maxFontSize-1
	 * @param minFontSize
	 *            velikost fontu, která má být přiřazena tagům s nejmenším
	 *            počtem obsahů
	 * @return list tagů seřazených dle počtu obsahů
	 */
	List<ContentTagsCloudItemTO> createTagsCloud(int maxFontSize, int minFontSize);

}
