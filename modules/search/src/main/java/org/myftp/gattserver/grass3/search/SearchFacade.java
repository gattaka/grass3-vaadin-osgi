package org.myftp.gattserver.grass3.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.search.service.SearchHit;

public enum SearchFacade {

	INSTANCE;

	private ArticleFacade articleFacade = ArticleFacade.INSTANCE;
 
	/**
	 * Demo funkce
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<SearchHit> searchArticles(String queryText) throws IOException,
			ParseException {

		final String searchFieldName = "obsah";
		final String contentLinkFieldName = "název";

		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36,
				analyzer);

		/**
		 * Tady by šlo asi rozšiřovat i existující index (z disku/DB)
		 */
		Directory index = new RAMDirectory();
		IndexWriter w = new IndexWriter(index, config);

		/**
		 * Tohle si pak bude dělat v rámci searchConnector-u modul článků sám
		 */
		List<ArticleDTO> articles = articleFacade.getAllArticles();
		for (ArticleDTO article : articles) {
			Document doc = new Document();
			doc.add(new Field("název", article.getContentNode().getName(),
					Field.Store.YES, Index.ANALYZED));
			doc.add(new Field("autor", article.getContentNode().getAuthor()
					.getName(), Field.Store.YES, Index.ANALYZED));
			doc.add(new Field("obsah", article.getOutputHTML(),
					Field.Store.YES, Index.ANALYZED));
			w.addDocument(doc);
		}

		w.close();

		/**
		 * Query
		 */
		Query query = new QueryParser(Version.LUCENE_36, searchFieldName,
				analyzer).parse(queryText);

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

		List<SearchHit> hitList = new ArrayList<SearchHit>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			hitList.add(new SearchHit(d.get(searchFieldName), d
					.get(contentLinkFieldName)));
		}

		searcher.close();

		return hitList;

	}
}
