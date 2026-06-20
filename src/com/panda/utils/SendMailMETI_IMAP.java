package com.panda.utils;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;

import org.apache.log4j.Logger;


public class SendMailMETI_IMAP {


	private static Logger logger = Logger.getLogger(SendMailMETI_IMAP.class.toString());
//	private static Logger logger = Logger.getLogger("javax.mail");

//    String host, port, emailid,username, password;
//    static Properties props = System.getProperties();
//    Session l_session = null;
//
//    public SendMailMETI_IMAP() {
//
//        host = "smtp.qiye.aliyun.com";
//        port = "465";
//        emailid = "meti_contact@pandaservicejapan.com";
//        username = "meti_contact@pandaservicejapan.com";
//        password = "5g.8wYzYpyt342@";
//
//    }



    // ===== 配置区 =====
    private static String IMAP_HOST = "imap.qiye.aliyun.com";
    private static String USERNAME  = "meti_contact@pandaservicejapan.com"; // 改成你的邮箱
    private static String PASSWORD  = "5g.8wYzYpyt342@";
    // ==================


    public static void main(String[] args) {


        USERNAME  = "meti_contact@pandaservicejapan.com"; // 改成你的邮箱
        PASSWORD  = "5g.8wYzYpyt342@";

        USERNAME = "meti_directmail@pandaservicejapan.com";
        PASSWORD = "M4nB78xQk2Rs";


        Store store = null;
        Folder inbox = null;

        try {
            // === TLS 强制（你这一步是对的，保留）===
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("mail.imaps.ssl.protocols", "TLSv1.2");

            // === IMAP 配置 ===
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", "imap.qiye.aliyun.com");
            props.put("mail.imaps.port", "993");
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.imaps.ssl.protocols", "TLSv1.2");
            props.put("mail.imaps.connectiontimeout", "10000");
            props.put("mail.imaps.timeout", "10000");

            Session session = Session.getInstance(props);
            store = session.getStore("imaps");
            store.connect(IMAP_HOST, USERNAME, PASSWORD);

            // === 打开收件箱 ===
            inbox = store.getFolder("INBOX");


            // 👉 打开“已发送”文件夹（阿里云通常是这个）
//            inbox = store.getFolder("Sent Messages");


            inbox.open(Folder.READ_ONLY);

            // === 时间区间（注意：Calendar 月份从 0 开始）===
            Calendar start = Calendar.getInstance();
            start.set(2026, Calendar.JANUARY, 1, 0, 0, 0);

            Calendar end = Calendar.getInstance();
            end.set(2026, Calendar.JANUARY, 31, 23, 59, 59);

            // ⭐⭐⭐ 阿里云 IMAP 唯一稳定 search 写法：只用日期 ⭐⭐⭐
            SearchTerm dateRange = new AndTerm(
                    new SentDateTerm(ComparisonTerm.GE, start.getTime()),
                    new SentDateTerm(ComparisonTerm.LE, end.getTime())
            );

            Message[] messages = inbox.getMessages();
            logger.info("命中邮件总数：" + messages.length);

            int count = 0;
            // === 本地判断失败邮件（绝对不要放进 search）===
            for (Message msg : messages) {

                if (!isFailedMail(msg)) {
//                    continue;
                }

//                logger.info("\n==============================");
//                logger.info("【发送失败邮件】");
//                logger.info("时间: " + msg.getSentDate());
                logger.info(++count + "主题: " + decode(msg.getSubject()));
//                logger.info("发件人: " + addressToString(msg.getFrom()));
//                logger.info("---- 正文 ----");


                String mailContent = getText(msg);
//                logger.info(mailContent);


                BounceResult result = parseBounce(mailContent);

				Object content = msg.getContent();
				if (content instanceof Multipart) {
					result.gongsi_ming = extractName(handleMultipart((Multipart) content));
				}

				logger.info(result.reason + "," + result.gongsi_ming + "," + result.recipient);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (inbox != null && inbox.isOpen()) inbox.close(false); } catch (Exception ignored) {}
            try { if (store != null && store.isConnected()) store.close(); } catch (Exception ignored) {}
        }
    }

    public static String extractName(String text) {
        if (text == null) return null;

        String flag = "，您好";
        int idx = text.indexOf(flag);
        if (idx == -1) {
            return null; // 没找到
        }
        return text.substring(0, idx).trim();
    }
    /** 递归处理 Multipart
     * @return */
    private static String handleMultipart(Multipart multipart) throws Exception {

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);

