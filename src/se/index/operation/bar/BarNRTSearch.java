package se.index.operation.bar;

import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import se.index.bar.db.IndexXUSTbarDb;
import se.index.model.SearchResultBean;
import se.index.operation.NRTSearch;
import se.index.utils.Const;
import se.index.utils.QueryUtil;

/**
 * Db��������
 * @author Dolphix.J Qing
 *
 */
public class BarNRTSearch extends NRTSearch{

	private IndexXUSTbarDb db = new IndexXUSTbarDb();

	public BarNRTSearch() {
		super(Const.INDEX_NAME);
	}

	/**
	 * ִ������
	 * @param q
	 * @return
	 * @throws ParseException
	 */
	private SearchResultBean executeQueryTopicNote(String q,int start, int end) {
		if (start > end || start < 0) {
			return null;
		}
		QueryParser parser = new QueryParser(Version.LUCENE_43, "title", new IKAnalyzer());
		Query query = null;
		SearchResultBean bean = null;
		try {
			query = parser.parse(q);
			bean = search(query, start, end,new Sort(new SortField[]{new SortField("title", SortField.Type.STRING),new SortField("count", SortField.Type.STRING)}));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bean;
	}


	/**
	 * ִ������
	 * @param q
	 * @return
	 * @throws ParseException
	 */
	private SearchResultBean executeQueryDetail(String q,int start, int end) {
		if (start > end || start < 0) {
			return null;
		}
		QueryParser parser = new QueryParser(Version.LUCENE_43, "content", new IKAnalyzer());
		Query query = null;
		SearchResultBean bean = null;
		try {
			query = parser.parse(q);
			bean = search(query, start, end,new Sort(new SortField[]{new SortField("content", SortField.Type.STRING),new SortField("rid", SortField.Type.STRING)}));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 * ������������������ӣ���SQL��
	 * @param keywords
	 * @return
	 */
	public String searchHotTopicUrl(String keywords){
		int SIZE = 3;
		if (null == keywords || keywords.equals("")) {
			return null;
		}
		SearchResultBean bean = executeQueryTopicNote(keywords,0,SIZE);

		List<Document> docs = bean.getDocs();
		if (null == docs || 0 == docs.size()) {
			return null;
		}
		return docs.get((int)(Math.random() * docs.size())).get("url");
	}
	/**
	 * SQL �������ݿ⣬�����÷���ֵ
	 * @param url
	 * @param n
	 * @return
	 */
	public String searchDb(String url, int n) {
		List<String> data = db.searchDbByUrl(url,n);
		if (null == data || 0 == data.size()) {
			return null;
		}
		return data.get((int)(Math.random() * data.size()));
	}

	/**
	 * ������������������ݣ�ֱ�ӷ��أ�
	 * @param keywords
	 * @return
	 */
	public String searchRandomDetail(String keywords){
		int SIZE = 13;
		SearchResultBean bean = executeQueryDetail(keywords,0,SIZE);
		List<Document> docs = bean.getDocs();
		if (null == docs || 0 == docs.size()) {
			return null;
		}
		return docs.get((int)(Math.random() * docs.size())).get("content").trim();
	}
	/**
	 * ��������+���ݿ�����
	 * @param keywords
	 * @return
	 */
	public String searchRandomTopicReply(String keywords){
		int SIZE = 13;
		if (null == keywords || keywords.equals("")) {
			return null;
		}
		String url = searchHotTopicUrl(keywords);
		if (null == url) {
			return null;
		}
		return searchDb(url, SIZE);
	}
	//------------��ϵ����������--------------


	/**
	 * ��������Q����ضȡ��ظ���
	 * @param q
	 * @return
	 * @throws ParseException
	 */
	private SearchResultBean executeQueryQuestion(String q,int start, int end) {
		if (start > end || start < 0) {
			return null;
		}
		QueryParser parser = new QueryParser(Version.LUCENE_43, "title", new IKAnalyzer());
		Query query = null;
		SearchResultBean bean = null;
		try {
			query = parser.parse(q);
			bean = search(query, start, end,new Sort(new SortField[]{SortField.FIELD_SCORE,new SortField("count", SortField.Type.INT,true)}));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * �����ظ�A����ضȡ����Ŷ�
	 * @param a
	 * @return
	 * @throws ParseException
	 */
	private SearchResultBean executeQueryAnswer(String a,int start, int end) {
		if (start > end || start < 0) {
			return null;
		}
		//ͨ�������
		Query query = new WildcardQuery(new Term("url2", "*"+a+"*"));
		SearchResultBean bean = search(query, start, end,new Sort(new SortField[]{new SortField("score", SortField.Type.FLOAT,true),SortField.FIELD_SCORE}));
		return bean;
	}


	/**
	 * ��ϵ����������
	 * @param email
	 * @param keywords
	 * @return
	 */
	public HashMap<String,String> searchInContext(String email,String keywords){

		//1.�����ݿ��ȡ������
		List<String> context = db.readDbContext(email,keywords);
		//2.�뵱ǰ��ƴ��
		if (context != null && context.size() > 1) {
			keywords = QueryUtil.parseQuery(context);
		}
//		System.out.println(keywords);
		//3.ִ�����������
		SearchResultBean bean = executeQueryQuestion(keywords,0,getSearchQCount());
		List<Document> docs = bean.getDocs();
		if (null == docs || 0 == docs.size()) {
			return null;
		}
		//4.�������
		String qUrl = docs.get((int)(Math.random() * docs.size())).get("url").trim();
		System.out.println("Q - "+qUrl);
		//��������
		bean = executeQueryAnswer(qUrl, 0, getSearchACount());
		docs = bean.getDocs();
		if (null == docs || 0 == docs.size()) {
			return null;
		}
		//5.��ô�
		HashMap<String, String> hashMap = new HashMap<String,String>();
		String rid = docs.get((int)(Math.random() * docs.size())).get("rid").trim();
		String content = docs.get((int)(Math.random() * docs.size())).get("content").trim();
		System.out.println("A - "+content);
		hashMap.put("key", rid);
		hashMap.put("text", content);
		
		return hashMap;
	}

	/**
	 * ����Q��¼����
	 * @return
	 */
	private int getSearchQCount(){

		if (Math.random() > 0.9f) {
			return 3;
		}else if (Math.random() > 0.7f) {
			return 2;
		}
		return 1;
	}
	
	/**
	 * ����A��¼����
	 * @return
	 */
	private int getSearchACount(){

		if (Math.random() > 0.9f) {
			return 7;
		}else if (Math.random() > 0.7f) {
			return 5;
		}else if (Math.random() > 0.5f) {
			return 3;
		}
		return 2;
	}
	
	
	public static void main(String[] args) {

//		System.out.println(getSearchQCount());
//		System.out.println(getSearchACount());
		
//		BarNRTSearch bs = new BarNRTSearch();
//		//searchInContext("487f87505f619bf9ea08f26bb34f8118", "��û������ɽ��");
//		System.out.println(bs.searchInContext("487f87505f619bf9ea08f26bb34f8118", "��û������ɽ��"));
	}
}
