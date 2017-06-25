package cn.lhn.test;

import java.io.File;
import java.io.IOException;
import java.util.List;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cn.lhn.dao.BookDao;
import cn.lhn.dao.BookDaoImpl;
import cn.lhn.pojo.Book;

public class Test1 {
	@Test
	public void CreateIndexTest() throws IOException{
		
	
	//	1. 采集数据
		BookDao bookDao= new BookDaoImpl();
		List<Book> bookList = bookDao.queryBookList();
		
	//	3. 创建分析器（分词器）
//		Analyzer analyzer = new StandardAnalyzer();
		//创建 第三方的 分词工具
		IKAnalyzer ikAnalyzer = new IKAnalyzer();
		
	//	4. 创建IndexWriterConfig配置信息类                                                                                 版本        
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, ikAnalyzer);
	//	5. 创建Directory对象，声明索引库存储位置
		Directory directory = FSDirectory.open(new File("g:\\aaa"));
	//	6. 创建IndexWriter写入对象
		IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig );
	//	7. 把Document写入到索引库中
		for (Book book : bookList) {
			//每个 book都是一个 文档对象  所以在这创建 document 对象 
			//	2. 创建Document文档对象
			Document doc = new Document();
			//文档对象里 又 有好多域  创建 域对象
			Integer id = book.getId();
			String name = book.getName();
			String desc = book.getDesc();
			Float price = book.getPrice();
			String pic = book.getPic();
			doc.add(new TextField("id", String.valueOf(id), Store.YES));
			doc.add(new TextField("name", name, Store.YES));
			doc.add(new TextField("price", String.valueOf(price), Store.YES));
			doc.add(new TextField("pic", pic, Store.YES));
			doc.add(new TextField("desc", desc, Store.NO));
			//保存文档到索引库存（   索引 保存  文档保存）
			indexWriter.addDocument(doc);
		}
	//	8. 释放资源
		indexWriter.close();
	}
	
	//搜索
	@Test
	public void SearchIndexTest() throws IOException{
//		 创建Query搜索对象
		Query query = new TermQuery(new Term("desc", "lucene"));
//		 2. 创建Directory流对象,声明索引库位置
		Directory directory = FSDirectory.open(new File("g:\\aaa"));
//		 3. 创建索引读取对象IndexReader
		IndexReader indexReader= DirectoryReader.open(directory);
//		 4. 创建索引搜索对象IndexSearcher
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		TopDocs search = indexSearcher.search(query, 5);
		ScoreDoc[] scoreDocs = search.scoreDocs;
//		 5. 使用索引搜索对象，执行搜索，返回结果集TopDocs
		for (ScoreDoc scoreDoc : scoreDocs) {
//		 6. 解析结果集
			//取出
			Document doc = indexSearcher.doc(scoreDoc.doc);
			System.out.println(doc.get("id"));
			System.out.println(doc.get("name"));
			System.out.println(doc.get("pic"));
			System.out.println(doc.get("price"));
			System.out.println(doc.get("desc"));
		}
//		 7. 释放资源3
		indexReader.close();
		
	}
	
}
