package com.panda.utils;



import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLCalculator {


	private static Logger logger = Logger.getLogger(XMLCalculator.class.toString());


    public static Object calculate(String formula, String xmlContent, String jisuan_info, StringBuffer loggerStringBuffer) throws Exception {
        Map<String, String> valuesMap = parseXMLToMap(xmlContent); // 从 XML 获取值
        formula = FuncUtils.toHalfWidth(formula);
        formula = formula.replace("×", "*");


        Object result = evaluateFormula(formula, valuesMap, jisuan_info, loggerStringBuffer);
        return result; // 使用公式计算结果
    }

    private static Map<String, String> parseXMLToMap(String xmlContent) throws Exception {
    	xmlContent = "<PS_ROOT>" + xmlContent + "</PS_ROOT>";

        Map<String, String> valuesMap = new HashMap<>();

        // 解析 XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xpath.evaluate("//*[text()]", doc, XPathConstants.NODESET);

        // 将每个标签名及其值存入 Map
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String tagName = node.getNodeName();
            String value = node.getTextContent().trim();

            if (!value.isEmpty()) {
            	valuesMap.put(tagName, value);
            }
        }
        return valuesMap;
    }

    public static Object evaluateFormula(String formula, Map<String, String> valuesMap, String jisuan_info, StringBuffer loggerStringBuffer) throws Exception {
    	String logger_info = "计算逻辑: \n" + formula;
        logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);
    	int i = 0;
    	String key = "[A-Z]{3}\\d{5}";
    	if (formula.contains("【") && formula.contains("】")) {
    		key = "【(.*?)】";
    		i = 1;
    	}


        // 提取所有标签名
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(formula);

        // 替换公式中的标签为实际值
        StringBuilder modifiedFormula = new StringBuilder(formula);
        int lastEnd = 0;

        while (matcher.find(lastEnd)) {
            String tagName;
			if (i == 0) {
				tagName = matcher.group();
			} else {
				tagName = matcher.group(i);
			}


//        	if (tagName.contains("【") && tagName.contains("】")) {
//        		tagName = tagName.replace("", modifiedFormula)"【(.*?)】";
//        	}

            Object value = valuesMap.getOrDefault(tagName, ""); // 未找到的标签默认值为 0

            if ("DED00020".equals(tagName)) {
            	tagName=tagName;
            }
	    	logger_info = "计算value[" + tagName + "]: " + value;
	        logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

            int start = matcher.start();
            int end = matcher.end();

            //TODO
            // 确保数值替换不会引起运算符冲突，数值前后加上空格
			if (i == 0 && !jisuan_info.contains("日本纪年")) {
				modifiedFormula.replace(start, end, " " + value + " ");

            } else {
            	modifiedFormula.replace(start, end, "" + value + "");

            }


            lastEnd = start + String.valueOf(value).length() + 2; // 更新位置
            matcher.reset(modifiedFormula.toString());
        }
        formula = modifiedFormula.toString();

        Object result = null;
        try {

        	if (formula.contains("@")) {
		        String[] formulaLines = formula.split("@"); // 按行分割公式字符串
		        formula = formulaLines[0];
		        for (String line : formulaLines) {
		            line = line.trim(); // 去除行首尾的空白
		            if (!line.isEmpty()) {
		               	if (line.contains("全角")) {
		               		int size = Integer.parseInt(line.replace("全角", ""));
		               		formula = FuncUtils.toFullWidthAndTruncate(formula, size);

		            	}
		            }
		        }

		        result = formula;

        	} else {
                // 使用 JavaScript 引擎计算结果
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
                // 修改公式：如果除数为 0，则结果为 0
                result = engine.eval(modifiedFormula.toString().replaceAll("(\\d+)\\s*/\\s*0", "0").trim());
        	}

        } catch (Exception e) {
//            e.printStackTrace();

        	jisuan_info = jisuan_info + "\n\t"  + e;
        	Exception newException = new Exception(jisuan_info, e);
        	throw newException; // 抛出新的异常，包含修改后的消息和原始异常作为原因

        }

    	logger_info = "计算结果: " + result;
        logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);
        return result;
    }

    public static void main(String[] args) {





    	//					1			2			11			1001
        String formula = "(AAJ00120 + AAK00080) - (AAJ00090 + AAJ00130 + AAK00050 + AAK00090)";

    	//				11
//        formula = "AAJ00130<0 ? 0 : AAJ00090";

        formula = "Math.ceil(1000/ 1000) * 1000";

    	String key = "";
    	String value = "";
//    	String result = "";
//    	double result = 0;
    	Object result;

        try {

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

            String jsCode = ""
                + "if (8 < 7) {"
                + "    0;"
                + "}else {"
                + "    1;"
                + "}";

            result = engine.eval(jsCode);

            System.out.println("jsCode: " + jsCode);  // 输出: 1
            System.out.println("结果: " + result);  // 输出: 1

			jsCode = ""
					+ "if ('abc' === 'abc' && 'abc' === 'abc') {    1;}\r\n"
					+ "else if ('abc' === 'abc') {    1;}\r\n"
					+ "else {    1;}";

			result = engine.eval(jsCode);

			System.out.println("jsCode: " + jsCode); // 输出: 1
			System.out.println("结果: " + result); // 输出: 1


//        	key = "T";
//        	value = "B";
//            formula = " ('' == '') ? '' : '<gen:hojinbango></gen:hojinbango>' ";
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent, "");



//        	key = "T";
//        	value = "B";
//            formula = " ''     ('<gen:hojinbango>' + ('').replace('T', '') + '</gen:hojinbango>')";
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent, "");






