package netrobot.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * Xml������(import:dom4j-1.6.1.jar)
 * @author Edchel
 *
 */
public class XmlUtil {
	private static final String noResult = "<result>no result</result>";
	
	/**
	 * xmlת��java bean
	 * @param xml
	 * @return
	 */
	public static Document createFromString(String xml) {
		try {
			return DocumentHelper.parseText(xml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * java beanת��Ϊxml
	 * @param object
	 * @return
	 */
	public static String parseObject2Xml(Object object){
		if (null == object) {
			return noResult;
		}
		StringWriter sw = new StringWriter();
		JAXBContext jaxbContext;
		Marshaller marshaller;
		try {
			jaxbContext = JAXBContext.newInstance(object.getClass());
			marshaller = jaxbContext.createMarshaller();
			marshaller.marshal(object, sw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return noResult;	
	}
	/**
	 * ��ȡָ��xpath���ı���������ʧ�ܷ���null
	 * @param xpath
	 * @param node
	 * @return
	 */
	public static String getTextFromNode(String xpath,Node node){
		try {
			return node.selectSingleNode(xpath).getText();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * ��ȡxml�ļ�
	 * @param path
	 * @return xml�ļ���Ӧ��Document
	 */
	public static Document createFromPath(String path){
		return createFromString(readFile(path));
	}

	/**
	 *  ���ļ�
	 * @param path
	 * @return �����ļ������ַ���
	 */
	private static String readFile(String path) {
		File file = new File(path);
		FileInputStream fileInputStream;
		StringBuffer sb = new StringBuffer();
		try {
			fileInputStream = new FileInputStream(file);
			//����ʹ��UTF-8��ȡ����
			String charset = CharsetUtil.getStreamCharset(file.toURI().toURL(), "utf-8");
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charset);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String s;
			while ((s = bufferedReader.readLine()) != null){
				s = s.replaceAll("\t", "").trim();
				if (s.length() > 0){
					sb.append(s);
				}
			}
			fileInputStream.close();
			bufferedReader.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return sb.toString();
	}
}
