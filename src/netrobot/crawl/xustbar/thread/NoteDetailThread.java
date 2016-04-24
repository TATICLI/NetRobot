package netrobot.crawl.xustbar.thread;

import java.util.List;

import netrobot.crawl.xustbar.CrawlNoteDetail;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.NoteDetail;
import netrobot.crawl.xustbar.model.TopicNote;
import netrobot.utils.JsonUtil;

public class NoteDetailThread extends ThreadPool{

	private XUSTbarDb db;
	
	//����ȡurl����
	private int state_count;
	
	//ÿ�δ����ݿ���ȡ������
	private final int count = 1000;
	
	public NoteDetailThread() {
		init();
	}
	/**
	 * ������ʼ��
	 */
	private void init() {
		db = new XUSTbarDb();
		//����
		setFlag(true);
		state_count = db.getTopicNoteCount();
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
	 * ��ȡ����
	 */
	private synchronized void crawlAllNoteDetail(){
		int realCount = 0;
		for (int i = 0; i < state_count; i+=realCount) {
			//���ݿ��ҳ��ѯ
			List<TopicNote> topicNotes = getTopicNotes(i, count);
			//ʵ����ȡ����
			realCount = topicNotes.size();
			System.out.println("state_count = "+state_count);
			for (int j = 0; j < topicNotes.size(); j++) {
				//�������棬���
				TopicNote topicNote = getTopicNote(topicNotes, j);
				CrawlNoteDetail cnd = new CrawlNoteDetail(topicNote.getNote_url());
				//���������ظ���Ŀ
				int reply_count = Integer.parseInt(cnd.getReplyPage());
				//������ȡ��Ȳ�����1000ҳ
				if (reply_count > 1000) {
					reply_count = 1000;
				}
				System.out.println("reply_count = "+reply_count);
				for (int k = 1; k <= reply_count; k++) {
					cnd = new CrawlNoteDetail(topicNote.getNote_url()+"?pn="+k);
//					System.out.println("url = "+topicNote.getNote_url()+"?pn="+k);
//					System.out.println(JsonUtil.parseJson(cnd.getNoteDetails()));
					db.saveNoteDetailInfo(cnd.getNoteDetails(), false);
				}
				//����TopicNote�����ݸ��²�����reply_time,state��
				int size = cnd.getReply_times().size();
				db.updateTopicNoteTime(topicNote, cnd.getReply_times().get(size-1), false);
			}
		}

	}
	

	@Override
	protected void executeMethod() {
		crawlAllNoteDetail();
	}

	public static void main(String[] args) {
		NoteDetailThread ndt = new NoteDetailThread();
		
		ndt.crawlAllNoteDetail();
//		XUSTbarDb db = new XUSTbarDb();
//		int count = db.getTopicNoteCount();
//		System.out.println(count);
//		ndt.getTopicNotes(10,40);
//		ndt.openNThreadCrawl(4);
	}
}