//
//            String dateString = "20241109";
//
//            // 创建 JavaScript 引擎
//            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
//
//            // 定义 JavaScript 脚本代码
//
//
//            // 定义 JavaScript 脚本代码，用正则表达式提取年份
//            String script = "var dateString = '" + dateString + "'; "
//                          + "dateString.replace(/^(\\d{4}).*/, '$1');";
//
//            script = "\"20241104\".match(/^(\\d{4})\\d{2}\\d{2}/)[1]";
////            script = "\"20241104\".match(/\\d{4}(\\d{2})\\d{2}/)[1]";
////            script = "\"20241104\".match(/\\d{6}(\\d{2})/)[1]";
//
//            try {
//                // 执行 JavaScript 代码并获取返回值
//                Object result1 = engine.eval(script);
//                logger.info("Extracted month: " + result1);
//            } catch (ScriptException e) {
//                e.printStackTrace();
//            }







			//最大处理999,999,999,999,999
			//最大处理999999999999999
//        	key = "百円未満切捨て";
//        	value = "-999999999999999";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//
//
//
//        	key = "百円未満切捨て";
//        	value = "-99999";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//
//
//
//        	key = "千円未満切捨て";
//        	value = "999999999999999";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//
//        	key = "千円未満切捨て";
//        	value = "99999";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//
//        	key = "マイナスは「0」表示";
//        	value = "999999999999999";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//
//
//
//
//        	key = "千円";
//        	value = "-999999999999999";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//
//
//
//
//        	key = "千円";
//        	value = "999999999999999999";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//
//
//
//
//        	key = "一円未満切捨て";
//        	value = "99999.999";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//
//
//
//        	key = "一円未満切捨て";
//        	value = "-99999.999";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);


//
//        	key = "%";
//        	value = "-0.123456";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//

//        	key = "小数点第3位以下切捨て";
//        	value = "0.66666666";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent,"", new StringBuffer());
//

//        	key = "小数点以下切捨て";
//        	value = "660*7.8/110";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent,"");


//        	key = "小数点第3位以下切捨て";
//        	value = "0.03";
//            formula = "Number('【】') === 0 ? 0 : 100";
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent,"", new StringBuffer());



//        	元号
//        	年
//        	月
//        	日


        	key = "日本纪年xtx";
        	value = "20241104";
        	value = "20290501";
        	value = "20170331";
        	String year = "2019";
        	String month  = "3";
            formula = roundingRules.get(key);
            formula = formula.replace("【】", value);
            formula = formula.replace("【year】", year);
            formula = formula.replace("【month】", month);
            logger.debug("["+key+"]计算value: " + value);
            result = calculate(formula, xmlContent, "", new StringBuffer());

