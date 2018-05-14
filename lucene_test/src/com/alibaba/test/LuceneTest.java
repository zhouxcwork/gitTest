package com.alibaba.test;



import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest {
	@Test
	public void testLucene() throws Exception {
//		1、执行索引库的目录
		Directory directory = FSDirectory.open(new File("H:\\indexRespo"));
		
//		2、指定分词器  --标准分词器
		Analyzer analyzer = new IKAnalyzer();
//		3、创建一个配置对象
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
//		4、创建一个 写入索引对象
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
		//5.写入对象
		File files = new File("G:\\CZBK就业班\\框架\\lucene\\Lucene&solr-day01\\资料\\上课用的查询资料searchsource");
		
		File[] list = files.listFiles();
		for (File file : list) {
			Document doc = new Document();
			//文件名
			TextField fileNameField = new TextField("name", file.getName(),Store.YES);
			doc.add(fileNameField);
			//文件路径
			Field  filePathField = new TextField("path", file.getPath(), Store.YES);
			doc.add(filePathField);
//			文件大小  单位 b
			long sizeOf = FileUtils.sizeOf(file);
			Field  fileSizeField = new TextField("size", sizeOf+"", Store.YES);
			doc.add(fileSizeField);
//			文件内容
			String fileContent = FileUtils.readFileToString(file);
			Field  fileContentField = new TextField("content", fileContent, Store.YES);
			doc.add(fileContentField);
			
			indexWriter.addDocument(doc);
		}
//		6、关闭IndexWriter对象
		indexWriter.close();
	}
	
	
	@Test
	public void testSearchIndex() throws  Exception {
//		1、指定索引库的目录
		Directory  directory = FSDirectory.open(new File("H:\\indexRespo"));

//		2、创建一个读取索引对象
		IndexReader  indexReader = DirectoryReader.open(directory);
		
//		3、创建一个搜索索引的对象
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
//		4、执行查询
		//4.1执行trem查询
		//Query query = new TermQuery(new Term("name", "spring"));
		//4.2查询所有
		//Query query = new MatchAllDocsQuery();
		
		//4.3分词查询
//		QueryParser queryParser = new QueryParser("name", new IKAnalyzer());
//		Query query = queryParser.parse("spring is a project");
		
//		QueryParser queryParser = new MultiFieldQueryParser(new String [] {"name","content"}, new IKAnalyzer());
//		Query query = queryParser.parse("spring is a project");
		
		//4.4文件大小区间  B
		//Query query = NumericRangeQuery.newLongRange("size", 0l, 100l, true, true);
		
		BooleanQuery query = new BooleanQuery();
		Query query1 = new TermQuery(new Term("content", "spring"));
		Query query2 = new TermQuery(new Term("name", "spring"));
		query.add(query1, Occur.SHOULD);
		query.add(query2, Occur.MUST_NOT);  //or   and
		
		
		System.out.println("查询语法："+query);
		
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("总条数："+topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = indexSearcher.doc(docId);
			System.out.println("name：--"+doc.get("name"));
//			System.out.println("size：--"+doc.get("size"));
//			System.out.println("path：--"+doc.get("path"));
//			System.out.println(doc.get("content"));
			System.out.println("*************************************");
		}
		
//		5、关闭资源
		indexReader.close();
	}
	
	
}
