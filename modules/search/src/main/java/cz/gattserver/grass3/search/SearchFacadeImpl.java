package cz.gattserver.grass3.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.pages.template.GrassPage;
import cz.gattserver.grass3.search.service.SearchConnector;
import cz.gattserver.grass3.search.service.SearchField;
import cz.gattserver.grass3.search.service.SearchEntity;
import cz.gattserver.grass3.search.service.SearchHit;

@Component
public class SearchFacadeImpl implements SearchFacade {

	@Autowired
	private ConnectorAggregator connectorAggregator;

	public Set<String> getSearchModulesIds() {
		return connectorAggregator.getSearchConnectorsById().keySet();
	}

	/**
	 * Search funkce
	 * 
	 * @throws IOException
	 * @throws ParseException
	 * @throws InvalidTokenOffsetsException
	 */
	public List<SearchHit> search(String queryText, Set<Enum<? extends SearchField>> searchFields, String moduleId,
			UserInfoTO user, GrassPage callingPage) throws IOException, InvalidTokenOffsetsException, ParseException {

		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		/**
		 * Tady by šlo asi rozšiřovat i existující index (z disku/DB)
		 */
		Directory index = new RAMDirectory();
		IndexWriter w = new IndexWriter(index, config);

		/**
		 * Hledej dle search connectoru
		 */
		SearchConnector connector = connectorAggregator.getSearchConnectorsById().get(moduleId);

		/**
		 * Pokud nebyly vybrány explicitně položky k prohledávání, prohledáváme
		 * všechny
		 */
		if (searchFields == null || searchFields.isEmpty())
			searchFields = new HashSet<Enum<? extends SearchField>>(Arrays.asList(connector.getSearchFields()));

		/**
		 * Získej dostupné obsahy
		 */
		List<SearchEntity> searchEntities = connector.getAvailableSearchEntities(user);

		/**
		 * Projdi všechny dostupné obsahy
		 */
		for (SearchEntity searchEntity : searchEntities) {
			Document doc = new Document();

			// sestav dokument z nabízených polí
			for (SearchEntity.Field field : searchEntity.getFields()) {
				doc.add(new TextField(field.getName().name(), field.getContent(), Field.Store.YES));
			}

			// přidej link
			String url = callingPage.getPageURL(searchEntity.getLink().getViewerPageFactory(),
					searchEntity.getLink().getSuffix());
			doc.add(new StringField(connector.getLinkFieldName(), url, Field.Store.YES));

			w.addDocument(doc);
		}

		// zavři index
		w.close();

		/**
		 * Query
		 */
		List<String> queries = new ArrayList<String>();
		List<String> fieldNames = new ArrayList<String>();
		for (Enum<? extends SearchField> searchField : searchFields) {
			queries.add(queryText);
			fieldNames.add(searchField.name());
		}
		Query query = new MultiFieldQueryParser(fieldNames.toArray(new String[fieldNames.size()]), analyzer)
				.parse(queryText);

		List<SearchHit> hitList = new ArrayList<SearchHit>();

		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(query, Integer.MAX_VALUE);
		ScoreDoc[] hits = docs.scoreDocs;

		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			hitList.add(new SearchHit("test", "best", d.get(connector.getLinkFieldName())));
		}

		reader.close();

		return hitList;

	}
}
