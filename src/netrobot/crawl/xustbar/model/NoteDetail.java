package netrobot.crawl.xustbar.model;


/**
 * ���������
 * @author qingdjun
 *
 */
public class NoteDetail {

	//������id == 1¥id
	private String tid;  //�������ظ�
	//�ظ�¥id
	private String rid;  //�������ظ�
	//��id,(0,nullΪ������) 
	private String pid;  //�ǿ�
	//����������
	private String title;
	//����(¥��)����
	private String context;
	//����(¥��)�ظ���
	private int reply_count;
	//������ʱ��
	private String last_crawl;
	
	
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public int getReply_count() {
		return reply_count;
	}
	public void setReply_count(int reply_count) {
		this.reply_count = reply_count;
	}
	public String getLast_crawl() {
		return last_crawl;
	}
	public void setLast_crawl(String last_crawl) {
		this.last_crawl = last_crawl;
	}
	
	
}
