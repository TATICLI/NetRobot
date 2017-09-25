package se.index.operation.bar;

import java.util.ArrayList;
import java.util.List;

import netrobot.crawl.bar.model.NoteDetail;
import netrobot.crawl.bar.model.TopicNote;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import se.index.bar.db.IndexXUSTbarDb;
import se.index.model.bar.DetailIndexBean;
import se.index.model.bar.TopicIndexBean;
import se.index.operation.NRTIndex;
import se.index.utils.Const;
import se.index.utils.ParseBean;

/**
 * ��������
 * @author Dolphix.J Qing
 *
 */
public class BarNRTIndex extends NRTIndex{
	
	private IndexXUSTbarDb db;
	
	public BarNRTIndex() {
		super(Const.INDEX_NAME);
		db = new IndexXUSTbarDb();
	}
	/**
	 * ���һ����������������
	 * @param bean
	 * @return
	 */
	public boolean addTopicIndex(TopicIndexBean bean){
		Document doc = ParseBean.parseTitle2Doc(bean);
		if (null != doc) {
			return updateDocument(new Term("url"), doc);
		}
		return false;
	} 
	
	/**
	 * ���һ��������������������
	 * @param bean
	 * @return
	 */
	public boolean addDetailIndex(DetailIndexBean bean){
		Document doc = ParseBean.parseContent2Doc(bean);
		if (null != doc) {
			return updateDocument(new Term("rid"), doc);
		}
		return false;
	} 
	/**
	 * �����ݿ�������������������
	 * @return
	 */
	public void dbTopicImport2Index(){
		List<TopicNote> topicNotes = null;
		int count = 1000;
		boolean flag = true;
		while( flag ) {
			//��ҳ������ѯ��Ӧ�ô�һֱ��row = 0��ʼ
			topicNotes = db.getDbTopicNote(0, count);
			if (null == topicNotes || 0 == topicNotes.size()) {
				flag = false;
				continue;
			}
			for (TopicNote topicNote : topicNotes) {
				TopicIndexBean bean = new TopicIndexBean();
				bean.setUrl(topicNote.getUrl());
				bean.setCount(topicNote.getCount());
				bean.setTitle(topicNote.getContent());
				addTopicIndex(bean);
			}
			
			commit();
			db.updateDbIndexState("topic", 0,topicNotes.get(0).getId(),topicNotes.get(topicNotes.size()-1).getId());
		}
	}

