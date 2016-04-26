package netrobot.crawl.xustbar.thread;

import java.util.List;

import netrobot.crawl.xustbar.CrawlNoteDetail;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.TopicNote;

public class NoteDetailThread implements Runnable{

	private static XUSTbarDb db;
	
	//����ȡurl����
	private int state_count;
	
	//ÿ�δ����ݿ���ȡ������
	private final int count = 1000;

	//�̳߳�
//	private static ExecutorService newFixedThreadPool;
	private static boolean flag = false;
	private static int row = 0;

	/**
	 * ������ʼ��
	 */
	private static void init() {
		db = new XUSTbarDb();
		//����
		flag = true;
		
	}
	
	/**
	 * ��ҳ��ѯTopicNote���������嵥
	 * @return
	 */
	private List<TopicNote> getTopicNotes(int row, int count){
		List<TopicNote> topicNotes = db.getTopicNoteCrawlList(row,count);
//		System.out.println(topicNotes.size());
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
	 * ץȡ���
	 * @return
	 */
	private boolean isCrawlFinish(){
		if (state_count <= db.getTopNoteCount()) {
			try {
				Thread.sleep(2000);
				state_count = db.getTopicNoteCount();
				if (state_count <= db.getTopNoteCount()) {
					//ֹͣ�߳�
					flag = false;
					System.out.println("NoteDetailThread will stop.");
					return true;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	/**
	 * ��ȡ����
	 */
	private void crawlAllNoteDetail(){
		int realCount = 0;
		//��ȡ������list.size()
		state_count = db.getTopicNoteCount();
		System.out.println("now,we need update note count = "+state_count);
		if (isCrawlFinish()) {
			return;
		}
		//������
		for( ; row < state_count; ) {
			//���ݿ��ҳ��ѯ
			List<TopicNote> topicNotes = getTopicNotes(row, count);
			//ʵ����ȡ����
			realCount = topicNotes.size();
			row += realCount;
			System.out.println("we real get note count total "+realCount+" from row "+row+" begin.");
			for (int j = 0; j < topicNotes.size(); j++) {
				//�������棬���
				TopicNote topicNote = getTopicNote(topicNotes, j);
				CrawlNoteDetail cnd = new CrawlNoteDetail(topicNote.getNote_url());
				//���������ظ�ҳ��(�������)
				String tmp = cnd.getReplyPage();
				//���ӱ�ɾ�����޷�����
				if (null == tmp || tmp.equals("")) {
					db.updateStateValue(topicNote.getNote_url(), 0);
					System.out.println("404   "+ topicNote.getNote_url());
					continue;
				}
				int reply_page = Integer.parseInt(tmp);
				//������ȡ��Ȳ�����1000ҳ
				if (reply_page > 1000) {
					reply_page = 1000;
				}
				for (int k = 1; k <= reply_page; k++) {
					cnd = new CrawlNoteDetail(topicNote.getNote_url()+"?pn="+k);
					//���ݿ�洢������������ʣ�
					db.saveNoteDetailInfo(cnd.getNoteDetails(), false);
					try {
						//���������,��˯ʱ��
						Thread.sleep((long)(50+Math.random()*180));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				//���ӱ�ɾ�����޷�����
				if (null == cnd.getReply_times()) {
					continue;
				}
				//����TopicNote�����ݸ��²�����reply_time,state��
				int size = cnd.getReply_times().size();
				//�ö���������
				if (0 != size && !topicNote.getLast_reply_time().equals("0")) {
					db.updateTopicNoteTime(topicNote, cnd.getReply_times().get(size-1), false);
				}
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
	public static void openNoteDetailThread() {
		init();
		NoteDetailThread tnt = new NoteDetailThread();
		Thread t1 = new Thread(tnt);
		t1.start();
	}
	@Override
	public void run() {
		while (flag) {
			crawlAllNoteDetail();
		}
	}
}
