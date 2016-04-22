package netrobot.crawl.xustbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import netrobot.crawl.Crawl;
import netrobot.utils.JsonUtil;
import netrobot.utils.RegexUtil;

public class CrawlNoteDetail extends Crawl{

	private String note_url;
	
	//һ���ظ�ID
	private static final String ONE_REPLY_ID = "<div id=\"post_content_(\\d*?)\"";
	//һ���ظ�����
	private static final String ONE_REPLY_CONTEXT = "class=\"d_post_content j_d_post_content  clearfix\">            (.*?)</div>";
	//һ���ظ�ʱ��
	private static final String ONE_REPLY_TIME = "(\\d+-\\d+-\\d+ \\d+:\\d+)";
	
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
	
	public static void main(String[] args) {

		//XUSTĳһ����url
		CrawlNoteDetail noteDetail = new CrawlNoteDetail("http://tieba.baidu.com/p/4496267469");

		List<String> pid = noteDetail.getOneReplyTime();
		
		for (int i = 0; i < pid.size(); i++) {
			System.out.println(i + " "+pid.get(i));
		}

	}
}
