package com.panda.batch;


import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class test_xml {

	private static Logger logger = Logger.getLogger(test_xml.class.toString());

    public static void main(String[] args) {
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
        		+ "<itemclass name=\"キャビネット\" progId=\"nta.CLCCabinet.1\">\r\n"
        		+ "	<param name=\"申告等管理\">\r\n"
        		+ "		<container name=\"申告等管理\" progId=\"nta.CLCStatementManager.1\">\r\n"
        		+ "			<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"R５年度　消費税申告\">\r\n"
        		+ "				<itemclass name=\"申告等\" progId=\"nta.CLCStatement.1\">\r\n"
        		+ "					<param name=\"添付書類管理\">\r\n"
        		+ "						<container name=\"添付書類管理\" progId=\"nta.CLCAttachedManager.1\">\r\n"
        		+ "							<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(平成27年7月1日以降提出分)\">\r\n"
        		+ "							</item>\r\n"
        		+ "						</container>\r\n"
        		+ "					</param>\r\n"
        		+ "				</itemclass>\r\n"
        		+ "			</item>\r\n"
        		+ "		</container>\r\n"
        		+ "	</param>\r\n"
        		+ "	<param name=\"添付書類管理\">\r\n"
        		+ "		<container name=\"添付書類管理\" progId=\"nta.CLCAttachedManager.1\">\r\n"
        		+ "			<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(平成27年7月1日以降提出分)\">\r\n"
        		+ "			</item>\r\n"
        		+ "		</container>\r\n"
        		+ "	</param>\r\n"
        		+ "</itemclass>\r\n"
        		+ "";


//

//        // 解析XML
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        try {
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));
//
//            // 遍历XML文档的根节点
//            traverseNode(document.getDocumentElement());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//
//
//
//
//
//
//
//
//

//
//        // 解析XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

            // 查找路径为A.B.C的节点
//            Element targetNode = findNodeByPath(document.getDocumentElement(), "申告等管理");
            Element targetNode = findNodeByPath(document.getDocumentElement(), "キャビネット.申告等管理.申告等管理.R５年度　消費税申告");
//            Element targetNode = findNodeByPath(document.getDocumentElement(), "R５年度　消費税申告");

            // 在找到的节点下添加新的节点
            if (targetNode != null) {
                Element newNode = document.createElement("newNode");
                newNode.setAttribute("attribute1", "value1");
                newNode.setTextContent("Node Content");

                targetNode.appendChild(newNode);

                // 将修改后的XML转换为字符串
                String modifiedXml = convertDocumentToString(document);

                // 输出修改后的XML
                logger.info(modifiedXml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }





    }
    private static void traverseNode(Node node) {
        // 打印节点的信息（示例中只是打印节点的名称）
        logger.info("Node Name: " + node.getNodeName() + " " + node.getAttributes().getNamedItem("name"));

        // 检查节点是否有子节点
        if (node.hasChildNodes()) {
            NodeList childNodes = node.getChildNodes();

            // 遍历子节点
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);

                // 仅处理元素节点
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    traverseNode(childNode); // 递归调用
                }
            }
        }
    }








    private static Element findNodeByPath(Element parent, String path) {
        String[] pathSegments = path.split("\\.");

        Element currentNode = parent;
        for (String segment : pathSegments) {
            currentNode = getChildElement(currentNode, segment);
            if (currentNode == null) {
                return null; // 如果找不到任何子节点，返回null
            }
        }

        return currentNode;
    }

    private static Element getChildElement(Element parent, String nodeName) {
        NodeList childNodes = parent.getChildNodes();parent.getTextContent();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getAttributes() == null) {
            	continue;
            }
            logger.info("Node Name: " + node.getNodeName() + " " + node.getAttributes().getNamedItem("name"));
            if (node.getNodeType() == Node.ELEMENT_NODE && (nodeName).equals(node.getAttributes().getNamedItem("name"))) {
                return (Element) node;
            }
        }
        return null;
    }

    private static String convertDocumentToString(Document document) {
        // 与前面的代码相同，略去以节省空间
        // ...
        return null;
    }
}
