package cn.itcast.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.junit.Test;

import cn.itcast.lucene.utils.File2DocumentUtils;

public class IndexDaoTest {

	String filePath = "/luceneDatasource/IndexWriter.txt";
	String filePath2 = "/luceneDatasource/小笑话.txt";

	IndexDao indexDao = new IndexDao();

	@Test
	public void testSave() {
		Document doc = File2DocumentUtils.file2Document(filePath);
		doc.setBoost(3f);
		indexDao.save(doc);

		Document doc2 = File2DocumentUtils.file2Document(filePath2);
		// doc2.setBoost(1.0f);
		indexDao.save(doc2);
	}

	@Test
	public void testDelete() {
		Term term = new Term("path", filePath);
		indexDao.delete(term);
	}

	@Test
	public void testUpdate() {
		Term term = new Term("path", filePath);

		Document doc = File2DocumentUtils.file2Document(filePath);
		doc.getField("content").setValue("中文");

		indexDao.update(term, doc);
	}

	@Test
	public void testSearch() {
		// String queryString = "IndexWriter";
		String queryString = "room";
		QueryResult qr = indexDao.search(queryString, 0, 10);

		System.out.println("result:" + qr.getRecordCount());
		for (Document doc : qr.getRecordList()) {
			File2DocumentUtils.printDocumentInfo(doc);
		}
	}

}
