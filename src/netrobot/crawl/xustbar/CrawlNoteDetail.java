package netrobot.crawl.xustbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import netrobot.crawl.Crawl;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.NoteDetail;
import netrobot.crawl.xustbar.model.TopicNote;
import netrobot.utils.JsonUtil;
import netrobot.utils.RegexUtil;

public class CrawlNoteDetail extends Crawl{	
	//������url
	private String note_url;  //�������ظ�
	//�ظ�¥id
	private List<String> reply_floor_ids;  //�������ظ�
	//��id(�һ���ظ�id=0)
	private List<String> reply_parent_ids;  //�ǿ�
	//һ���ظ�(��¥��¥)����
	private List<String> reply_contexts;
	//һ���ظ�(��¥��¥)�ظ���
//	private int lzl_reply_count;
	//���ظ�ʱ��
	private List<String> reply_times;

	//һ���ظ�ID
	private static final String ONE_REPLY_ID = "<div id=\"post_content_(\\d*?)\"";
	//һ���ظ�����
	private static final String ONE_REPLY_CONTEXT = "class=\"d_post_content j_d_post_content  clearfix\">            (.*?)</div>";
	//һ���ظ�ʱ��
	private static final String ONE_REPLY_TIME = "(\\d+-\\d+-\\d+ \\d+:\\d+)";
	
	private static final String REPLY_PAGE = "�ظ�������<span class=\"red\">(\\d+)</span>ҳ";
	
	private static HashMap<String, String> params;

	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}
	
	public CrawlNoteDetail(String note_url) {
		readPageByGet(note_url, params, "UTF-8");
		this.note_url = note_url;
	}
	
	public List<String> getReply_times() {
		return reply_times;
	}

	public void setReply_times(List<String> reply_times) {
		this.reply_times = reply_times;
	}

	/**
	 * ��ȡһ���ظ�¥����ID
	 * @return
	 */
	private List<String> getOneReplyID(){
		return RegexUtil.getList(getPageSourceCode(), ONE_REPLY_ID, 1);
	}
	/**
	 * ��ȡһ���ظ�����
	 * @return
	 */
	private List<String> getOneReplyContext(){
		return RegexUtil.getList(getPageSourceCode(), ONE_REPLY_CONTEXT, 1);
	}
	/**
	 * ��ȡһ���ظ�ʱ��
	 * @return
	 */
	private List<String> getOneReplyTime(){
		return RegexUtil.getList(getPageSourceCode(), ONE_REPLY_TIME, 1);
	}
	/**
	 * ��ȡ���ӻظ�ҳ��
	 * @return
	 */
	public String getReplyPage(){
		return RegexUtil.getFirstString(getPageSourceCode(), REPLY_PAGE, 1);
	}
	/**
	 * ��ȡһ���ظ����ݣ�����
	 * @param exceptLabel
	 * @return
	 */
	private List<String> getOneReplyContext(boolean exceptLabel){
		List<String> oneReplycList = getOneReplyContext();
		if (exceptLabel) {
			List<String> exceptOneReply = new ArrayList<String>();
			for (String one : oneReplycList) {
				//����ȥ��������ҳhtml��ǩ,emoji����
				exceptOneReply.add(RegexUtil.filterEmoji(one.replaceAll("<[^>]*>", "")));
			}
			return exceptOneReply;
		}
		return oneReplycList;
	}
	
	/**
	 * ��ȡ�������������
	 * @return
	 */
	private int getNoteDetailMinSize() {
		
		reply_floor_ids = getOneReplyID();
		reply_contexts = getOneReplyContext(true);
		reply_times = getOneReplyTime();
		
		int min = reply_floor_ids.size();
		if (min > reply_contexts.size()) {
			min = reply_contexts.size();
		}
		
		if (min > reply_times.size()) {
			min = reply_times.size();
		}
		return min;
	}
	
	/**
	 * ��װ������ϸ����
	 * @return
	 */
	public List<NoteDetail> getNoteDetails() {
		List<NoteDetail> noteDetails = new ArrayList<NoteDetail>();

		int minSize = getNoteDetailMinSize();
		for (int i = 0; i < minSize; i++) {
			NoteDetail noteDetail = new NoteDetail();
			
			noteDetail.setNote_url(note_url);
			noteDetail.setReply_floor_id(reply_floor_ids.get(i));
			noteDetail.setReply_context(reply_contexts.get(i));
			noteDetail.setReply_parent_id("0");
			noteDetail.setReply_time(reply_times.get(i));
			noteDetails.add(noteDetail);
		}

		return noteDetails;
	}
	
	public static void main(String[] args) {

		//XUSTĳһ����url
		CrawlNoteDetail cnd = new CrawlNoteDetail("http://tieba.baidu.com/p/4481777065");

//		List<String> pid = noteDetail.getOneReplyTime();
//		
//		for (int i = 0; i < pid.size(); i++) {
//			System.out.println(i + " "+pid.get(i));
//		}
		
		
		System.out.println(cnd.getReplyPage());
//		XUSTbarDb db = new XUSTbarDb();
//		db.saveNoteDetailInfo(cnd.getNoteDetails(), false);
	}
}
