package netrobot.crawl.xustbar.model;

public class TopicNote {
	//������url
	private String note_url;   //���ظ�
	//�ظ�����
	private int topic_reply_count;
	//���ӱ���
	private String note_title;
	//���ظ�ʱ��
	private String last_reply_time;
	
	public String getNote_url() {
		return note_url;
	}
	public void setNote_url(String note_url) {
		this.note_url = note_url;
	}
	public int getTopic_reply_count() {
		return topic_reply_count;
	}
	public void setTopic_reply_count(int topic_reply_count) {
		this.topic_reply_count = topic_reply_count;
	}
	public String getNote_title() {
		return note_title;
	}
	public void setNote_title(String note_title) {
		this.note_title = note_title;
	}
	public String getLast_reply_time() {
		return last_reply_time;
	}
	public void setLast_reply_time(String last_reply_time) {
		this.last_reply_time = last_reply_time;
	}

}
