//package se.index.test;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//
//
//public class IndexTest{
//	private static int count = 1;
//	
//	
//	private static  final String url = "http://10.100.58.51:8080/NetRobot/servlet/Vcrobot?email=487f87505f619bf9ea08f26bb34f8118&keywords=";
//	private static final String[] question = {
//		"��ɽС����ô�ߣ�",
//		"����У԰����������",
//		"213����˭��˧��",
//		"���ƴ�У����˭��",
//		"����������ô����",
//		"�����أ�",
//		"��������ɽ�˶಻��",
//		"����ٸ��Ʊ�Ƕ��٣�",
//		"�����ҿ��ˣ�ֱ���忼��",
//		"���ʲô���֣�"
//	};
//	
//	public static void main(String[] args) {
//		for (int i = 0; i < 10; i++) {
//			doGet(url+question[i]);
//			try {
//				Thread.sleep(100);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	
//	
//	
//	//���������û�
//	private static void createThread(){
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				long start = System.currentTimeMillis();
//				//���ͷ�������
//				doGet(url);
//				long end = System.currentTimeMillis();
//				System.out.println((count++)+"  "+(end-start)+"ms");
//			}
//		}).start();
//	}
//	
//	/**
//     * doGet������������
//     * @param url
//     * @return
//     */
//    public static String doGet(String url) {
//
//        try{
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(url);
//            HttpResponse httpResponse = httpClient.execute(httpGet);
//            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                HttpEntity httpEntity = httpResponse.getEntity();
//                InputStream inputStream = httpEntity.getContent();
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                String line = null;
//                StringBuffer stringBuffer = new StringBuffer();
//                while (null != (line = bufferedReader.readLine())) {
//                    stringBuffer.append(line);
//                }
//                return stringBuffer.toString();
//            }
//        }catch (Exception e){e.printStackTrace();}
//
//        return null;
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////package se.index.test;
////
////import java.util.HashSet;
////
////import org.apache.lucene.analysis.standard.StandardAnalyzer;
////import org.apache.lucene.document.Document;
////import org.apache.lucene.document.Field.Store;
////import org.apache.lucene.document.StringField;
////import org.apache.lucene.document.TextField;
////import org.apache.lucene.index.Term;
////import org.apache.lucene.queryparser.classic.ParseException;
////import org.apache.lucene.queryparser.classic.QueryParser;
////import org.apache.lucene.search.Query;
////import org.apache.lucene.util.Version;
////
////import se.index.manager.IndexManager;
////import se.index.model.ConfigBean;
////import se.index.model.IndexConfig;
////import se.index.model.SearchResultBean;
////import se.index.operation.NRTIndex;
////import se.index.operation.NRTSearch;
////
////public class IndexTest {
////
////	public static void main(String[] args) throws ParseException {
////		HashSet<ConfigBean> configBeans = new HashSet<ConfigBean>();
////		//���3�������ļ�����
////		for (int i = 0; i < 4; i++) {
////			ConfigBean bean = new ConfigBean();
////			bean.setIndexPath("E:/lucene_index/test");
////			bean.setIndexName("test"+i);
////			configBeans.add(bean);
////		}
////		IndexConfig.setConfigBeans(configBeans);
////		String indexName = "test0";
////		NRTIndex nrtIndex = new NRTIndex(indexName);
////		Document doc1 = new Document();
////		
////		doc1.add(new StringField("id","1",Store.YES));
////		doc1.add(new TextField("content","����ѧԺ",Store.YES));
////		nrtIndex.addDocument(doc1);
////		
////		Document doc2 = new Document();
////		doc1.add(new StringField("id","2",Store.YES));
////		doc1.add(new TextField("content","Lucene��������",Store.YES));
////		nrtIndex.addDocument(doc2);
////		nrtIndex.commit();
////		System.out.println("�����2����¼");
////		
////		NRTSearch nrtSearch = new NRTSearch(indexName);
////		QueryParser parser = new QueryParser(Version.LUCENE_43, "content", new StandardAnalyzer(Version.LUCENE_43));
////		Query query = parser.parse("����ѧԺLucene��������");
////		SearchResultBean bean = nrtSearch.search(query, 0, 10);
////		System.out.println("��1�β�ѯ"+bean.getCount());
////
////		doc1 = new Document();
////		doc1.add(new StringField("id","2",Store.YES));
////		doc1.add(new TextField("content","",Store.YES));
////		Term term = new Term("id", "2");
////		nrtIndex.updateDocument(term, doc1);
////		nrtIndex.commit();
////		System.out.println("��1���޸ļ�¼");
////		bean = nrtSearch.search(query, 0, 10);
////		System.out.println("��2�β�ѯ"+bean.getCount());
////	}
////
////}
