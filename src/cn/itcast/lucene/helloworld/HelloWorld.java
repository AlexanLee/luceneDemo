package cn.itcast.lucene.helloworld;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

import cn.itcast.lucene.utils.File2DocumentUtils;

public class HelloWorld {

	String filePath = "/Users/lijianli/Rep/LuceneDemoSrc/luceneDatasource/IndexWriter.txt";

	String indexPath = "/Users/lijianli/Rep/LuceneDemoSrc/luceneIndex";

	Analyzer analyzer = new StandardAnalyzer();

	@Test
	public void createIndex() throws Exception {
		// file --> doc
		Document doc = File2DocumentUtils.file2Document(filePath);

		IndexWriter indexWriter = new IndexWriter(indexPath, analyzer, true,
				MaxFieldLength.LIMITED);
		indexWriter.addDocument(doc);
		indexWriter.close();
	}

	@Test
	public void search() throws Exception {
//		String queryString = "document";
		String queryString = "document";

		String[] fields = { "name", "content" };
		QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
		Query query = queryParser.parse(queryString);

		IndexSearcher indexSearcher = new IndexSearcher(indexPath);
		Filter filter = null;
		TopDocs topDocs = indexSearcher.search(query, filter, 10000);
		System.out.println("search result:" + topDocs.totalHits + "");

		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			int docSn = scoreDoc.doc;
			Document doc = indexSearcher.doc(docSn);
			File2DocumentUtils.printDocumentInfo(doc);
		}
	}
}
