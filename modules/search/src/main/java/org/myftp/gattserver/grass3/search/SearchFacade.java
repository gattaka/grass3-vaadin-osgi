package org.myftp.gattserver.grass3.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.myftp.gattserver.grass3.model.dto.UserInfoDTO;
import org.myftp.gattserver.grass3.search.service.ISearchConnector;
import org.myftp.gattserver.grass3.search.service.ISearchField;
import org.myftp.gattserver.grass3.search.service.SearchEntity;
import org.myftp.gattserver.grass3.windows.template.GrassWindow;

import com.vaadin.terminal.ExternalResource;

public enum SearchFacade {

	INSTANCE;

	public Set<String> getSearchModulesIds() {
		ConnectorAggregator aggregator = ConnectorAggregator.getInstance();
		return aggregator.getSearchConnectorsById().keySet();
	}

	/**
	 * Search funkce
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<String> search(String queryText,
			Set<Enum<? extends ISearchField>> searchFields, String moduleId,
			UserInfoDTO user, GrassWindow grassWindow) throws IOException,
			ParseException {

		// StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		CzechAnalyzer analyzer = new CzechAnalyzer(Version.LUCENE_36);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
				analyzer);

		/**
		 * Tady by šlo asi rozšiřovat i existující index (z disku/DB)
		 */
		Directory index = new RAMDirectory();
		IndexWriter w = new IndexWriter(index, config);

		/**
		 * Hledej dle search connectoru
		 */
		ConnectorAggregator aggregator = ConnectorAggregator.getInstance();
		ISearchConnector connector = aggregator.getSearchConnectorsById().get(
				moduleId);

		/**
		 * Pokud nebyly vybrány explicitně položky k prohledávání, prohledáváme
		 * všechny
		 */
		if (searchFields == null || searchFields.isEmpty())
			searchFields = new HashSet<Enum<? extends ISearchField>>(
					Arrays.asList(connector.getSearchFields()));

		/**
		 * Získej dostupné obsahy
		 */
		List<SearchEntity> searchEntities = connector
				.getAvailableSearchEntities(user);

		/**
		 * Projdi všechny dostupné obsahy
		 */
		for (SearchEntity searchEntity : searchEntities) {
			Document doc = new Document();

			// sestav dokument z nabízených polí
			for (SearchEntity.Field field : searchEntity.getFields()) {
				doc.add(new Field(((ISearchField) field.getName())
						.getFieldName(), field.getContent(), Field.Store.YES,
						field.isTokenized() ? Index.ANALYZED : Index.NO));
			}

			String url = new ExternalResource(grassWindow.getWindow(
					searchEntity.getLink().getViewerClass()).getURL()
					+ searchEntity.getLink().getSuffix()).getURL();

			// přidej link
			doc.add(new Field(connector.getLinkFieldName(), url,
					Field.Store.YES, Index.NO));

			w.addDocument(doc);
		}

		// zavři index
		w.close();

		/**
		 * Query
		 */
		List<String> queries = new ArrayList<String>();
		List<String> fieldNames = new ArrayList<String>();
		for (Enum<? extends ISearchField> searchField : searchFields) {
			queries.add(queryText);
			fieldNames.add(((ISearchField) searchField).getFieldName());
		}
		Query query = MultiFieldQueryParser.parse(Version.LUCENE_36,
				queries.toArray(new String[0]),
				fieldNames.toArray(new String[0]), analyzer);

		/**
		 * Search
		 */
		int hitsPerPage = 100;
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				hitsPerPage, true);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		List<String> linkList = new ArrayList<String>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			linkList.add(d.get(connector.getLinkFieldName()));
		}

		searcher.close();

		return linkList;

	}
}
