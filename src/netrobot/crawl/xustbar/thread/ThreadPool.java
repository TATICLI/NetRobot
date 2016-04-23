package netrobot.crawl.xustbar.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadPool {

	protected boolean flag = false;
	
	protected long SLEEP_TIME = 1000;
	
	/**
	 * ����N�߳��������
	 * @param N
	 */
	protected void openNThreadCrawl(int N) {
		//�����̳߳�
		final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  
		for (int i = 0; i < N; i++) {  
			
			cachedThreadPool.execute(new Runnable() {  
				public void run() {  
					//ʹ�ñ�Ƿ���ȫ�˳��߳�
					while (flag) {
						try {
							executeMethod();
							Thread.sleep(SLEEP_TIME);//��˯1000����
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally{
							//�ر��̳߳�
							cachedThreadPool.shutdown();
						}
					}
				}  
			});  
		}  
	}
	//������д�˷���
	protected abstract void executeMethod();
	
	protected void setSleepTime(long time) {
		this.SLEEP_TIME = time;
	}
	protected long getSleepTime() {
		return SLEEP_TIME;
	}
	protected boolean getFlag() {
		return flag;
	}
	protected void setFlag(boolean flag) {
		this.flag = flag;
	}
}
