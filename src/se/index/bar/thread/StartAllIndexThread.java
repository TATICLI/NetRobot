package se.index.bar.thread;

import se.index.entity.IndexInit;
import se.index.operation.bar.BarNRTIndex;

/**
 * ��������Db�����߳�
 * @author Dolphix.J Qing
 *
 */
public class StartAllIndexThread {

	/**
	 * ��������Db�����߳�
	 */
	public static void openAllIndexThread() {
		IndexInit.initIndex();
		BarNRTIndex barNRTIndex = new BarNRTIndex();
		//��1�������ϵ��ӳ�
		barNRTIndex.indexQ();
		barNRTIndex.indexA();
	}
}
