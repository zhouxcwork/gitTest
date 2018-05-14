package com.alibaba.solrtest;



import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class solrTest {
	
	//添加
	@Test
	public void testAdd() throws Exception {
		SolrServer solrServer = new  HttpSolrServer("http://localhost:8080/solr/collection1");
		for (int i = 1; i <11; i++) {
			SolrInputDocument doc = new SolrInputDocument();
			//id必须要添加
			doc.addField("id", ""+i);
			doc.addField("name", "solr的标题："+i);
			doc.addField("content", "solr的内容==="+i);
			solrServer.add(doc);
			System.out.println("111");
			System.out.println("2222");   
		}
		solrServer.commit();
	}
//	修改
	@Test
	public void testUpdate() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		//自动覆盖  如果id一样就会覆盖
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", "1");
		doc.addField("name", "solr添加标题-修改后");
		doc.addField("content", "solr添加内容-修改后");
		solrServer.add(doc);	
		solrServer.commit();
	}
	
//	删除
	@Test
	public void testDelete() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		//solrServer.deleteById("1");
		
		solrServer.deleteByQuery("name:solr添加标题2");
		solrServer.commit();
	}
	
//	查询
	@Test
	public void testQuery() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		SolrQuery solrQuery = new SolrQuery();
		
		solrQuery.setQuery("name:solr");
		//solrQuery.set("q", "solr");//有默认查询域
		
		QueryResponse queryResponse = solrServer.query(solrQuery);
		SolrDocumentList results = queryResponse.getResults();
		System.out.println("总条数："+results.getNumFound());
//		System.out.println("results.getStart()："+results.getStart());
//		System.out.println("results.getMaxScore()："+results.getMaxScore());
		
		//只查询了 前十条
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("name"));
			System.out.println(solrDocument.get("content"));
		}
	}
	
		//添加
		@Test
		public void testAdd1() throws Exception {
			SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
			
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", "你好");
			doc.addField("name", "标题");
			doc.addField("content", "传智播客");
			solrServer.add(doc);
			solrServer.commit();
		}
		
		//更新
		@Test
		public void testUpdate1() throws Exception {
			SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", "你好");
			doc.addField("name", "h");
			doc.addField("content", "传智播客");
			solrServer.add(doc);
			solrServer.commit();
		}
		//删除
		@Test
		public void testDelete1() throws Exception {
			SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
			//solrServer.deleteById("mmp");
			solrServer.deleteByQuery("name:h");
			solrServer.commit();
		}
		//查询
		@Test
		public void testQuery1() throws Exception {
			SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
			SolrQuery solrQuery = new SolrQuery();
			//"params": {
//			     "q": "花儿",
//				"df": "product_name",
				
//				"product_catalog_name:美味厨房",
//				"product_price:[* TO 10]"
				
//			      "start": "0",
//			      "rows": "10",
//			      "sort": "product_price asc",
//				      "hl": "true",
//				      "hl.simple.pre": "<span style='color:red'>",
//				      "hl.simple.post": "</span>",
//				      "hl.fl": "product_name",
			
			solrQuery.set("q","小黄人");
			solrQuery.set("df", "product_keywords");
			//solrQuery.addFilterQuery("product_catalog_name:美味厨房");
			solrQuery.addFilterQuery("product_price:[* TO 109]");
			solrQuery.setStart(0);
			solrQuery.setRows(10);
			solrQuery.addSort("product_price", ORDER.desc);
			solrQuery.setHighlight(true);
			solrQuery.setHighlightSimplePre("<span style=\"color:red\">");
			solrQuery.setHighlightSimplePost("</span>");
			solrQuery.addHighlightField("product_name");
			
			QueryResponse queryResponse = solrServer.query(solrQuery);
			
			Map<String, Map<String, List<String>>> map = queryResponse.getHighlighting();
			//"highlighting": {
//			    "3": {
//			      "product_name": [
//			        "神偷奶爸电影同款 惨叫发泄公仔 发声<span style='color:red'>小黄人</span>"
//			      ]
//			    },
//			    "554": {},
//			    }
			
			SolrDocumentList documentList = queryResponse.getResults();
			for (SolrDocument solrDocument : documentList) {
				Map<String, List<String>> map2 = map.get(solrDocument.get("id"));
				List<String> list = map2.get("product_name");
				String product_name=null;
				if(list != null&&list.size()>0) {
					product_name=list.get(0);
				}else {
					product_name = (String) solrDocument.get("product_name");
				}
				System.out.println(solrDocument.get("id"));
				System.out.println(product_name);
				System.out.println(solrDocument.get("product_price"));
				System.out.println(solrDocument.get("product_picture"));
				System.out.println(solrDocument.get("product_catalog_name"));
				System.out.println("=========================================================");
			}
		}
	
}
