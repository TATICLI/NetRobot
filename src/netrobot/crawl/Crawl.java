package netrobot.crawl;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import netrobot.utils.CharsetUtil;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;


public abstract class Crawl {

	
	private static Logger log = Logger.getLogger(Crawl.class);
	//����Դ����
	private String pageSourceCode = "";
	//����ͷ��Ϣ
	private Header[] reponseHeaders = null;
	//���ӳ�ʱʱ��
	private static int connectTimeOut = 60000;
	//���Ӷ�ȡʱ��
	private static int readTimeOut = 60000;
	//Ĭ�������ʴ���
	private static int maxConnectTimes = 5;
	//��ҳĬ�ϱ��뷽ʽ
	private static String charsetName = "ISO-8859-1";
	//��HttpClientί�и�MultiThreadedHttpConnectionManager��֧�ֶ��߳�
	private static MultiThreadedHttpConnectionManager httpConnectManager = new MultiThreadedHttpConnectionManager();
	private static HttpClient httpClient = new HttpClient(httpConnectManager);

	static {
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectTimeOut);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(readTimeOut);
		//��������ı����ʽ
		httpClient.getParams().setContentCharset("UTF-8");
	}
	/**
	 * @param urlStr
	 * @param params
	 * @param charsetName 
	 * @Description: GET��ʽ����ҳ��
	 */
	public boolean readPageByGet(String urlStr, HashMap<String, String> params, String charsetName) {
		GetMethod method = createGetMethod(urlStr, params);
		return  readPage(method, charsetName, urlStr);
	}
	
	/**
	 * @param urlStr
	 * @param params
	 * @param charsetName 
	 * @Description: POST��ʽ����ҳ��
	 */
	public boolean readPageByPost(String urlStr, HashMap<String, String> params, String charsetName) {
		PostMethod method = createPostMethod(urlStr, params);
		return  readPage(method, charsetName, urlStr);
	}
	
	/**
	 * @param method
	 * @param defaultCharset
	 * @param urlStr
	 * @Description: ִ��HttpMethod����ȡ���������ص�ͷ��Ϣ����ҳԴ����
	 */
	private boolean readPage(HttpMethod method, String defaultCharset, String urlStr) {
		int n = maxConnectTimes;
		while (n > 0) {
			try {
				//�жϷ���״̬�Ƿ���200
				if (httpClient.executeMethod(method) != HttpStatus.SC_OK) {
					log.info("can't connect " + urlStr + (maxConnectTimes - n + 1));
					n--;
				} else {
					//��ȡͷ��Ϣ
					reponseHeaders = method.getRequestHeaders();
					//��ȡ�������������
					InputStream inputStream = method.getResponseBodyAsStream();
					BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream, charsetName));
					StringBuffer stringBuffer = new StringBuffer();
					String lineString = "";
					while ((lineString = bufferReader.readLine()) != null) {
						stringBuffer.append(lineString);
						stringBuffer.append("\n");
					}
					pageSourceCode = stringBuffer.toString();
					//������ı��뷽ʽ
					InputStream in = new ByteArrayInputStream(pageSourceCode.getBytes(charsetName));
					String charset = CharsetUtil.getStreamCharset(in, defaultCharset);
					//������뷽ʽ��ͬ�������ת�����
					if (!charsetName.toLowerCase().equals(charset.toLowerCase())) {
						pageSourceCode = new String(pageSourceCode.getBytes(charsetName), charset);
					}
					return true;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error(urlStr + "can`t connect " + (maxConnectTimes - n + 1));
				n--;
			}
		}
		return false;
	}
	
	/**
	 * @param urlStr
	 * @param params ����ͷ��Ϣ
	 * @Description: ����GET����
	 */
	@SuppressWarnings("rawtypes")
	private GetMethod createGetMethod(String urlStr, HashMap<String, String> params){
		GetMethod method = new GetMethod(urlStr);
		if (params == null) {
			return method;
		}
		Iterator<Entry<String, String>> itor = params.entrySet().iterator();
		while (itor.hasNext()) {
			Entry entry = (Entry) itor.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			method.setRequestHeader(key, val);
		}
		return method;
	}

	/**
	 * @param urlStr
	 * @param params ����ͷ��Ϣ
	 * @Description: ����POST����
	 */
	@SuppressWarnings("rawtypes")
	private PostMethod createPostMethod(String urlStr, HashMap<String, String> params) {
		PostMethod method = new PostMethod(urlStr);
		if (params == null) {
			return method;
		}
		Iterator<Entry<String, String>> itor = params.entrySet().iterator();
		while (itor.hasNext()) {
			Entry entry = (Entry) itor.next();
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			method.setRequestHeader(key, val);
		}
		return method;
	}
	
	/**
	 * @return String
	 * @Description: ��ȡ��ҳԴ����
	 */
	public String getPageSourceCode(){
		return pageSourceCode;
	}
	
	/**
	 * @return Header[]
	 * @Description: ��ȡ��ҳ����ͷ��Ϣ
	 */
	public Header[] getHeader(){
		return reponseHeaders;
	}
	
	/**
	 * @param timeout
	 * @Description: �������ӳ�ʱʱ��
	 */
	public void setConnectTimeOut(int timeOut){
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeOut);
		Crawl.connectTimeOut = timeOut;
	}
	
	/**
	 * @param timeout 
	 * @Description: ���ö�ȡ��ʱʱ��
	 */
	public void setReadTimeOut(int timeOut){
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeOut);
		Crawl.readTimeOut = timeOut;
	}
	
	/**
	 * @param maxConnectTimes
	 * @Description: ���������ʴ���������ʧ�ܵ������ʹ��
	 */
	public static void setMaxConnectTimes(int maxConnectTimes) {
		Crawl.maxConnectTimes = maxConnectTimes;
	}

	/**
	 * @param connectTimeout
	 * @param readTimeout
	 * @Description: �������ӳ�ʱʱ��Ͷ�ȡ��ʱʱ��
	 */
	public void setTimeout(int connectTimeout, int readTimeout){
		setConnectTimeOut(connectTimeout);
		setReadTimeOut(readTimeout);
	}

	/**
	 * @param charsetName
	 * @Description: ����Ĭ�ϱ��뷽ʽ
	 */
	public static void setCharsetName(String charsetName) {
		Crawl.charsetName = charsetName;
	}

}

