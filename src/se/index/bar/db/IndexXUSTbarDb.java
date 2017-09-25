package se.index.bar.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import netrobot.crawl.bar.model.NoteDetail;
import netrobot.crawl.bar.model.TopicNote;
import netrobot.db.manager.DbServer;
import netrobot.utils.TimeUtil;
/**
 * ���ݿ����(import:proxool.cglib.jar)
 * @author Dolphix.J Qing
 *
 */
public class IndexXUSTbarDb {
	
	private static final String POOL_NAME = "proxool.vcrobot";
	
	
	private static final IndexXUSTbarDb db = new IndexXUSTbarDb();
	/**
	 * �����ݿ��л�ȡcrawlTopicNote�嵥,��ҳ��ѯ
	 * @return
	 */
	public List<TopicNote> getDbTopicNote(int row, int count){
		List<TopicNote> topicNotes = new ArrayList<TopicNote>();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			//SELECT * FROM `topicnote` LIMIT 1000, 1000
			String sql = "SELECT * FROM `topic` WHERE `lucene` = '1' LIMIT "+row+" , "+count;
			ResultSet rs = dbServer.select(sql);
			while (rs.next()){
				TopicNote tn = new TopicNote();
				tn.setId(rs.getInt("id"));
				tn.setUrl(rs.getString("url"));
				tn.setCount(rs.getInt("count"));
				tn.setContent(rs.getString("content"));
				topicNotes.add(tn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return topicNotes;
	}
	
	/**
	 * �����ݿ��л�ȡcrawlTopicDetail�嵥,��ҳ��ѯ
	 * @return
	 */
	public List<NoteDetail> getDbNoteDetail(int row, int count){
		List<NoteDetail> noteDetails = new ArrayList<NoteDetail>();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			//SELECT * FROM `notedetail` LIMIT 1000, 1000
			String sql = "SELECT * FROM `detail` WHERE `lucene` = '1' LIMIT "+row+" , "+count;
			ResultSet rs = dbServer.select(sql);
			while (rs.next()){
				NoteDetail nd = new NoteDetail();
				nd.setUrl(rs.getString("url"));
				nd.setId(rs.getInt("id"));
				nd.setFloorid(rs.getString("floorid"));
				nd.setContent(rs.getString("content"));
				nd.setScore(rs.getFloat("score"));
				noteDetails.add(nd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return noteDetails;
	}
	
	/**
	 * ��ȡ���ݿ���TopicNote����������������
	 * @return
	 */
	public int getDbTopicUpdateCount(){
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "SELECT COUNT(*) FROM `topic` WHERE `lucene` = '1'";
			ResultSet rs = dbServer.select(sql);
			while(rs.next()){
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return 0;
	}
	
	/**
	 * ��ȡ���ݿ���NoteDetail����������������
	 * @return
	 */
	public int getDbDetailUpdateCount(){
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "SELECT COUNT(*) FROM `detail` WHERE `lucene` = '1'";
			ResultSet rs = dbServer.select(sql);
			while(rs.next()){
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return 0;
	}
	
	/**
	 * ��������״̬
	 * @return
	 */
	public int updateDbIndexState(String tableName, int lucene, int start, int end){
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "UPDATE "+tableName+" SET lucene = '"+lucene+"' WHERE id >= "+start+" AND id <= "+end+"" ;
			return dbServer.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return 0;
	}
	
	/**
	 * �������ݿ�����״̬
	 * @return
	 */
	public int resetDbIndexState(String tableName, int lucene){
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "update "+tableName+" set `lucene` = '"+lucene+"'";
			return dbServer.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return 0;
	}
	/**
	 * ����Url����Db
	 * @param url
	 * @param n
	 * @return
	 */
	public List<String> searchDbByUrl(String url, int n){
		List<String> data = new ArrayList<String>();
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "SELECT content FROM detail WHERE url = '"+url+"'";
			ResultSet rs = dbServer.select(sql);
			while (rs.next() && n > 0){
				--n;
				String tmp = rs.getString("content");
				data.add(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return data;
	}
	
	/**
	 * �����ݿ��ж�ȡ������
	 * @param email
	 * @return
	 */
	public List<String> readDbContext(String email,String keywords){
		List<String> context = new ArrayList<String>();
		DbServer dbServer = new DbServer(POOL_NAME);
		int count = 0;
		boolean in = true;
		int id = 0;
		try {
			//����������
			String sql = "INSERT INTO context (email,content,time) VALUES ('"+email+"','"+keywords+"','"+TimeUtil.getCurTime()+"')";
//			System.out.println(sql);
			dbServer.insert(sql);
			//��ȡ��Ϣ
			sql = "SELECT * FROM context WHERE email = '"+email+"' ORDER BY id ASC";
			ResultSet rs = dbServer.select(sql);
			while (rs.next()){
				if (in) {
					in = false;
					id = rs.getInt("id");
				}
				String tmp = rs.getString("content");
				//ͳ�Ƽ�¼����
				++count;
				context.add(tmp);
			}
			//ɾ��������
			if (count > 3) {
				sql = "DELETE FROM context WHERE id = '"+id+"'";
				dbServer.delete(sql);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return context;
	}
	
	
	public static void main(String[] args) {
		db.readDbContext("487f87505f619bf9ea08f26bb34f8118","�㲻����ˣ�Ұ�");
	}
}




///**
// * �����ݿ��л�ȡcrawlTopicNote�嵥,��ҳ��ѯ
// * @return
// */
//public List<TopicNote> getTopicNoteContent(int row, int count){
//	List<TopicNote> topicNotes = new ArrayList<TopicNote>();
//	DbServer dbServer = new DbServer(POOL_NAME);
//	try {
//		//SELECT * FROM `topicnote` LIMIT 1000, 1000
//		String sql = "SELECT * FROM `topicnote` WHERE `index` = '1' LIMIT "+row+" , "+count;
//		ResultSet rs = dbServer.select(sql);
//		while (rs.next()) {
//			TopicNote topicNote = new TopicNote();
//			topicNotes.add(topicNote);
//			topicNote.setNote_url(rs.getString("note_url"));
//			topicNote.setNote_title(rs.getString("note_title"));
//			topicNote.setTopic_reply_count(rs.getInt("topic_reply_count"));
//			topicNote.setLast_reply_time(rs.getString("last_reply_time"));
//		}
//	} catch (Exception e) {
//		e.printStackTrace();
//	}finally {
//		dbServer.close();
//	}
//	return topicNotes;
//}
//
///**
// * �����ݿ��л�ȡcrawlTopicDetail�嵥,��ҳ��ѯ
// * @return
// */
//public List<NoteDetail> getNoteDetailContent(int row, int count){
//	List<NoteDetail> noteDetails = new ArrayList<NoteDetail>();
//	DbServer dbServer = new DbServer(POOL_NAME);
//	try {
//		//SELECT * FROM `notedetail` LIMIT 1000, 1000
//		String sql = "SELECT * FROM `notedetail` WHERE `index` = '1' LIMIT "+row+" , "+count;
//		ResultSet rs = dbServer.select(sql);
//		while (rs.next()) {
//			NoteDetail noteDetail = new NoteDetail();
//			noteDetail.setNote_url(rs.getString("note_url"));
//			noteDetail.setReply_context(rs.getString("reply_context"));
//			noteDetail.setReply_floor_id(rs.getString("reply_floor_id"));
//			noteDetail.setReply_parent_id(rs.getString("reply_parent_id"));
//			noteDetails.add(noteDetail);
//		}
//	} catch (Exception e) {
//		e.printStackTrace();
//	}finally {
//		dbServer.close();
//	}
//	return noteDetails;
//}
//
///**
// * ��ȡ���ݿ���TopicNote����������������
// * @return
// */
//public int getTopicNoteCount(){
//	DbServer dbServer = new DbServer(POOL_NAME);
//	try {
//		String sql = "SELECT COUNT(*) FROM `topicnote` WHERE `index` = '1'";
//		ResultSet rs = dbServer.select(sql);
//		while(rs.next()){
//			return rs.getInt(1);
//		}
//	} catch (Exception e) {
//		e.printStackTrace();
//	}finally {
//		dbServer.close();
//	}
//	return 0;
//}
//
///**
// * ��ȡ���ݿ���NoteDetail����������������
// * @return
// */
//public int getNoteDetailCount(){
//	DbServer dbServer = new DbServer(POOL_NAME);
//	try {
//		String sql = "SELECT COUNT(*) FROM `notedetail` WHERE `index` = '1'";
//		ResultSet rs = dbServer.select(sql);
//		while(rs.next()){
//			return rs.getInt(1);
//		}
//	} catch (Exception e) {
//		e.printStackTrace();
//	}finally {
//		dbServer.close();
//	}
//	return 0;
//}
//
///**
// * ��������״̬
// * @return
// */
//public int updateDbIndexOne(String tableName, int index){
//	DbServer dbServer = new DbServer(POOL_NAME);
//	try {
//		String sql = "update "+tableName+" set `index` = '"+index+"'";
//		return dbServer.update(sql);
//	} catch (Exception e) {
//		e.printStackTrace();
//	}finally {
//		dbServer.close();
//	}
//	return 0;
//}

//-------------------