	/**
	 * �����ݿ���������������������
	 * @return
	 */
	public void dbDetailImport2Index(){
		List<NoteDetail> noteDetails = new ArrayList<NoteDetail>();
		int count = 1000;
		boolean flag = true;
		while( flag ) {
			//��ҳ������ѯ��Ӧ�ô�һֱ��row = 0��ʼ
			noteDetails = db.getDbNoteDetail(0, count);
			if (null == noteDetails || 0 == noteDetails.size()) {
				flag = false;
				continue;
			}
			for (NoteDetail noteDetail: noteDetails) {
				DetailIndexBean bean = new DetailIndexBean();
				bean.setUrl2(noteDetail.getUrl());
				bean.setRid(noteDetail.getFloorid());
				bean.setContent(noteDetail.getContent());
				bean.setScore(noteDetail.getScore());
				addDetailIndex(bean);
			}
			commit();
			db.updateDbIndexState("detail", 0,noteDetails.get(0).getId(),noteDetails.get(noteDetails.size()-1).getId());
			
		}
	}
	/**
	 * ��������
	 */
	public void indexQ() {
		new Thread(new Runnable() {
			boolean flagQ = true;
			@Override
			public void run() {
				while(flagQ){
					System.out.println("Tips - Indexing Q");
					dbTopicImport2Index();
					System.out.println("Tips - Q index finish");
					try {
						//1�����ӳ�
						Thread.sleep(60*1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	/**
	 * �����ظ�
	 */
	public void indexA() {
		new Thread(new Runnable() {
			boolean flagA = true;
			@Override
			public void run() {
				while(flagA){
					System.out.println("Tips - Indexing A");
					dbDetailImport2Index();
					System.out.println("Tips - A index finish");
					try {
						//һ�����ӳ�
						Thread.sleep(60*1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}

















//NRTIndex nrtIndexTopic = null;
//NRTIndex nrtIndexDetail = null;
//private HashSet<ConfigBean> configBeans = new HashSet<ConfigBean>();
//
//private int count = 1000;
//
////������
//public static List<String> indexName;
////������
//public static List<String> indexPath;
//
//static{
//	//������
//	indexName = new ArrayList<String>();
//	indexName.add("topicnote");
//	indexName.add("notedetail");
//	//����·��
//	indexPath = new ArrayList<String>();
//	indexPath.add("E:/lucene_index_bar/xust");
//	indexPath.add("E:/lucene_index_bar/xust");
//}
//
//public BarNRTIndex(){
//	setConfigBeans(setHashMap(indexName), setHashMap(indexPath));
//	db = new IndexXUSTbarDb();
//	nrtIndexTopic = new NRTIndex("topicnote");
//	nrtIndexDetail = new NRTIndex("notedetail");
//}
//
///**
// * ��������ļ�����
// * @param indexName
// * @param indexPath
// */
//private void setConfigBeans(HashMap<Integer, String> indexName, HashMap<Integer, String> indexPath){
//	for (int i = 0; i < indexName.size(); i++) {
//		ConfigBean bean = new ConfigBean();
//		bean.setIndexPath(indexPath.get(i));
//		bean.setIndexName(indexName.get(i));
//		configBeans.add(bean);
//	}
//	IndexConfig.setConfigBeans(configBeans);
//}
///**
// * ����HashMap
// * @param list
// * @return
// */
//public static HashMap<Integer, String> setHashMap(List<String> list) {
//	HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
//	for (int i = 0; i < list.size(); i++) {
//		hashMap.put(i, list.get(i));
//	}
//	return hashMap;
//}
///**
// * ��������
// */
//private void addDocument(NRTIndex nrtIndex, String url, String content){
//	Document doc = new Document();
//	//Ĭ�ϲ��ִ�
//	doc.add(new StringField("url",url,Store.YES));
//	//Ĭ�Ϸִ�
//	doc.add(new TextField("content",content,Store.YES));
//	nrtIndex.addDocument(doc);
//}
///**
// * ��ȡ���ݿ�����������
// * @param row
// * @param count
// * @return
// */
//private List<TopicNote> getDbTopicNotesContent(int row, int count){
//	List<TopicNote> topicNotes = db.getTopicNoteContent(row, count);
//	return topicNotes;
//}
///**
// * ��ȡ���ݿ�����������
// * @param row
// * @param count
// * @return
// */
//private List<NoteDetail> getDbNoteDetailsContent(int row, int count){
//	List<NoteDetail> noteDetails = db.getNoteDetailContent(row, count);
//	return noteDetails;
//}
///**
// * �������������������
// * @return
// */
//private int getDbTopicNoteCount(){
//	return db.getTopicNoteCount();
//}
///**
// * �������������������
// * @return
// */
//private int getDbNoteDetailCount(){
//	return db.getNoteDetailCount();
//}
///**
// * ִ��������������
// */
//public void executeTopicNoteIndex(){
//	int index_count = getDbTopicNoteCount();
//	int row = 0;
//	List<TopicNote> topicNotes;
//	for (  ; row < index_count;  ) {
//		//��ҳ��ȡ����������
//		topicNotes = getDbTopicNotesContent(row, count);
//		row += topicNotes.size();
//		//�����ӱ��⽨������
//		for (int i = 0; i < topicNotes.size(); i++) {
//			addDocument(nrtIndexTopic,topicNotes.get(i).getNote_url(), topicNotes.get(i).getNote_title());
//		}
//		nrtIndexTopic.commit();
//	}
//}
///**
// * ִ��������������
// */
//public void executeNoteDetailIndex(){
//	int index_count = getDbNoteDetailCount();
//	int row = 0;
//	List<NoteDetail> noteDetails;
//	for (  ; row < index_count;  ) {
//		//��ҳ��ȡ����������
//		noteDetails = getDbNoteDetailsContent(row, count);
//		row += noteDetails.size();
//		//�����ӱ��⽨������
//		for (int i = 0; i < noteDetails.size(); i++) {
//			addDocument(nrtIndexDetail,noteDetails.get(i).getNote_url(), noteDetails.get(i).getReply_context());
//		}
//		nrtIndexDetail.commit();
//	}
//}