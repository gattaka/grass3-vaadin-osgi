package cz.gattserver.grass3.search;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.search.service.SearchField;
import cz.gattserver.grass3.search.service.SearchHit;

public interface SearchFacade {

	public Set<String> getSearchModulesIds();

	/**
	 * Search funkce
	 * 
	 * @throws IOException
	 * @throws ParseException
	 * @throws InvalidTokenOffsetsException
	 */
	public List<SearchHit> search(String queryText, Set<Enum<? extends SearchField>> searchFields, String moduleId,
			UserInfoTO user, GrassPage callingPage) throws IOException, InvalidTokenOffsetsException, ParseException;
}
