package cz.gattserver.grass3.facades;

import java.util.Collection;
import java.util.List;

import cz.gattserver.grass3.model.domain.ContentNode;
import cz.gattserver.grass3.model.dto.ContentTagDTO;

public interface IContentTagFacade {

	public List<ContentTagDTO> getContentTagsForOverview();

	/**
	 * Bere řetězec tagů, parsuje je a ukládá do nich (nebo vytvoří nové)
	 * referenci na tento obsah - <b>mění {@link ContentNode} entitu v DB</b>
	 * 
	 * @param tags
	 *            řetězec tagů oddělených mezerami
	 * @param contentId
	 *            obsah, který je oanotován těmito tagy
	 * @return množina tagů, jako objektů, odpovídající těm ze vstupního řetězce
	 */
	public boolean saveTags(Collection<String> tags, Long contentId);

	/**
	 * Získej tag dle jeho jména
	 * 
	 * @param tagName
	 *            jméno tagu
	 * @return tag
	 */
	public ContentTagDTO getContentTagById(Long id);

	public boolean countContentNodes();

}
