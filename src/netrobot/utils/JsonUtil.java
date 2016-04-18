package netrobot.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Json������(import:jackson-databind-2.1.4.jar,jackson-annotations-2.1.4.jar,jackson-core-2.1.4.jar)
 * @author qingdujun
 *
 */
public class JsonUtil {

	private static final String noData = "{\"result\" : null}";
	private static ObjectMapper mapper;
	
	static{
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
	/**
	 * Jsonת��Ϊ�ַ���
	 * @param object
	 * @return
	 */
	public static String parseJson(Object object){
		if (null == object) {
			return noData;
		}try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noData;
	}
	
	/**
	 * Jsonת��Ϊjava bean
	 * @param json
	 * @return
	 */
	public static JsonNode json2Object(String json){
		try {
			return mapper.readTree(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * ����java�������ɶ�Ӧjson,����ָ��һ��json��root��
	 * @param obj
	 * @param root
	 * @return ������ʧ�ܷ���{datas:null} 
	 * 
	 */
	public static String parseJson(Object obj, String root){

		if(obj == null){
			return noData;
		}

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("{\"");
			sb.append(root);
			sb.append("\":");
			sb.append(mapper.writeValueAsString(obj));
			sb.append("}");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noData;
	}

	/**
	 * ��json�ַ�����װ��jsonp������var data={}��ʽ
	 * @param json
	 * @param var
	 * @return ������varΪnull����Ĭ�ϱ�����Ϊdatas
	 * 
	 */
	public static String wrapperJsonp(String json, String var){
		if(var == null){
			var = "datas";
		}
		return new StringBuilder().append("var ").append(var).append("=").append(json).toString();
	}
}
