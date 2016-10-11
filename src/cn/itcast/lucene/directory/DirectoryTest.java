package cn.itcast.lucene.directory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import cn.itcast.lucene.utils.File2DocumentUtils;

public class DirectoryTest {
	String filePath = "/Users/lijianli/Rep/LuceneDemoSrc/luceneDatasource/IndexWriter.txt";

	String indexPath = "/Users/lijianli/Rep/LuceneDemoSrc/luceneIndex";

	Analyzer analyzer = new StandardAnalyzer();

	@Test
	public void test1()throws Exception {
		// Directory dir = FSDirectory.getDirectory(indexPath);
		Directory dir = new RAMDirectory();
		
		Document doc = File2DocumentUtils.file2Document(filePath);
		IndexWriter indexWriter = new IndexWriter(dir, analyzer, MaxFieldLength.LIMITED);
		indexWriter.addDocument(doc);
		indexWriter.close();
	}
	
	@Test
	public void test2() throws Exception{
		Directory fsDir = FSDirectory.getDirectory(indexPath);
	
		Directory ramDir = new RAMDirectory(fsDir);
		
		IndexWriter ramIndexWriter = new IndexWriter(ramDir, analyzer, MaxFieldLength.LIMITED);
		Document doc = File2DocumentUtils.file2Document(filePath);
		ramIndexWriter.addDocument(doc);
		ramIndexWriter.close();
		
		IndexWriter fsIndexWriter = new IndexWriter(fsDir, analyzer,true, MaxFieldLength.LIMITED);
		fsIndexWriter.addIndexesNoOptimize(new Directory[]{ramDir});
		//	fsIndexWriter.flush();
		//	fsIndexWriter.optimize();
		fsIndexWriter.close();
	}
	
	
	@Test
	public void test3() throws Exception{
		Directory fsDir = FSDirectory.getDirectory(indexPath);
		IndexWriter fsIndexWriter = new IndexWriter(fsDir, analyzer, MaxFieldLength.LIMITED);
		
		fsIndexWriter.optimize();
		fsIndexWriter.close();
	}
}
