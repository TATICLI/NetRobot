package netrobot.crawl.bar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import netrobot.crawl.Crawl;
import netrobot.crawl.bar.model.NoteDetail;
import netrobot.utils.RegexUtil;
import netrobot.utils.TimeUtil;

public class CrawlNoteDetail extends Crawl{	
	//������url
	private String note_url;  //�������ظ�

	//һ���ظ�ID
	private static final String ONE_REPLY_ID = "<div id=\"post_content_(\\d*?)\"";
	//һ���ظ�����
	private static final String ONE_REPLY_CONTEXT = "class=\"d_post_content j_d_post_content  clearfix\">            (.*?)</div>";
	
	private static final String REPLY_COUNT_PAGE = ">(\\d+)</span>�ظ�������<span class=\"red\">(\\d+)</span>ҳ</li>";

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
	private List<String> getFloorid(){
		return RegexUtil.getList(getPageSourceCode(), ONE_REPLY_ID, 1);
	}
	/**
	 * ��ȡһ���ظ�����
	 * @return
	 */
	private List<String> getContext(){
		return RegexUtil.getList(getPageSourceCode(), ONE_REPLY_CONTEXT, 1);
	}
	/**
	 * ��ȡ�ظ�������
	 * @return
	 */
	public String getCount(){
		return RegexUtil.getFirstString(getPageSourceCode(), REPLY_COUNT_PAGE, 1);
	}
	/**
	 * ��ȡ���ӻظ�ҳ��
	 * @return
	 */
	public String getReplyPage(){
		return RegexUtil.getFirstString(getPageSourceCode(), REPLY_COUNT_PAGE, 2);
	}
	/**
	 * ��ȡһ���ظ����ݣ�����
	 * @param exceptLabel
	 * @return
	 */
	private List<String> getContext(boolean exceptLabel){
		List<String> oneReplycList = getContext();
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
	 * ��װ������ϸ����
	 * @return
	 */
	public List<NoteDetail> getNoteDetails(int pn) {
		List<NoteDetail> noteDetails = new ArrayList<NoteDetail>();

		List<String> floorid = getFloorid();
		List<String> content = getContext(true);
		if (null == getCount() || getCount().equals("")) {
			System.out.println("reply count err!");
			return null;
		}
		int count = Integer.parseInt(getCount());
		for (int i = 0; i < floorid.size() && i < content.size(); i++) {
			if (null == content.get(i) || content.get(i).equals("") || content.get(i).length() < 6 || content.get(i).length() > 120) {
				continue;
			}
			NoteDetail noteDetail = new NoteDetail();

			noteDetail.setUrl(getNoteUrl(note_url));
			noteDetail.setFloorid(floorid.get(i));
			noteDetail.setContent(content.get(i).trim());
			noteDetail.setCount(count);
			noteDetail.setFloor(i+1+((pn-1)*50));
			if (1 == (i+1+((pn-1)*50))) {
				System.out.println("Floor one exist, insert topic!");
			}
			noteDetail.setTime(TimeUtil.getCurTime());
			noteDetails.add(noteDetail);
		}
		System.out.println("crawling url "+note_url);
		
		return noteDetails;
	}
	/**
	 * ��ԭ����url
	 * @param url
	 * @return
	 */
	private String getNoteUrl(String url) {

		return url.substring(0, (url.indexOf("?") > 0 ? url.indexOf("?"): url.length()));
	}
	
	
	
	public static void main(String[] args) {

		CrawlNoteDetail cnd = new CrawlNoteDetail("http://tieba.baidu.com/p/4566764422");
		System.out.println(cnd.getCount());
		
		
		int i = 1;
		for (NoteDetail nd : cnd.getNoteDetails(1)) {
			System.out.println((i++)+" "+nd.getUrl()+" "+nd.getContent()+" "+nd.getCount()+" "+nd.getFloorid()+" "+nd.getFloor()+" "+nd.getTime());
		}
	}
}