            // 嵌套 multipart
            if (part.getContent() instanceof Multipart) {
                handleMultipart((Multipart) part.getContent());
                continue;
            }

            // 只处理附件
            String disposition = part.getDisposition();
            if (!Part.ATTACHMENT.equalsIgnoreCase(disposition)) {
                continue;
            }

            String fileName = decode(part.getFileName());
//            System.out.println("发现附件：" + fileName);

            String attachmentText = readPart(part);
            return parseMailTextAttachment(attachmentText);
        }
		return null;
    }

    /** 读取附件内容 */
    private static String readPart(BodyPart part) throws Exception {
        InputStream is = part.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buf = new byte[4096];
        int len;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return baos.toString(StandardCharsets.UTF_8.name());
    }

    /** 解析你给的那种“邮件文本附件”
     * @return */
    private static String parseMailTextAttachment(String text) throws Exception {

        String[] lines = text.split("\\r?\\n");

        boolean bodyStart = false;
        StringBuilder base64Body = new StringBuilder();

//        System.out.println("------ 附件内容解析 ------");

        for (String line : lines) {

            if (line.startsWith("Subject:")) {
                String subject = line.substring(8).trim();
//                System.out.println("附件内 Subject：" + decode(subject));
            }

            // 空行后是 base64 正文
            if (bodyStart) {
                base64Body.append(line.trim());
            }

            if (line.trim().isEmpty()) {
                bodyStart = true;
            }
        }

        if (base64Body.length() > 0) {
            byte[] decoded = Base64.getDecoder().decode(base64Body.toString());
            String body = new String(decoded, StandardCharsets.UTF_8);

//            System.out.println("------ 解码后的正文 ------");
//            System.out.println(body);
            return body;
        } else {
            System.out.println("⚠ 未检测到 Base64 正文");
        }
		return null;

    }


    public static class BounceResult {
        public String recipient;
        public String systemReply;
        public String reason;
        public String suggestion;
        public String gongsi_ming;

        @Override
        public String toString() {
            return "收件人: " + recipient + "\n"
                 + "系统应答: " + systemReply + "\n"
                 + "退信原因: " + reason + "\n"
                 + "解决建议: " + suggestion;
        }
    }

    public static BounceResult parseBounce(String text) {
        BounceResult r = new BounceResult();

        // 1️⃣ 收件人
        Matcher m1 = Pattern.compile("无法发送到\\s+([\\w._%+-]+@[\\w.-]+)")
                .matcher(text);
        if (m1.find()) {
            r.recipient = m1.group(1);
        }

        // 2️⃣ 系统应答
        Matcher m2 = Pattern.compile("系统应答[:：](.+)")
                .matcher(text);
        if (m2.find()) {
            r.systemReply = m2.group(1).trim();
        }

        // 3️⃣ 退信原因
        Matcher m3 = Pattern.compile("退信原因[:：](.+)")
                .matcher(text);
        if (m3.find()) {
            r.reason = m3.group(1).trim();
        }

        // 4️⃣ 解决建议
        Matcher m4 = Pattern.compile("解决建议[:：](.+)")
                .matcher(text);
        if (m4.find()) {
            r.suggestion = m4.group(1).trim();
        }

        return r;
    }

    /**
     * 判断是否为退信 / 发送失败
     */
    private static boolean isFailedMail(Message msg) throws Exception {
        String subject = decode(msg.getSubject());

        if (subject != null) {
            String s = subject.toLowerCase();
            if (s.contains("delivery")
                    || s.contains("failed")
                    || s.contains("undelivered")
                    || s.contains("投递失败")
                    || s.contains("无法投递")) {
                return true;
            }
        }

        Address[] from = msg.getFrom();
        if (from != null) {
            for (Address addr : from) {
                String f = addr.toString().toLowerCase();
                if (f.contains("mailer-daemon") || f.contains("postmaster")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 解析邮件正文（支持 multipart）
     */
    private static String getText(Part part) throws Exception {
        if (part.isMimeType("text/plain")) {
            return part.getContent().toString();
        }
        if (part.isMimeType("text/html")) {
            return part.getContent().toString();
        }
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mp.getCount(); i++) {
                sb.append(getText(mp.getBodyPart(i))).append("\n");
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * 处理中文主题乱码
     */
    private static String decode(String text) {
        try {
            return text == null ? "" : MimeUtility.decodeText(text);
        } catch (Exception e) {
            return text;
        }
    }

    private static String addressToString(Address[] addrs) {
        if (addrs == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Address a : addrs) {
            sb.append(a.toString()).append(" ");
        }
        return sb.toString();
    }


}