//package netrobot.crawl;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map.Entry;
//
//import netrobot.utils.CharsetUtil;
//
//import org.apache.commons.httpclient.Header;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.log4j.Logger;
//
///**
// *  �ɼ�����(import:commons-httpclient-3.0.jar,commons-codec-1.5.jar,antlr-2.7.4.jar,log4j-1.2.13.jar,commons-logging.jar)
// * @author qingdujun
// * 
// */
//public abstract class Crawl {
//
//	//��ӡ��־��Ϣ
//	private static Logger logger = Logger.getLogger(Crawl.class);
//	//��ҳԴ����洢
//	private String pageSourceCode = "";
//	//ͷ��Ϣ
//	private Header[] responseHeaders = null;
//	private static int connectTimeOut = 2000;
//	private static int readTimeOut = 2000;
//	private static int maxConnectTimes = 5;
//	private static String charsetName = "ISO-8859-1";
//
//	private static MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
//	private static HttpClient httpClient = new HttpClient(httpConnectionManager);
//
//	static{
//		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectTimeOut);
//		httpClient.getHttpConnectionManager().getParams().setSoTimeout(readTimeOut);
//		httpClient.getParams().setContentCharset("UTF-8");
//	}
//
//	/**
//	 * GET��ʽ��ȡ��ҳ
//	 * @param url
//	 * @param params
//	 * @param charsetName
//	 * @return
//	 */
//	public boolean readPageByGet(String url,HashMap<String, String> params,String charsetName) {
//		GetMethod method = createGetMethod(url, params);
//		return readPage(method, charsetName, url);
//	}
//	public boolean readPageByGet(String url,HashMap<String, String> params,String charsetName,boolean detect) {
//		GetMethod method = createGetMethod(url, params);
//		return readPage(method, charsetName, url, detect);
//	}
//	/**
//	 * POST��ʽ��ȡ��ҳ
//	 * @param url
//	 * @param params
//	 * @param charsetName
//	 * @return
//	 */
//	public boolean readPageByPost(String url,HashMap<String, String> params,String charsetName) {
//		PostMethod method = createPostMethod(url, params);
//		return readPage(method, charsetName, url);
//	}
//	public boolean readPageByPost(String url,HashMap<String, String> params,String charsetName,boolean detect) {
//		PostMethod method = createPostMethod(url, params);
//		return readPage(method, charsetName, url, detect);
//	}
//
//	/**
//	 * readPage̽����뷽ʽ
//	 * @param method
//	 * @param defaultCharset
//	 * @param url
//	 * @param detect trueΪ��̽��
//	 * @return
//	 */
//	private boolean readPage(HttpMethod method,String defaultCharset,String url,boolean detect){
//		readPage(method, defaultCharset, url);
//		try {
//			if (detect) {
//				InputStream ins = new ByteArrayInputStream(pageSourceCode.getBytes(charsetName));
//				//̽����뷽ʽ
//				String charset = CharsetUtil.getStreamCharset(ins, defaultCharset);
//				System.out.println("detectCharset:"+charset);
//				if (!charsetName.toLowerCase().equals(charset.toLowerCase())) {
//					//ת��
//					pageSourceCode = new String(pageSourceCode.getBytes(charsetName),charset);
//				}
//			}
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	private boolean readPage(HttpMethod method,String defaultCharset,String url) {
//		int n = maxConnectTimes;
//		while (n > 0) {
////			System.out.println("while n = "+n);
//			try {
////				System.out.println("try n = "+n);
//				if (HttpStatus.SC_OK != httpClient.executeMethod(method)) {
//					System.out.println("if n = "+n);
//					logger.info(" can't connect "+url+(maxConnectTimes-n+1)+" times ");
//					--n;
//				}else {
////					System.out.println("else n = "+n);
//					responseHeaders = method.getRequestHeaders();
//					InputStream inputStream = method.getResponseBodyAsStream();
//					//�˴���ָ��charsetName�������������
//					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,charsetName));
//					StringBuffer stringBuffer = new StringBuffer();
//					String lineInfo = "";
//					while (null != (lineInfo = bufferedReader.readLine())){
//						stringBuffer.append(lineInfo);
//						stringBuffer.append("\n");
//					}
////					System.out.println("defaultCharset:"+defaultCharset);
//					pageSourceCode = new String(stringBuffer.toString().getBytes(charsetName),defaultCharset);
//					return true;
//				}
//			} catch (Exception e) {
////				System.out.println("catch n = "+n);
//				logger.error(url+" can't connect "+(maxConnectTimes-n+1)+" times ");
//				--n;
//			}
//		}
//		return false;
//	}
//
//	private GetMethod createGetMethod(String url,HashMap<String, String> params) {
//		GetMethod method = new GetMethod(url);
//		if (null == params) {
//			return method;
//		}
//		//EntryΪһʵ��
//		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
//		while (iterator.hasNext()) {
//			Entry<String, String> entry= iterator.next();
//			String key = (String)entry.getKey();
//			String value = (String)entry.getValue();
//			method.setRequestHeader(key,value);
//		}
//		return method;
//	}
//
//	private PostMethod createPostMethod(String url,HashMap<String, String> params) {
//		PostMethod method = new PostMethod(url);
//		if (null == params) {
//			return method;
//		}
//		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
//		while (iterator.hasNext()) {
//			Entry<String, String> entry= iterator.next();
//			String key = (String)entry.getKey();
//			String value = (String)entry.getValue();
//			method.setRequestHeader(key,value);
//		}
//		return method;
//	}
//
//	public String getPageSourceCode(){
//		return pageSourceCode;
//	}
//
//	public Header[] getHeader(){
//		return responseHeaders;
//	}
//
//	public void setConnectTimeOut(int timeOut){
//		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeOut);
//		Crawl.connectTimeOut = timeOut;
//	}
//
//	public void setReadTimeOut(int timeOut){
//		httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeOut);
//		Crawl.readTimeOut = timeOut;
//	}
//
//	public static void setMaxConnectTimes(int maxConnectTimes) {
//		Crawl.maxConnectTimes = maxConnectTimes;
//	}
//
//	public void setTimeout(int connectTimeout, int readTimeout){
//		setConnectTimeOut(connectTimeout);
//		setReadTimeOut(readTimeout);
//	}
//
//	public static void setCharsetName(String charsetName) {
//		Crawl.charsetName = charsetName;
//	}
//}
