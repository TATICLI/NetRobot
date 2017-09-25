package se.index.utils;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * ���ķִ�
 * @author Dolphix.J Qing
 *
 */
public class SplitUtil {

	public static String splitWord(Analyzer analyzer,String str) {
		if (analyzer == null || str == null) {
			return null;
		}
		try {
			StringReader sr = new StringReader(str);
			TokenStream tokenStream = analyzer.tokenStream("", sr);
			tokenStream.reset();
			CharTermAttribute term = tokenStream.getAttribute(CharTermAttribute.class);
			//ѭ������ִʽ��
			String word = "";
			while (tokenStream.incrementToken()) {
				word += (term.toString()+"/");
			}
			return word.substring(0, word.length()-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
}
