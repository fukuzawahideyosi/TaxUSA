package com.panda.utils;


public class XmlConverter2 {

    // Method to convert escaped XML content to actual XML format
    public static String convertEscapedToXml(String escapedXml) {
        if (escapedXml == null || escapedXml.isEmpty()) {
            return escapedXml;
        }

        return escapedXml
            .replace("&amp;lt;", "<")
            .replace("&amp;gt;", ">")
            .replace("&amp;quot;", "\"")
            .replace("&apos;", "'")
//            .replace("&amp;", "&")
            ;
    }

    // Method to convert actual XML content back to escaped format
    public static String convertXmlToEscaped(String xmlContent) {
        if (xmlContent == null || xmlContent.isEmpty()) {
            return xmlContent;
        }

        return xmlContent
//            .replace("&", "&amp;")
            .replace("<", "&amp;lt;")
            .replace(">", "&amp;gt;")
            .replace("\"", "&amp;quot;")
            .replace("'", "&apos;")
            ;
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        String escapedXml = "&lt;reportValue reportId=\"SHA010\" reportName=\"消費税及び地方消費税の申告書(一般用)\" leafId=\"\" page=\"1\"&gt;";
        String actualXml = "<reportValue reportId=\"SHA010\" reportName=\"消費税及び地方消費税の申告書(一般用)\" leafId=\"\" page=\"1\">";

        // Convert escaped to actual XML
        String convertedToXml = convertEscapedToXml(escapedXml);
        System.out.println("Converted to XML: \n" + convertedToXml);

        // Convert actual XML back to escaped format
        String convertedToEscaped = convertXmlToEscaped(actualXml);
        System.out.println("Converted to Escaped XML: \n" + convertedToEscaped);
    }
}
