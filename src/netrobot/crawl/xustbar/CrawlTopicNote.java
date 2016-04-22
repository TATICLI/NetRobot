package netrobot.crawl.xustbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import netrobot.crawl.Crawl;
import netrobot.crawl.xustbar.db.XUSTbarDb;
import netrobot.crawl.xustbar.model.TopicNote;
import netrobot.utils.RegexUtil;

/**
 * ��ȡ������ҳ��������url
 * @author qingdujun
 *
 */
public class CrawlTopicNote extends Crawl{

	private String bar_url;
	private static HashMap<String, String> params;
	/**
	 * 1 replycount
	 * 2 -
	 * 3 url
	 * 4 title
	 */
	private static final String NOTE_RSUT = "title=\"�ظ�\">(\\d+)</div>(.*?)<a href=\"(.*?)\" title=\"(.*?)\"";
	//�ö�������
	private static final String TOP_NOTE_COUNT = "(alt=\"�ö�\")";
	private static final String  LAST_REPLY_TIME = "title=\"���ظ�ʱ��\">            (.*?)</span>";
	//������ҳ��
	private static final String TOTAL_PAGE = "�ظ�������<span class=\"red\">(\\d+)</span>ҳ</li>";
	
	private List<String> note_urls;
	private List<String> topic_reply_counts;
	private List<String> note_titles;
	private List<String> last_reply_times;
	
	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}

	/**
	 * ָ����ȡ��url��Ĭ�ϱ���
	 * @param bar_url
	 */
	public CrawlTopicNote(String bar_url) {
		readPageByGet(bar_url, params, "UTF-8");
		this.bar_url = bar_url;
	}

	/**
	 * ��ȡҳ������������url
	 * @return
	 */
	private List<String> getNoteUrl(){
		return RegexUtil.getArrayList(getPageSourceCode(), NOTE_RSUT, bar_url, 3);
	}
	/**
	 * ��ȡ�������ظ���
	 * @return
	 */
	private List<String> getTopicReplyCount(){
		return RegexUtil.getList(getPageSourceCode(), NOTE_RSUT, 1);
	}
	/**
	 * ��ȡ���ӱ���
	 * @return
	 */
	private List<String> getNoteTitle(){
		return RegexUtil.getList(getPageSourceCode(), NOTE_RSUT, 4);
	}

	/**
	 * ��ȡ���������ظ�ʱ��
	 * @return
	 */
	private List<String> getLastReplyTime(){
		//����ȱʡʱ��
		List<String> tmpReplyTime = RegexUtil.getList(getPageSourceCode(), LAST_REPLY_TIME, 1);
		for (int i = 0; i < getTopNoteCount(); i++) {
			tmpReplyTime.add(0, "0");
		}
		return tmpReplyTime;
	}
	/**
	 * ��ȡ�ö�����������ʱ��,���ֶ�����
	 * @return
	 */
	private int getTopNoteCount() {
		return RegexUtil.getList(getPageSourceCode(), TOP_NOTE_COUNT, 1).size();
	}
	/**
	 * ���˱�����emoji����
	 * @param emoji
	 * @return
	 */
	private List<String> getNoteTitle(boolean emoji) {
		List<String> noteTitles = getNoteTitle();
		if (emoji) {
			List<String> filterEmoji = new ArrayList<String>();
			for (String noteTitle : noteTitles) {
				filterEmoji.add(RegexUtil.filterEmoji(noteTitle));
			}
			return filterEmoji;
		}
		return noteTitles;
	}

	/**
	 * �ų�����������ַ
	 * @param exceptOther
	 * @return
	 */
	private List<String> getNoteUrl(boolean exceptOther) {
		List<String> urls = getNoteUrl();
		if (exceptOther) {
			List<String> exceptUrls = new ArrayList<String>();
			for (String url : urls) {
				if (url.indexOf("tieba") > 0) {
					exceptUrls.add(url);
				}
			}
			return exceptUrls;
		}
		return urls;
	}
	/**
	 * ʱ��ƴ��
	 * @return
	 */
	private String getLinkTime(String time) {
		long curTime = System.currentTimeMillis();

		if (time.indexOf(":") > 0) {
			//			yyyy-MM-dd HH:mm
			//			eg.20:22
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd ");
			Date date = new Date(curTime);  
			String tm = sdf.format(date)+time;  
			return tm;
		}else if(time.indexOf("-") > 0) {
			//			eg.4-20
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-");
			SimpleDateFormat sdf2 = new SimpleDateFormat(" HH:mm");
			Date date1 = new Date(curTime);  
			Date date2 = new Date(curTime); 
			String tm = sdf1.format(date1)+time+sdf2.format(date2);  
			//			System.out.println(tm);
			return tm;
		}
		return time;
	}
	/**
	 * ʱ�䴦��
	 * @param isLink
	 * @return
	 */
	private List<String> getLastReplyTime(boolean isLink) {
		List<String> lastReplyTimes = getLastReplyTime();

		if (isLink) {
			List<String> linkTimes = new ArrayList<String>();
			for (String linkTime : lastReplyTimes) {
				linkTime = getLinkTime(linkTime);
				linkTimes.add(linkTime);
			}
			return linkTimes;
		}
		return lastReplyTimes;
	}
	
	/**
	 * ��ȡ������������
	 * @return
	 */
	private int getTopicNoteMinSize() {
	
		note_urls = getNoteUrl(true);
		topic_reply_counts = getTopicReplyCount();
		note_titles = getNoteTitle(true);
		last_reply_times = getLastReplyTime(true);
		
		int min = note_urls.size();
		if (min > topic_reply_counts.size()) {
			min = topic_reply_counts.size();
		}
		
		if (min > note_titles.size()) {
			min = note_titles.size();
		}
		
		if (min > last_reply_times.size()) {
			min = last_reply_times.size();
		}
		return min;
	}
	
	/**
	 * ��װ����������������
	 * @return
	 */
	public List<TopicNote> getTopicNotes() {
		List<TopicNote> topicNotes = new ArrayList<TopicNote>();

		int minSize = getTopicNoteMinSize();
		for (int i = 0; i < minSize; i++) {
			TopicNote topicNote = new TopicNote();
			
			topicNote.setNote_url(note_urls.get(i));
			topicNote.setTopic_reply_count(Integer.parseInt(topic_reply_counts.get(i)));
			topicNote.setNote_title(note_titles.get(i));
			topicNote.setLast_reply_time(last_reply_times.get(i));
			topicNotes.add(topicNote);
		}

		return topicNotes;
	}
	
	public static void main(String[] args) {
		//������ΪXUST��ҳ����
		CrawlTopicNote ctn = new CrawlTopicNote("http://tieba.baidu.com/f?ie=utf-8&kw=%E8%A5%BF%E5%AE%89%E7%A7%91%E6%8A%80%E5%A4%A7%E5%AD%A6");

//		List<String> cn = ctn.getTopicReplyCount();
//		List<String> url= ctn.getNoteUrl(true);
//		List<String> tl= ctn.getNoteTitle();
//		List<String> tm= ctn.getLastReplyTime(true);
//		
//		for (int i = 0; i < url.size(); i++) {
//			System.out.println((i+1)+" "+cn.get(i)+" "+url.get(i)+" "+tl.get(i)+" "+tm.get(i));
//		}
		
		XUSTbarDb db = new XUSTbarDb();
		db.saveTopicNoteCrawlInfo(ctn.getTopicNotes(), false);

	}
}
