package netrobot.utils;
/**
 * ����������Ŀ¼
 * @author qingdujun
 *
 */
public class ClassUtil {

	public static String getClassPath(Class<?> c){
		return c.getResource("").getPath().replaceAll("%20", " ");
	}
	
	public static String getClassRootPath(Class<?> c){
		return c.getResource("/").getPath().replaceAll("%20", " ");
	}

	/**
	 * ����class�ļ����ڵ�Ŀ¼
	 * @param c
	 * @param hasName
	 * @return
	 */
	public static String getClassPath(Class<?> c, boolean hasName) {
		String name = c.getSimpleName() + ".class";
		String path = c.getResource(name).getPath().replaceAll("%20", " ");
		if (hasName) {
			return path;
		} else {
			return path.substring(0, path.length() - name.length());
		}
	}
}
