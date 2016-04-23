package netrobot.crawl.xustbar.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Java �̳߳ء������̲߳������߳�ͬ���Լ��̰߳�ȫ�˳�ʾ��
 * @author Dolphix Qing
 * 
 */
public class Test {

	private static boolean flag = true;
	
	private static int iStop = 0;
	
	/**
	 * 1."static��Ա����"Ϊ�̲߳���ȫ,��synchronized�߳�ͬ��. 
	 * 2."static��Ա�����̰߳�ȫ"
	 */
	private static synchronized void executeMethod() {
		System.out.println(Thread.currentThread().getName()+" isWorking, iStop = "+iStop);
		++iStop;
		if (iStop >= 3) {
			flag = false;
			System.out.println(Thread.currentThread().getName()+" isFinish");
		}
	}
	
	/**
	 * ����N�߳��������
	 * @param bar_url
	 * @param N
	 */
	public static void openNThreadCrawl(int N) {
		//�����̳߳�
		final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  
		for (int i = 0; i < N; i++) {  
			
			cachedThreadPool.execute(new Runnable() {  
				public void run() {  
					//ʹ�ñ�Ƿ���ȫ�˳��߳�
					while (flag) {
						try {
							executeMethod();
							Thread.sleep(1000);//��˯1000����
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
	
	public static void main(String[] args) {
		openNThreadCrawl(4);
	}

}
