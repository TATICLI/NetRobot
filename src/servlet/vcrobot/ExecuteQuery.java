package servlet.vcrobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ltp.cloud.analysis.TypeWord;
import ltp.cloud.db.LTPDb;
import netrobot.utils.TimeUtil;
import se.index.operation.bar.BarNRTSearch;
import se.index.utils.JsonUtil;
import servlet.vcrobot.db.VcrobotDb;

public class ExecuteQuery {

	/**
	 * ִ�в�ѯ
	 * @param email
	 * @param keywords
	 * @return
	 */
	public static String executeQuery(String email, String keywords){
		HashMap<String, String> hashMap = new BarNRTSearch().searchInContext(email, keywords);
		return (hashMap == null) ? null : JsonUtil.parseJson(hashMap);
	}
	/**
	 * �������ݣ�ʵʱ����
	 * @param keywords
	 * @return
	 */
	public static String analyseContain(String keywords, String email){
		
//		System.out.println(keywords);
		if(keywords == null){
			return null;
		}
		
		
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "10001");

		//��ѧ  ����ʽ��[Q@��Է���û��A@�Ҳ�����]
		if (isQA(keywords)) {
			List<String> QA = getQA(keywords);
			if (QA != null) {
				hashMap.put("text", VcrobotDb.saveQA(QA.get(0), QA.get(1)));
				return JsonUtil.parseJson(hashMap);
			}
		}
		
		if (keywords.contains("�ҵ���Ȥ")) {
			hashMap.put("text", LTPDb.getWords(email));
			return JsonUtil.parseJson(hashMap);
		}
		
		if (keywords.contains("����") || keywords.contains("ʱ��") || keywords.contains("����")) {
			hashMap.put("text", TimeUtil.getCurTime());
			return JsonUtil.parseJson(hashMap);
		}

		//���߳�---����������﷨����
		analysisInterest(email, keywords);

		return null;
	}
	/**
	 * �ж��Ƿ�ѵ�����
	 * @param keywords
	 * @return
	 */
	private static boolean isQA(String keywords){
		if (keywords == null) {
			return false;
		}
		if (keywords.startsWith("[Q@") && keywords.endsWith("]") && keywords.contains("A@")) {
			return true;
		}
		return false;
	}
	/**
	 * ���QA
	 * @param keywords
	 * @return
	 */
	private static List<String> getQA(String keywords){
		if (keywords == null) {
			return null;
		}
		List<String> QA = new ArrayList<String>();
		int posA = keywords.indexOf("A@");
		String Q = keywords.substring(3, posA).trim();
		String A = keywords.substring(posA+2,keywords.length()-1).trim();
//		System.out.println(Q+"----"+A);
		QA.add(Q);
		QA.add(A);
		return QA;
	}
	
	/**
	 * �����û���Ȥ
	 * @param email
	 * @param keywords
	 */
	private static void analysisInterest(final String email, final String keywords){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				LTPDb.saveKeywords(TypeWord.analysisWordType(keywords), email);
			}
		}).start();
	}
	public static void main(String[] args) {
//		analyseContain("[Q@���ƴ�У����˭��A@Ŀǰ������硣]");
//		System.out.println(analyseContain("[Q@���ƴ�ί�����˭��A@Ŀǰ�����°���]"));
	}
}
