package netrobot.crawl.bar.thread;

public class StartAllCrawlThread {

	/**
	 * ��������ץȡ�߳�
	 */
	public static void startAllCrawlThread(){
		//ץȡbar��ҳ��Ϣ
		BarThread.openCrawlBarThread();
		//ץȡ��������
		TopicNoteThread.openTopicNoteThread();
		//ץȡ��������
		NoteDetailThread.openNoteDetailThread();
	}
	
	public static void main(String[] args) throws Exception{
		startAllCrawlThread();
	}

}
