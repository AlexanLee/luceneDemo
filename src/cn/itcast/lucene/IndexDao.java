package cn.itcast.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumberTools;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeasy.analysis.MMAnalyzer;

public class IndexDao {

	String indexPath = "/Users/lijianli/Rep/LuceneDemoSrc/luceneIndex";

	// Analyzer analyzer = new StandardAnalyzer();
	Analyzer analyzer = new MMAnalyzer();

	public void save(Document doc) {
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(indexPath, analyzer, MaxFieldLength.LIMITED);
			indexWriter.addDocument(doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				indexWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void delete(Term term) {
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(indexPath, analyzer, MaxFieldLength.LIMITED);
			indexWriter.deleteDocuments(term);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				indexWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void update(Term term, Document doc) {
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(indexPath, analyzer, MaxFieldLength.LIMITED);
			indexWriter.updateDocument(term, doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				indexWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public QueryResult search(String queryString, int firstResult, int maxResults) {
		try {
			String[] fields = { "name", "content" };
			Map<String, Float> boosts = new HashMap<String, Float>();
			boosts.put("name", 3f);
			// boosts.put("content", 1.0f); Ĭ��Ϊ1.0f

			QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer, boosts);
			Query query = queryParser.parse(queryString);

			return search(query, firstResult, maxResults);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public QueryResult search(Query query, int firstResult, int maxResults) {
		IndexSearcher indexSearcher = null;

		try {
			// 2�����в�ѯ
			indexSearcher = new IndexSearcher(indexPath);
			Filter filter = new RangeFilter("size", NumberTools.longToString(200)
					, NumberTools.longToString(1000), true, true);

			// ========== ����
			Sort sort = new Sort();
			sort.setSort(new SortField("size")); // Ĭ��Ϊ����
			// sort.setSort(new SortField("size", true));
			// ==========

			TopDocs topDocs = indexSearcher.search(query, filter, 10000, sort);

			int recordCount = topDocs.totalHits;
			List<Document> recordList = new ArrayList<Document>();

			// ============== ׼��������
			Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
			Scorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(formatter, scorer);

			Fragmenter fragmenter = new SimpleFragmenter(50);
			highlighter.setTextFragmenter(fragmenter);
			// ==============

			// 3��ȡ����ǰҳ������
			int end = Math.min(firstResult + maxResults, topDocs.totalHits);
			for (int i = firstResult; i < end; i++) {
				ScoreDoc scoreDoc = topDocs.scoreDocs[i];

				int docSn = scoreDoc.doc; // �ĵ��ڲ����
				Document doc = indexSearcher.doc(docSn); // ���ݱ��ȡ����Ӧ���ĵ�

				// =========== ����
				// ���ظ�����Ľ���������ǰ����ֵ��û�г��ֹؼ��֣��᷵�� null
				String hc = highlighter.getBestFragment(analyzer, "content", doc.get("content"));
				if (hc == null) {
					String content = doc.get("content");
					int endIndex = Math.min(50, content.length());
					hc = content.substring(0, endIndex);// ���ǰ50���ַ�
				}
				doc.getField("content").setValue(hc);
				// ===========

				recordList.add(doc);
			}

			return new QueryResult(recordCount, recordList);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				indexSearcher.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
