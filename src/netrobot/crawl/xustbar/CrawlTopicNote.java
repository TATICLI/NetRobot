package netrobot.crawl.xustbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import netrobot.crawl.Crawl;
import netrobot.utils.RegexUtil;

/**
 * ��ȡ������ҳ��������url
 * @author qingdujun
 *
 */
public class CrawlTopicNote extends Crawl{

	private String url;
	private static HashMap<String, String> params;
	//ƥ��������ҳ����url����
	private static final String NOTE_URL = "<div class=\"threadlist_text threadlist_title j_th_tit  \">            <a href=\"(.*?)\"";
	//ƥ�����ظ�ʱ��
	private static final String LAST_REPLY_TIME = "<span class=\"threadlist_reply_date j_reply_data\" title=\"���ظ�ʱ��\">            (.*?)</span>";
	
	static{
		params = new HashMap<String,String>();
		params.put("Referer", "tieba.baidu.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
		params.put("Host", "tieba.baidu.com");
	}
	public CrawlTopicNote(String url) {
		readPageByGet(url, params, "UTF-8");
		this.url = url;
	}


	public List<String> getNextNoteUrl(){
		return RegexUtil.getArrayList(getPageSourceCode(), NOTE_URL, url, 1);
	}
	
	public List<String> getLastReplyTime(){
		return RegexUtil.getList(getPageSourceCode(), LAST_REPLY_TIME, 1);
	}
	
	/**
	 * �ų�����������ַ
	 * @param exceptOther
	 * @return
	 */
	public List<String> getNextNoteUrl(boolean exceptOther) {
		List<String> urls = getNextNoteUrl();
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

	public static void main(String[] args) {
		//������ΪXUST��ҳ����
		CrawlTopicNote updateUrlTime = new CrawlTopicNote("http://tieba.baidu.com/f?ie=utf-8&kw=%E8%A5%BF%E5%AE%89%E7%A7%91%E6%8A%80%E5%A4%A7%E5%AD%A6");

		List<String> nextUrls = updateUrlTime.getNextNoteUrl(true);
		List<String> lastReplyTimes = updateUrlTime.getLastReplyTime();
		

		for (int i = 0; i < nextUrls.size(); i++) {
			System.out.println(i+" "+nextUrls.get(i)+" "+lastReplyTimes.get(i));
		}
	}
}
