package com.panda.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlConverter {
    public static String formatXml(String inputXml) {
        try {
            // Parse the input XML string
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(inputXml.getBytes("UTF-8")));

            // Process the document
            StringBuilder outputBuilder = new StringBuilder();
            outputBuilder.append("<param name=\"帳票データ\">");
            outputBuilder.append("&lt;reportValue reportId=&quot;SHA010&quot; reportName=&quot;消費税及び地方消費税の申告書(一般用)&quot; leafId=&quot;&quot; page=&quot;1&quot;&gt;");

            NodeList pages = document.getElementsByTagName("SHA010-1");
            processPage(pages, outputBuilder, "SHA010-1");

            pages = document.getElementsByTagName("SHA010-2");
            processPage(pages, outputBuilder, "SHA010-2");

            outputBuilder.append("&lt;/reportValue&gt;");
            outputBuilder.append("</param>");
            return outputBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void processPage(NodeList pages, StringBuilder outputBuilder, String reportId) {
        for (int i = 0; i < pages.getLength(); i++) {
            Node page = pages.item(i);
            if (page.getNodeType() == Node.ELEMENT_NODE) {
                outputBuilder.append("&lt;page reportId=&quot;")
                        .append(reportId)
                        .append("&quot; reportName=&quot;&quot; status=&quot;0&quot;&gt;");

                processNode(page, outputBuilder);

                outputBuilder.append("&lt;/page&gt;");
            }
        }
    }

    private static void processNode(Node node, StringBuilder outputBuilder) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            outputBuilder.append("&amp;lt;")
                    .append(element.getTagName())
                    .append("&amp;gt;");

            // Process attributes
            NamedNodeMap attributes = element.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Node attr = attributes.item(j);
                outputBuilder.append(" ")
                        .append("&amp;quot;")
                        .append(attr.getNodeName())
                        .append("&amp;quot;=&amp;quot;")
                        .append(attr.getNodeValue())
                        .append("&amp;quot;");
            }

            // Process child nodes
            NodeList children = element.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                processNode(children.item(j), outputBuilder);
            }

            outputBuilder.append("&amp;lt;/")
                    .append(element.getTagName())
                    .append("&amp;gt;");
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            String content = node.getTextContent().trim();
            if (!content.isEmpty()) {
                outputBuilder.append(content);
            }
        }
    }

    public static void main(String[] args) {
        String inputXml = "<your_input_xml_here>";
        String formattedXml = formatXml(inputXml);
        System.out.println(formattedXml);
    }
}
