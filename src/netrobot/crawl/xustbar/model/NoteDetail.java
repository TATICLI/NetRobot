package netrobot.crawl.xustbar.model;


/**
 * ���������
 * @author qingdjun
 *
 */
public class NoteDetail {

	//������url
	private String note_url;  //�������ظ�
	//�ظ�¥id
	private String reply_floor_id;  //�������ظ�
	//��id(�һ���ظ�id=0)
	private String reply_parent_id;  //�ǿ�
	//һ���ظ�(��¥��¥)����
	private String reply_context;
	//һ���ظ�(��¥��¥)�ظ���
//	private int lzl_reply_count;
	//���ظ�ʱ��
	private String reply_time;
	
	
	public String getNote_url() {
		return note_url;
	}
	public void setNote_url(String note_url) {
		this.note_url = note_url;
	}
	public String getReply_floor_id() {
		return reply_floor_id;
	}
	public void setReply_floor_id(String reply_floor_id) {
		this.reply_floor_id = reply_floor_id;
	}
	public String getReply_parent_id() {
		return reply_parent_id;
	}
	public void setReply_parent_id(String reply_parent_id) {
		this.reply_parent_id = reply_parent_id;
	}
	public String getReply_context() {
		return reply_context;
	}
	public void setReply_context(String reply_context) {
		this.reply_context = reply_context;
	}
//	public int getLzl_reply_count() {
//		return lzl_reply_count;
//	}
//	public void setLzl_reply_count(int lzl_reply_count) {
//		this.lzl_reply_count = lzl_reply_count;
//	}
	public String getReply_time() {
		return reply_time;
	}
	public void setReply_time(String reply_time) {
		this.reply_time = reply_time;
	}
	
}
