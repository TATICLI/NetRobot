package netrobot.crawl.bar.thread;

import java.util.List;
import netrobot.crawl.bar.CrawlTopicNote;
import netrobot.crawl.bar.db.CrawlXUSTbarDb;
import netrobot.crawl.bar.model.Bar;
import netrobot.crawl.bar.model.TopicNote;

public class TopicNoteThread implements Runnable{

	private static CrawlXUSTbarDb db;

	private static String bar_url;
	private static String bar_name;
	private static int crawl_frequency;
	private static int bar_crawl_note_count;
	//�̳߳�
	//	private static ExecutorService newFixedThreadPool;

	/**
	 * ������ʼ��
	 */
	private static boolean init() {
		db = new CrawlXUSTbarDb();
		Bar bar = getBarInfo();
		if (null == bar) {
			System.out.println("bar not exist!");
			return false;
		}
		bar_url = getDealBarUrl(bar.getUrl());
		bar_name = bar.getName();
		System.out.println("we will crawl "+bar_name);
		//������ȡƵ��
		crawl_frequency = bar.getFrequency();
		//��ȡ����
		bar_crawl_note_count = bar.getCount();
		return true;
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
	 * ��ȡ���ݿ���Bar��Ϣ
	 * @return
	 */
	private static Bar getBarInfo(){
		List<Bar> bars = db.getBarTableInfo();
		if (null == bars) {
			System.out.println("bar info get error!");
			return null;
		}
		for (Bar bar : bars) {
			if (bar.getName().trim().equals("�����Ƽ���ѧ")) {
				System.out.println("Exist XUST.");
				return bar;
			}
		}
		return null;
	}

	/**
	 * ��ȡ����
	 */
	private void crawlAllTopicNote(){

		System.out.println("bar_crawl_note_count = "+bar_crawl_note_count);
		int pn = 0;
		int page = bar_crawl_note_count/50;
		while (pn <= page){
			System.out.println("crawling XUST page "+pn);
			if ( db.getiStop() >= 50 * 10) {
				// 10ҳδ��ȡ���������Զ���������ȡ����
				System.out.println("Continues 30 not need to update!crawl finish.��������"+Thread.currentThread().getName());
				break;
			}
			CrawlTopicNote ctn = new CrawlTopicNote(bar_url+(pn++)*50);

			List<TopicNote> tns = ctn.getTopicNotes();
			//��⣬��MD5����
			db.saveTopicNoteCrawlInfo(tns, 0,false);

			try { 
				Thread.sleep((long)(100+Math.random()*crawl_frequency));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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

	@Override
	public void run() {
		crawlAllTopicNote();
	}

	public static void openTopicNoteThread(){
		if (init()){
			TopicNoteThread tnt = new TopicNoteThread();
			Thread t = new Thread(tnt);
			t.start();
		}
	}
}
