package netrobot.crawl.xustbar.db;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import netrobot.crawl.xustbar.model.NoteDetail;
import netrobot.crawl.xustbar.model.Setting;
import netrobot.crawl.xustbar.model.TopicNote;
import netrobot.db.manager.DbServer;
import netrobot.utils.JsonUtil;
import netrobot.utils.ParseMD5;
/**
 * ���ݿ����(import:proxool.cglib.jar)
 * @author qingdujun
 *
 */
public class XUSTbarDb {
	
	private static int iStop = 1;

	private static final String POOL_NAME = "proxool.tiebadb";

	/**
	 * �߳��Ƿ�ֹͣ
	 * @return
	 */
	public int getiStop() {
		return iStop;
	}
	/**
	 * �����ݿ��л�ȡSetting��
	 * @return
	 */
	public List<Setting> getSettingTableInfo(){
		List<Setting> settings = new ArrayList<Setting>();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select * from setting";
			ResultSet rs = dbServer.select(sql);
			while (rs.next()) {
				Setting setting = new Setting();
				setting.setBar_url(rs.getString("bar_url"));
				setting.setBar_name(rs.getString("bar_name"));
				setting.setBar_crawl_note_count(rs.getInt("bar_crawl_note_count"));
				setting.setCrawl_frequency(rs.getInt("crawl_frequency"));
				setting.setLast_crawl_time(rs.getString("last_crawl_time"));
				settings.add(setting);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return settings;
	}
	
	/**
	 * ����Setting��Ϣ
	 * @param setting
	 * @param isMD5
	 */
	public void saveSettingCrawlInfo(Setting setting,boolean isMD5) {
		if (null == setting) {
			return;
		}

		String bar_url = ParseMD5.parseStr2MD5(setting.getBar_url());
		if (hasExistBarUrl(bar_url) || hasExistBarUrl(setting.getBar_url())) {
			updateSettingInfo(setting, isMD5);
		}else {
			insertSettingInfo(setting,isMD5);
		}
	}
	/**
	 * ��Setting����в������
	 * @param setting
	 * @param isMD5
	 */
	private void insertSettingInfo(Setting setting,boolean isMD5) {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			if (isMD5) {
				//MD5����
				params.put(i++, ParseMD5.parseStr2MD5(setting.getBar_url()));
				params.put(i++, ParseMD5.parseStr2MD5(setting.getBar_name()));
				params.put(i++, setting.getBar_crawl_note_count());
				params.put(i++, setting.getCrawl_frequency());
				params.put(i++, setting.getLast_crawl_time());
				params.put(i, 1);
			}else {
				params.put(i++, setting.getBar_url());
				params.put(i++, setting.getBar_name());
				params.put(i++, setting.getBar_crawl_note_count());
				params.put(i++, setting.getCrawl_frequency());
				params.put(i++, setting.getLast_crawl_time());
				params.put(i, 1);
			}
			
			dbServer.insert("setting","bar_url,bar_name,bar_crawl_note_count,crawl_frequency,last_crawl_time,state",params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * �������ɲɼ�����������Ŀ
	 * @param topicNote
	 * @param isMD5
	 */
	private void updateSettingInfo(Setting setting, boolean isMD5){
		if (null == setting) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer,Object>();
			int i = 1;

			if (isMD5) {
				//MD5����
				params.put(i++, ParseMD5.parseStr2MD5(setting.getBar_name()));
				params.put(i++, setting.getBar_crawl_note_count());
				params.put(i++, setting.getCrawl_frequency());
				params.put(i++, setting.getLast_crawl_time());
				params.put(i, 1);
			}else {
				params.put(i++, setting.getBar_name());
				params.put(i++, setting.getBar_crawl_note_count());
				params.put(i++, setting.getCrawl_frequency());
				params.put(i++, setting.getLast_crawl_time());
				params.put(i, 1);
			}
			String columns = "bar_name,bar_crawl_note_count,crawl_frequency,last_crawl_time,state";
			String condition = "where bar_url = '"+setting.getBar_url()+"'";
			dbServer.update("setting", columns, condition, params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * �ж�Bar_Url�Ƿ����
	 * @param note_url
	 * @return
	 */
	private boolean hasExistBarUrl(String bar_url) {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select sum(1) as count from setting where bar_url = '"+bar_url+"'";
			ResultSet rs = dbServer.select(sql);
			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return true;
	}
	
	/**
	 * �����ݿ��л�ȡcrawlTopicNote�嵥
	 * @return
	 */
	public List<TopicNote> getTopicNoteCrawlList(){
		List<TopicNote> topicNotes = new ArrayList<TopicNote>();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select * from topicnote where `state` = '1'";
			ResultSet rs = dbServer.select(sql);
			while (rs.next()) {
				TopicNote topicNote = new TopicNote();
				topicNotes.add(topicNote);
				topicNote.setNote_url(rs.getString("note_url"));
				topicNote.setNote_title(rs.getString("note_title"));
				topicNote.setTopic_reply_count(rs.getInt("topic_reply_count"));
				topicNote.setLast_reply_time(rs.getString("last_reply_time"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return topicNotes;
	}
	/**
	 * 
	 * @param topicNotes
	 * @param isMD5  �Ƿ����
	 */
	public void saveTopicNoteCrawlInfo(List<TopicNote> topicNotes,boolean isMD5) {
		if (null == topicNotes) {
			return;
		}
		for (TopicNote topicNote : topicNotes) {
			//MD5����
			String note_url = ParseMD5.parseStr2MD5(topicNote.getNote_url());
			if (hasExistNoteUrl(note_url) || hasExistNoteUrl(topicNote.getNote_url())) {
//				updateStateValue(note_url, 1);
				++iStop;
				//if (iStop >= 150)  Stop Thread!,return
				updateTopicNoteCount(topicNote, isMD5);
			}else {
				iStop = 1;
				insertTopicNoteCrawlInfo(topicNote,isMD5);
			}
		}
	}
	public void method() {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	/**
	 * ����stateֵ
	 * @param note_url
	 * @param state
	 */
	private void updateStateValue(String note_url, int state) {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "update topicnote set `state` = '"+state+"' where note_url = '"+note_url+"'";
			dbServer.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	/**
	 * �ж�Note_Url�Ƿ����
	 * @param note_url
	 * @return
	 */
	private boolean hasExistNoteUrl(String note_url) {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select sum(1) as count from topicnote where note_url = '"+note_url+"'";
			ResultSet rs = dbServer.select(sql);
			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return true;
	}
	
	/**
	 * ִ���������������
	 * @param topicNote
	 * @param isMD5
	 */
	private void insertTopicNoteCrawlInfo(TopicNote topicNote, boolean isMD5) {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			if (isMD5) {
				//MD5����
				params.put(i++, ParseMD5.parseStr2MD5(topicNote.getNote_url()));
				params.put(i++, topicNote.getTopic_reply_count());
				params.put(i++, ParseMD5.parseStr2MD5(topicNote.getNote_title()));
				params.put(i++, ParseMD5.parseStr2MD5(topicNote.getLast_reply_time()));
				params.put(i, 1);
			}else {
				params.put(i++, topicNote.getNote_url());
				params.put(i++, topicNote.getTopic_reply_count());
				params.put(i++, topicNote.getNote_title());
				params.put(i++, topicNote.getLast_reply_time());
				params.put(i, 1);
			}
			
			dbServer.insert("topicnote","note_url,topic_reply_count,note_title,last_reply_time,state",params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	/**
	 * ������������ʱ��
	 * @param topicNote
	 * @param isMD5
	 */
	private void updateTopicNoteTime(TopicNote topicNote, boolean isMD5){
		if (null == topicNote) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer,Object>();
			int i = 1;
			if (isMD5) {
				//MD5����
				params.put(i++, topicNote.getLast_reply_time());
				params.put(i++, 0);
			}else {
				params.put(i++, topicNote.getLast_reply_time());
				params.put(i++, 0);
			}
			String columns = "reply_time,state";
			String condition = "where note_url = '"+topicNote.getNote_url()+"'";
			dbServer.update("topicnote", columns, condition, params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * ��������������Ŀ
	 * @param topicNote
	 * @param isMD5
	 */
	private void updateTopicNoteCount(TopicNote topicNote, boolean isMD5){
		if (null == topicNote) {
			return;
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer,Object>();
			int i = 1;
			if (isMD5) {
				//MD5����
				params.put(i++, topicNote.getTopic_reply_count());
			}else {
				params.put(i++, topicNote.getTopic_reply_count());
			}
			String columns = "topic_reply_count";
			String condition = "where note_url = '"+topicNote.getNote_url()+"'";
			dbServer.update("topicnote", columns, condition, params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	/**
	 * ������������
	 * @param noteDetails
	 * @param isMD5
	 */
	public void saveNoteDetailInfo(List<NoteDetail> noteDetails,boolean isMD5) {
		if (null == noteDetails) {
			return;
		}
		for (NoteDetail noteDetail : noteDetails) {
			//MD5����
			String reply_floor_id = ParseMD5.parseStr2MD5(noteDetail.getReply_floor_id());
			if (hasExistNoteUrl(reply_floor_id) || hasExistNoteUrl(noteDetail.getReply_floor_id())) {
//				updateStateValue(note_url, 1);
				continue;
			}else {
				insertNoteDetailInfo(noteDetail,isMD5);
			}
		}
	}
	
	/**
	 * ִ����������������
	 * @param noteDetail
	 * @param isMD5
	 */
	private void insertNoteDetailInfo(NoteDetail noteDetail, boolean isMD5) {
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			if (isMD5) {
				//MD5����
				params.put(i++, ParseMD5.parseStr2MD5(noteDetail.getNote_url()));
				params.put(i++, ParseMD5.parseStr2MD5(noteDetail.getReply_floor_id()));
				params.put(i++, ParseMD5.parseStr2MD5("0"));
				params.put(i++, ParseMD5.parseStr2MD5(noteDetail.getReply_context()));
				params.put(i, 1);
			}else {
				params.put(i++, noteDetail.getNote_url());
				params.put(i++, noteDetail.getReply_floor_id());
				params.put(i++, "0");
				params.put(i++, noteDetail.getReply_context());
				params.put(i, 1);
			}
			
			dbServer.insert("notedetail","note_url,reply_floor_id,reply_parent_id,reply_context,state",params);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
	}
	
	public static void main(String[] args) {
		
		
		XUSTbarDb db = new XUSTbarDb();
		System.out.println(JsonUtil.parseJson(db.getTopicNoteCrawlList()));

	}
}
