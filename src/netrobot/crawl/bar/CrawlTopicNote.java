package netrobot.crawl.bar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import netrobot.crawl.Crawl;
import netrobot.crawl.bar.model.TopicNote;
import netrobot.utils.RegexUtil;
import netrobot.utils.TimeUtil;

/**
 * ��ȡ������ҳ��������url
 * @author qingdujun
 *
 */
public class CrawlTopicNote extends Crawl{

	private String bar_url;
	private static HashMap<String, String> params;

	private static final String RPLEY_COUNT = "title=\"�ظ�\">(\\d+)</span>";	
	//url 2
	//title 3
	private static final String URL_TITLE = "<div class=\"threadlist_title pull_left j_th_tit(.*?)<a href=\"(.*?)\" title=\"(.*?)\" target=\"_blank\" class=\"j_th_tit \">";
	
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
		return RegexUtil.getArrayList(getPageSourceCode(), URL_TITLE, bar_url, 2);
	}
	/**
	 * ��ȡ�������ظ���
	 * @return
	 */
	private List<String> getCount(){
		return RegexUtil.getList(getPageSourceCode(), RPLEY_COUNT, 1);
	}
	/**
	 * ��ȡ���ӱ���
	 * @return
	 */
	private List<String> getNoteContent(){
		return RegexUtil.getList(getPageSourceCode(), URL_TITLE, 3);
	}

	/**
	 * ���˱�����emoji����
	 * @param emoji
	 * @return
	 */
	private List<String> getNoteContent(boolean emoji) {
		List<String> noteTitles = getNoteContent();
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
	 * ��װ����������������
	 * @return
	 */
	public List<TopicNote> getTopicNotes() {
		List<TopicNote> topicNotes = new ArrayList<TopicNote>();

		List<String> url = getNoteUrl(true);
		List<String> content = getNoteContent(true);
		List<String> count = getCount();

		for (int i = 0; i < url.size(); i++) {
			TopicNote topicNote = new TopicNote();
			topicNote.setUrl(url.get(i));
			topicNote.setContent(content.get(i).trim());
			topicNote.setCount(Integer.parseInt(count.get(i)));
			topicNote.setFloor(0);
			topicNote.setTime(TimeUtil.getCurTime());
			topicNotes.add(topicNote);
		}

		return topicNotes;
	}
	
    
    
	public static void main(String[] args) {

		CrawlTopicNote ctn = new CrawlTopicNote("http://tieba.baidu.com/f?kw=%CE%F7%B0%B2%BF%C6%BC%BC%B4%F3%D1%A7");

		int i = 1;
		for (TopicNote tn : ctn.getTopicNotes()) {
			System.out.println((i++)+" "+tn.getUrl()+" "+tn.getContent()+" "+tn.getCount()+" "+tn.getFloor()+" "+tn.getTime());
		}
		
		 
	}
}
