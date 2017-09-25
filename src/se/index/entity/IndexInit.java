package se.index.entity;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import se.index.model.ConfigBean;
import se.index.model.IndexConfig;
import se.index.utils.Const;

public class IndexInit {

	/**
	 * ��ʼ����������
	 */
	public static void initIndex(){
		HashSet<ConfigBean> hashSet = new HashSet<ConfigBean>();
		ConfigBean bean = new ConfigBean();
		bean.setIndexPath(Const.INDEX_PATH);
		bean.setIndexName(Const.INDEX_NAME);
		//����ִʼ���
		HashMap<String, Analyzer> fieldAnalyzers = new HashMap<String, Analyzer>();
		//TODO �����Ҫ���ֶΣ��ִʼ���
		fieldAnalyzers.put("url", new WhitespaceAnalyzer(Version.LUCENE_43));
		fieldAnalyzers.put("url2", new WhitespaceAnalyzer(Version.LUCENE_43));
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new IKAnalyzer(), fieldAnalyzers);
		bean.setAnalyzer(analyzer);
		hashSet.add(bean);
		//�������
		IndexConfig.setConfigBeans(hashSet);
	}
}
