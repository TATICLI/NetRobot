package ltp.cloud.analysis;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import ltp.cloud.bean.Word;
import ltp.cloud.utils.ConstUtil;
import ltp.cloud.utils.NetRequest;

public class TypeWord {

	//http://api.ltp-cloud.com/analysis/?api_key=API_KEY&text=""&pattern=pos&format=json
	/**
	 * ��÷������List
	 * @param keywords
	 * @return
	 */
	public static List<Word> analysisWordType(String keywords){
		
		String json = NetRequest.doGet(getLTPPosURL(keywords));
		if (json == null) {
			return null;
		}

		return parseJson(json);
	}
	
	/**
	 * ƴ��LTP����URL
	 * @param text
	 * @return
	 */
	private static String getLTPPosURL(String text){
		return ConstUtil.LTP_URL+"?api_key="+ConstUtil.api_key+"&text="+text+"&pattern=pos&format=json";
	}
	/**
	 * JsonתList
	 * @param json
	 * @return
	 */
	private static List<Word> parseJson(String json){
		List<Word> words = new ArrayList<Word>();
		JSONArray jsonArray = JSONArray.fromObject(json).getJSONArray(0).getJSONArray(0);
		
		for (int i = 0; i < jsonArray.size(); i++) {
			String pos = (String)jsonArray.getJSONObject(i).getString("pos");
			if (pos.equals("n") || pos.equals("nh") || pos.equals("ni") || pos.equals("nl") || pos.equals("ns")
					|| pos.equals("nt") || pos.equals("nz") || pos.equals("ws")) {
				String cont = (String)jsonArray.getJSONObject(i).getString("cont");
				
				Word word = new Word();
				word.setPos(pos);
				word.setCont(cont);
				
				words.add(word);
			}
		}
		return words;
	}
	
	public static void main(String[] args) {
//		String json = "[[[{\"id\": 2, \"cont\": \"�й�\", \"pos\": \"ns\"}, {\"id\": 3, \"cont\": \"��\", \"pos\": \"n\"}]]]";

		for (Word w : analysisWordType("����Android�������������")) {
			System.out.println(w.getPos()+"-----"+w.getCont());
		}

	}
}