//        	key = "年";
//        	value = "20241104";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent, "", new StringBuffer());

//
//
//        	key = "月";
//        	value = "20241104";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);
//
//
//        	key = "日";
//        	value = "20241104";
//            formula = roundingRules.get(key);
//            formula = formula.replace("【】", value);
//            logger.debug("["+key+"]计算value: " + value);
//            result = calculate(formula, xmlContent);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Map<String, String> roundingRules = new LinkedHashMap<>();

    public XMLCalculator() {
    	/*
    	 * 自定义计算公式
    	 */
    	roundingRules.put("小数点第3位以下切り捨て", "Math.floor(【】*1000)/1000");
    	roundingRules.put("小数点以下切り捨て", "Math.floor(【】).toFixed(0)");
    	roundingRules.put("千円未満切り捨て", "(【】 >= 0) ? Math.floor(【】 / 1000) * 1000 : Math.ceil(【】 / 1000) * 1000");//trunc
    	roundingRules.put("百円未満切り捨て", "(【】 >= 0) ? Math.floor(【】 / 100) * 100 : Math.ceil(【】 / 100) * 100");//trunc
        roundingRules.put("一円未満切り捨て", "(【】 >= 0) ? Math.floor(【】) : Math.ceil(【】)"); // trunc
        roundingRules.put("未満切り捨て", "(【】 >= 0) ? Math.floor(【】) : Math.ceil(【】)"); // trunc

        //别名
        roundingRules.put("小数点第3位以下切捨て", roundingRules.get("小数点第3位以下切り捨て"));
        roundingRules.put("小数点以下切捨て", roundingRules.get("小数点以下切り捨て"));
        roundingRules.put("千円未満切捨て", roundingRules.get("千円未満切り捨て"));
        roundingRules.put("百円未満切捨て", roundingRules.get("百円未満切り捨て"));
        roundingRules.put("一円未満切捨て", roundingRules.get("一円未満切り捨て"));
        roundingRules.put("未満切捨て", roundingRules.get("未満切り捨て"));


        roundingRules.put("マイナスは「0」表示", "【】 < 0 ? 0 : 【】");
        roundingRules.put("千円", "Math.ceil(【】 / 1000) * 1000");
        /*
"1:明治
2:大正
3:昭和
4:平成
5:令和"
         */
        String wareki = ""
                + "var yyyymmdd = '【】';\n"
                + "var year = parseInt(yyyymmdd.substring(0, 4), 10);\n"
                + "var month = parseInt(yyyymmdd.substring(4, 6), 10);\n"
                + "var day = parseInt(yyyymmdd.substring(6, 8), 10);\n"
                + "var era = '', eraYear = 0;\n"
                + "if (year > 2019 || (year === 2019 && month >= 5)) {\n"
                + "    era = '5';\n"
                + "    eraYear = year - 2018;\n"
                + "} else if (year > 1989 || (year === 1989 && month >= 1)) {\n"
                + "    era = '4';\n"
                + "    eraYear = year - 1988;\n"
                + "} else if (year > 1926 || (year === 1926 && month >= 12)) {\n"
                + "    era = '3';\n"
                + "    eraYear = year - 1925;\n"
                + "} else if (year > 1912 || (year === 1912 && month >= 7)) {\n"
                + "    era = '2';\n"
                + "    eraYear = year - 1911;\n"
                + "} else if (year >= 1868) {\n"
                + "    era = '1';\n"
                + "    eraYear = year - 1867;\n"
                + "} else {\n"
                + "    era = '不明';\n"
                + "    eraYear = 0;\n"
                + "}\n"
                ;

        roundingRules.put("日本纪年ALL", wareki
                + "era + '_' + (eraYear === 1 ? '元' : eraYear) + '';"
        		);

        roundingRules.put("日本纪年_年号", wareki
                + "era;"
        		);

        roundingRules.put("日本纪年_年", wareki
                + " (eraYear === 1 ? '元' : eraYear) + '';"
        		);

        roundingRules.put("日本纪年xtx", wareki
                + "        '<gen:era>' + era + '</gen:era>'"
                + "        +'<gen:yy>' + eraYear + '</gen:yy>'"
                + "        +'<gen:mm>' + month + '</gen:mm>'"
                + "        +'<gen:dd>' + day + '</gen:dd>'"
                + ";"
        		);




//        roundingRules.put("元号", "\"20241104\".match(/^(\\d{4})\\d{2}\\d{2}/)[1]");
//        roundingRules.put("年", "\"20241104\".match(/^(\\d{4})\\d{2}\\d{2}/)[1] - 2018");
//        roundingRules.put("月", "\"20241104\".match(/\\d{4}(\\d{2})\\d{2}/)[1]");
//        roundingRules.put("日", "\"20241104\".match(/\\d{6}(\\d{2})/)[1]");


//        roundingRules.put("千円", "Math.ceil(BigInt(【】) / BigInt(1000)) * BigInt(1000)");


        /*
         *

对比总结

    Math.floor 总是“向下”舍入，例如 5.8 变为 5.0，-5.8 变为 -6.0。
    Math.ceil 总是“向上”舍入，例如 5.2 变为 6.0，-5.2 变为 -5.0。


1. Math.floor(double a)

    功能：将传入的浮点数 a 向下舍入到最接近的整数。
    原理：不管 a 是正数还是负数，Math.floor 总是返回小于或等于 a 的最大整数。
    结果类型：返回值是 double 类型。

logger.info(Math.floor(5.8));  // 输出 5.0
logger.info(Math.floor(5.0));  // 输出 5.0
logger.info(Math.floor(-5.8)); // 输出 -6.0

2. Math.ceil(double a)

    功能：将传入的浮点数 a 向上舍入到最接近的整数。
    原理：无论 a 是正数还是负数，Math.ceil 总是返回大于或等于 a 的最小整数。
    结果类型：返回值是 double 类型。

logger.info(Math.ceil(5.2));   // 输出 6.0
logger.info(Math.ceil(5.0));   // 输出 5.0
logger.info(Math.ceil(-5.2));  // 输出 -5.0
         *
         *
         */
    }

