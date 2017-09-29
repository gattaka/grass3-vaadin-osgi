package cz.gattserver.grass3.facades;

import java.util.Collection;
import java.util.List;

import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.dto.ContentTagDTO;

public interface ContentTagFacade {

	public List<ContentTagDTO> getContentTagsForOverview();

	/**
	 * Bere řetězec tagů, parsuje je a ukládá do nich (nebo vytvoří nové)
	 * referenci na tento obsah - <b>mění {@link ContentNode} entitu v DB</b>
	 * 
	 * @param tags
	 *            řetězec tagů oddělených mezerami
	 * @param contentNodeId
	 *            obsah, který je oanotován těmito tagy
	 * @return množina tagů, jako objektů, odpovídající těm ze vstupního řetězce
	 */
	public void saveTags(Collection<String> tags, Long contentNodeId);

	public void saveTags(Collection<String> tags, ContentNode contentNode);

	/**
	 * Získej tag dle jeho jména
	 * 
	 * @param tagName
	 *            jméno tagu
	 * @return tag
	 */
	public ContentTagDTO getContentTagById(Long contentNodeId);

	public void countContentNodes();

}
