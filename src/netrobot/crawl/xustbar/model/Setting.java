package netrobot.crawl.xustbar.model;

/**
 * ����������
 * @author qingdujun
 *
 */
public class Setting {
	//ĳ����ҳurl
	private String bar_url;  //����
	//����
	private String bar_name;
	//ץȡ��������
	private int bar_crawl_note_count;
	//��ȡƵ��
	private int crawl_frequency;
	//���һ����ȡʱ��
	private String last_crawl_time;
	
	public String getBar_url() {
		return bar_url;
	}
	public void setBar_url(String bar_url) {
		this.bar_url = bar_url;
	}
	public String getBar_name() {
		return bar_name;
	}
	public void setBar_name(String bar_name) {
		this.bar_name = bar_name;
	}
	public int getBar_crawl_note_count() {
		return bar_crawl_note_count;
	}
	public void setBar_crawl_note_count(int bar_crawl_note_count) {
		this.bar_crawl_note_count = bar_crawl_note_count;
	}
	public int getCrawl_frequency() {
		return crawl_frequency;
	}
	public void setCrawl_frequency(int crawl_frequency) {
		this.crawl_frequency = crawl_frequency;
	}
	public String getLast_crawl_time() {
		return last_crawl_time;
	}
	public void setLast_crawl_time(String last_crawl_time) {
		this.last_crawl_time = last_crawl_time;
	}
}
