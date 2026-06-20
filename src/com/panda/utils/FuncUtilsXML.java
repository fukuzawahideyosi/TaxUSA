package com.panda.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class FuncUtilsXML {

	private static Logger logger = Logger.getLogger(FuncUtilsXML.class.toString());

	public static String getTagValue(Document doc, String tagName) {
		NodeList list = doc.getElementsByTagNameNS("*", tagName);
		if (list.getLength() > 0) {
			return list.item(0).getTextContent().trim();
		}
		return "";
	}


	  public static String parseWarekiDate(Document doc, String tagName) throws Exception {

	        XPath xpath = XPathFactory.newInstance().newXPath();

	        String base = "//*[local-name()='" + tagName + "']";

	        String era = xpath.evaluate(base + "/*[local-name()='era']", doc);
	        String yy  = xpath.evaluate(base + "/*[local-name()='yy']", doc);
	        String mm  = xpath.evaluate(base + "/*[local-name()='mm']", doc);
	        String dd  = xpath.evaluate(base + "/*[local-name()='dd']", doc);

	        int westernYear = convertToWestern(Integer.parseInt(era), Integer.parseInt(yy));

	        LocalDate date = LocalDate.of(
	                westernYear,
	                Integer.parseInt(mm),
	                Integer.parseInt(dd)
	        );

	        return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	    }

	    private static int convertToWestern(int era, int yy) {

	        int startYear;

	        switch (era) {
	            case 1: startYear = 1868; break; // 明治
	            case 2: startYear = 1912; break; // 大正
	            case 3: startYear = 1926; break; // 昭和
	            case 4: startYear = 1989; break; // 平成
	            case 5: startYear = 2019; break; // 令和
	            default: throw new IllegalArgumentException("未知 era: " + era);
	        }

	        return startYear + yy - 1;
	    }
}