//    public static void main(String[] args) {
//        String formula = "AAJ00090 < 0 ? 0 : AAJ00090";
//        Map<String, Integer> valuesMap = new HashMap<>();
//        valuesMap.put("AAJ00090", -5);
//
//        try {
//            int result = evaluateFormula(formula, valuesMap);
//            logger.debug("计算结果: " + result); // 结果应为 0
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


     // 替换为实际 XML 内容
    static  String xmlContent = ""
       		+ ""
       		+ "  <SHA010 VR=\"10.0\" id=\"SHA010-1\" page=\"1\">"
       		+ "        <SHA010-1 page=\"1\">"
       		+ "            <AAI00000>"
       		+ "                <AAI00010 IDREF=\"TEISYUTSU_DAY\"></AAI00010>"
       		+ "                <AAI00020 IDREF=\"ZEIMUSHO\"></AAI00020>"
       		+ "                <AAI00030 IDREF=\"NOZEISHA_ADR\"></AAI00030>"
       		+ "                <AAI00040 IDREF=\"NOZEISHA_TEL\"></AAI00040>"
       		+ "                <AAI00050>"
       		+ "                    <AAI00060 IDREF=\"NOZEISHA_NM_KN\"></AAI00060>"
       		+ "                    <AAI00070 IDREF=\"NOZEISHA_NM\"></AAI00070>"
       		+ "                </AAI00050>"
       		+ "                <AAI00080 IDREF=\"NOZEISHA_BANGO\"></AAI00080>"
       		+ "                <AAI00090>"
       		+ "                    <AAI00100 IDREF=\"DAIHYO_NM_KN\"></AAI00100>"
       		+ "                    <AAI00110 IDREF=\"DAIHYO_NM\"></AAI00110>"
       		+ "                </AAI00090>"
       		+ "                <AAI00115><kubun_CD>2</kubun_CD></AAI00115>"
       		+ "                <AAI00120>"
       		+ "                    <AAI00130 IDREF=\"KAZEI_KIKAN_FROM\"></AAI00130>"
       		+ "                    <AAI00140 IDREF=\"KAZEI_KIKAN_TO\"></AAI00140>"
       		+ "                </AAI00120>"
       		+ "                <AAI00150 IDREF=\"SHINKOKU_KBN\"></AAI00150>"
       		+ "                <AAI00160>"
       		+ "                    <AAI00170></AAI00170>"
       		+ "                    <AAI00180></AAI00180>"
       		+ "                </AAI00160>"
       		+ "                <AAI00190 IDREF=\"KANPU_KINYUKIKAN\"></AAI00190>"
       		+ "                <AAI00250>2</AAI00250>"
       		+ "                <AAI00200>"
       		+ "                    <AAI00210 IDREF=\"DAIRI_NM\"></AAI00210>"
       		+ "                    <AAI00220 IDREF=\"DAIRI_TEL\"></AAI00220>"
       		+ "                </AAI00200>"
       		+ "                <AAI00230>1</AAI00230>"
       		+ "                <AAI00240>2</AAI00240>"
       		+ "            </AAI00000>"
       		+ "            <AAJ00000>"
       		+ "                <AAJ00010>keshuibiao_zhun_e</AAJ00010>"
       		+ "                <AAJ00020>xiaofeishui_e_guoshui_bufen</AAJ00020>"
       		+ "                <AAJ00030>0</AAJ00030>"
       		+ "                <AAJ00040>"
       		+ "                    <AAJ00050></AAJ00050>"
       		+ "                    <AAJ00060>0</AAJ00060>"
       		+ "                    <AAJ00070>0</AAJ00070>"
       		+ "                    <AAJ00080></AAJ00080>"
       		+ "                </AAJ00040>"
       		+ "                <AAJ00090>11</AAJ00090>"
       		+ "                <AAJ00100></AAJ00100>"
       		+ "                <AAJ00110>null</AAJ00110>"
       		+ "                <AAJ00120>1</AAJ00120>"
       		+ "                <AAJ00130>1001</AAJ00130>"
       		+ "                <AAJ00140>"
       		+ "                    <AAJ00150>0</AAJ00150>"
       		+ "                    <AAJ00160>0</AAJ00160>"
       		+ "                </AAJ00140>"
       		+ "                <AAJ00170>"
       		+ "                    <AAJ00180></AAJ00180>"
       		+ "                    <AAJ00190></AAJ00190>"
       		+ "                </AAJ00170>"
       		+ "            </AAJ00000>"
       		+ "            <AAK00000>"
       		+ "                <AAK00010>"
       		+ "                    <AAK00020>等于AAJ00090</AAK00020>"
       		+ "                    <AAK00030>等于AAJ00100</AAK00030>"
       		+ "                </AAK00010>"
       		+ "                <AAK00040>"
       		+ "                    <AAK00050>【全年应缴消费税地方税部分】＜0时填入</AAK00050>"
       		+ "                    <AAK00060>【全年应缴消费税地方税部分】≥0时填入</AAK00060>"
       		+ "                </AAK00040>"
       		+ "                <AAK00070>null</AAK00070>"
       		+ "                <AAK00080>2</AAK00080>"
       		+ "                <AAK00090></AAK00090>"
       		+ "                <AAK00100>"
       		+ "                    <AAK00110>0</AAK00110>"
       		+ "                    <AAK00120>0</AAK00120>"
       		+ "                </AAK00100>"
       		+ "                <AAK00130>AAK00130＝（AAJ00120＋AAK00080）－（AAJ00090＋AAJ00130＋AAK00050＋AAK00090）</AAK00130>"
       		+ "            </AAK00000>"
       		+ "            <AAL00000>"
       		+ "                <AAL00010>2</AAL00010>"
       		+ "                <AAL00020>2</AAL00020>"
       		+ "                <AAL00030>2</AAL00030>"
       		+ "                <AAL00040>2</AAL00040>"
       		+ "            </AAL00000>"
       		+ "            <AAM00000>"
       		+ "                <AAM00010>2</AAM00010>"
       		+ "                <AAM00020>客户选择法人一般原则时等于3，选择原则2割时等于8</AAM00020>"
       		+ "                <AAM00030>【本申告主体在2022年1月1日~2022年12月31日的日本课税销售额是】的数字保留到千位，即123,456,789变成123,457,000</AAM00030>"
       		+ "            </AAM00000>"
       		+ "            <AAX00000>2</AAX00000>"
       		+ "            <AAY00000>2</AAY00000>"
       		+ "        </SHA010-1>"
       		+ "        <SHA010-2 page=\"1\">"
       		+ "            <AAN00000>"
       		+ "                <AAN00010 IDREF=\"NOZEISHA_ADR\"></AAN00010>"
       		+ "                <AAN00020 IDREF=\"NOZEISHA_TEL\"></AAN00020>"
       		+ "                <AAN00030>"
       		+ "                    <AAN00040 IDREF=\"NOZEISHA_NM_KN\"></AAN00040>"
       		+ "                    <AAN00050 IDREF=\"NOZEISHA_NM\"></AAN00050>"
       		+ "                </AAN00030>"
       		+ "                <AAN00060>"
       		+ "                    <AAN00070 IDREF=\"DAIHYO_NM_KN\"></AAN00070>"
       		+ "                    <AAN00080 IDREF=\"DAIHYO_NM\"></AAN00080>"
       		+ "                </AAN00060>"
       		+ "                <AAN00090>"
       		+ "                    <AAN00100 IDREF=\"KAZEI_KIKAN_FROM\"></AAN00100>"
       		+ "                    <AAN00110 IDREF=\"KAZEI_KIKAN_TO\"></AAN00110>"
       		+ "                </AAN00090>"
       		+ "                <AAN00120 IDREF=\"SHINKOKU_KBN\"></AAN00120>"
       		+ "                <AAN00130>"
       		+ "                    <AAN00140></AAN00140>"
       		+ "                    <AAN00150></AAN00150>"
       		+ "                </AAN00130>"
       		+ "            </AAN00000>"
       		+ "            <AAO00000>"
       		+ "                <AAO00010>2</AAO00010>"
       		+ "                <AAO00020>2</AAO00020>"
       		+ "            </AAO00000>"
       		+ "            <AAP00000>keshuibiao_zhun_e</AAP00000>"
       		+ "            <AAQ00000>"
       		+ "                <AAQ00010>0</AAQ00010>"
       		+ "                <AAQ00020>0</AAQ00020>"
       		+ "                <AAQ00030>0</AAQ00030>"
       		+ "                <AAQ00040>0</AAQ00040>"
       		+ "                <AAQ00050></AAQ00050>"
       		+ "                <AAQ00060></AAQ00060>"
       		+ "            </AAQ00000>"
       		+ "            <AAR00000>"
       		+ "                <AAR00010>0</AAR00010>"
       		+ "                <AAR00020>0</AAR00020>"
       		+ "                <AAR00030>0</AAR00030>"
       		+ "            </AAR00000>"
       		+ "            <AAS00000></AAS00000>"
       		+ "            <AAT00000>"
       		+ "                <AAT00010>0</AAT00010>"
       		+ "                <AAT00020>0</AAT00020>"
       		+ "                <AAT00030>0</AAT00030>"
       		+ "                <AAT00040>0</AAT00040>"
       		+ "                <AAT00050>xiaofeishui_e_guoshui_bufen</AAT00050>"
       		+ "            </AAT00000>"
       		+ "            <AAU00000>0</AAU00000>"
       		+ "            <AAV00000>"
       		+ "                <AAV00010>0</AAV00010>"
       		+ "                <AAV00020>0</AAV00020>"
       		+ "            </AAV00000>"
       		+ "            <AAW00000>"
       		+ "                <AAW00010></AAW00010>"
       		+ "                <AAW00020>0</AAW00020>"
       		+ "                <AAW00030>0</AAW00030>"
       		+ "                <AAW00040></AAW00040>"
       		+ "            </AAW00000>"
       		+ "        </SHA010-2>"
       		+ "    </SHA010>"
       		+ ""
       		+ ""
       		+ ""
       		+ ""
       		+ "";
}
