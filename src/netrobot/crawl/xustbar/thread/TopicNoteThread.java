package netrobot.crawl.xustbar.thread;

import java.util.List;
import netrobot.crawl.xustbar.CrawlTopicNote;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.Setting;
import netrobot.crawl.xustbar.model.TopicNote;

public class TopicNoteThread implements Runnable{

	//ҳ����
	private static int pn;
	
	private static XUSTbarDb db;
	
	private static String bar_url;
	private static String bar_name;
	private static int crawl_frequency;
	private static int bar_crawl_note_count;
	//�̳߳�
//	private static ExecutorService newFixedThreadPool;
	private static boolean flag = false;

	/**
	 * ������ʼ��
	 */
	private static void init() {
		db = new XUSTbarDb();
		//����
		flag = true;
		//��ʼ��ȡҳ��
		pn = 3894;
		Setting setting = getBarSettingInfo();
		if (null == setting) {
			System.out.println("bar not exist!");
			return;
		}
		bar_url = getDealBarUrl(setting.getBar_url());
		bar_name = setting.getBar_name();
		System.out.println("we will crawl "+bar_name);
		crawl_frequency = setting.getCrawl_frequency();
		//������ȡƵ��
		bar_crawl_note_count = setting.getBar_crawl_note_count();
	}
	/**
	 * ����url����
	 * @param src
	 * @return
	 */
	private static String getDealBarUrl(String src){
		//����url���ɼ�����
		String barname;
		if (src.indexOf("&") > 0) {
			barname = src.substring(src.indexOf("=")+1,src.indexOf("&"));
		}else {
			barname = src.substring(src.indexOf("=")+1,src.length());
		}
		
		String barurl = "http://tieba.baidu.com/f?kw="+barname+"&ie=utf-8&tp=0&pn=";
		System.out.println("link url = "+barurl);
		return barurl;
	}
	/**
	 * ��ȡ���ݿ���Setting��Ϣ
	 * @return
	 */
	private static Setting getBarSettingInfo(){
		List<Setting> settings = db.getSettingTableInfo();
		if (null == settings) {
			System.out.println("Setting info get error!");
			return null;
		}
		for (Setting setting : settings) {
			if (setting.getBar_name().trim().equals("�����Ƽ���ѧ")) {
				System.out.println("Exist XUST.");
				return setting;
			}
		}
		return null;
	}

	/**
	 * ��ȡ����
	 */
	private void crawlAllTopicNote(){

		System.out.println("crawling XUST page "+pn);
		if ( (db.getiStop() >= 150) || ( pn >= bar_crawl_note_count / 50) ) {
			//�ر��̣߳���ȡ����
			System.out.println("crawl finish.��������"+Thread.currentThread().getName());
			flag = false;
		}
		CrawlTopicNote ctn = new CrawlTopicNote(bar_url+(pn++)*50);
		
		List<TopicNote> tns = ctn.getTopicNotes();
		db.saveTopicNoteCrawlInfo(tns, false);
	}
	/**
	 * ����N�߳�
	 * @param N
	 */
//	public static void openTopicNoteThread(int N) {
//		init();
//		//�����̳߳�
//		newFixedThreadPool = Executors.newFixedThreadPool(N);  
//		for (int i = 0; i < N; i++) {  
//			newFixedThreadPool.execute(new TopicNoteThread());
//		}  
//		newFixedThreadPool.shutdown();
//	}
	public static void openTopicNoteThread(){
		init();
		TopicNoteThread tnt = new TopicNoteThread();
		Thread t1 = new Thread(tnt);
		t1.start();
	}
	@Override
	public void run() {
		while (flag) {
			crawlAllTopicNote();
			try { 
				Thread.sleep((long)(20+Math.random()*crawl_frequency));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
