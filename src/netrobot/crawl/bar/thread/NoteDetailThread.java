package netrobot.crawl.bar.thread;

import java.util.List;
import netrobot.crawl.bar.CrawlNoteDetail;
import netrobot.crawl.bar.db.CrawlXUSTbarDb;
import netrobot.crawl.bar.model.TopicNote;

public class NoteDetailThread implements Runnable{

	private static CrawlXUSTbarDb db;

	private boolean again = true;
	//	private static ExecutorService newFixedThreadPool;

	/**
	 * ������ʼ��
	 */
	private static void init() {
		db = new CrawlXUSTbarDb();
	}

	/**
	 * ��ҳ��ѯTopicNote���������嵥
	 * @return
	 */
	private List<TopicNote> getTopicNotes(int row, int count){
		List<TopicNote> topicNotes = db.getTopicNoteCrawlList(row,count);
		return topicNotes;
	} 
	/**
	 * ��ȡ����һ�����ݽ�����ȡ
	 * @param topicNotes
	 * @param i
	 * @return
	 */
	private TopicNote getTopicNote(List<TopicNote> topicNotes, int i){
		if ((null == topicNotes) || (topicNotes.size() <= i)) {
			return null;
		}
		return topicNotes.get(i);
	}

	/**
	 * ��ȡ����
	 */
	private void crawlAllNoteDetail(){
		List<TopicNote> topicNotes = null;
		int row = 0, count = 1000;
		//��ȡ������list.size()
		int num = db.getTopicNoteCount();
		System.out.println("now,we need update note count = "+num);
		//�����߳�
		if (0 == num) {
			again = false;
		}
		//������
		while(row < num) {
			//���ݿ��ҳ��ѯ
			topicNotes = getTopicNotes(row, count);
			row += count;
			if (null == topicNotes || 0 == topicNotes.size()) {
				continue;
			}
			for (int j = 0; j < topicNotes.size(); j++) {
				//�������棬���
				TopicNote topicNote = getTopicNote(topicNotes, j);
				if (null == topicNote) {
					continue;
				}
				CrawlNoteDetail cnd = new CrawlNoteDetail(topicNote.getUrl());
				//���������ظ�ҳ��(�������)
				String tmp = cnd.getReplyPage();
				//������ʳɹ�
				//���ӱ�ɾ�����޷�����
				if (null == tmp || tmp.equals("")) {
					db.updateStateValue(topicNote.getUrl(), 0);
					System.out.println("404   "+ topicNote.getUrl());
					continue;
				}
				int reply_page = Integer.parseInt(tmp);
				//������ȡ��Ȳ�����1000ҳ
				if (reply_page > 1000) {
					reply_page = 1000;
				}
				for (int k = 1; k <= reply_page; k++) {
					cnd = new CrawlNoteDetail(topicNote.getUrl()+"?pn="+k);
					//���ݿ�洢����,��MD5��������ʣ�
					db.saveNoteDetailInfo(cnd.getNoteDetails(k), false);
					try {
						//���������,��˯ʱ��
						Thread.sleep((long)(200+Math.random()*60));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				db.updateStateValue(topicNote.getUrl(), 0);
			}
		}
	}


	//	public static void openNoteDetailThread(int N) {
	//		init();
	//		//�����̳߳�
	//		newFixedThreadPool = Executors.newFixedThreadPool(N);  
	//		for (int i = 0; i < N; i++) {  
	//			newFixedThreadPool.execute(new NoteDetailThread());
	//		}  
	//		newFixedThreadPool.shutdown();
	//	}

	@Override
	public void run() {
		while (again) {
			crawlAllNoteDetail();
			try {
				//��ͣһ����
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}

	public static void openNoteDetailThread() {
		init();
		NoteDetailThread tnt = new NoteDetailThread();
		Thread t = new Thread(tnt);
		t.start();
	}
}
