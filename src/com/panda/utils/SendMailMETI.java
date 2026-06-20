package com.panda.utils;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SendMailMETI {


	private static Logger logger = Logger.getLogger(SendMailMETI.class.toString());
//	private static Logger logger = Logger.getLogger("javax.mail");

    String host, port, emailid,username, password;
    Properties props = System.getProperties();
    Session l_session = null;

    public SendMailMETI() {

        host = "smtp.qiye.aliyun.com";
        port = "465";
        emailid = "meti_contact@pandaservicejapan.com";
        username = "meti_contact@pandaservicejapan.com";
        password = "5g.8wYzYpyt342@";


        emailid = "meti_directmail@pandaservicejapan.com";
        username = "meti_directmail@pandaservicejapan.com";
        password = "M4nB78xQk2Rs";



        emailSettings();
        createSession();
//        sendMessage("w12345678qa@yahoo.co.jp", "43936834@qq.com","Test subject","Test mail with some random text");
    }

    public void emailSettings() {
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
//        props.put("mail.debug", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
        props.put("mail.smtp.cc", "Sent"); // 设置已发送文件夹的路径，可以根据实际需要修改

        props.put("mail.logging", "true"); // 确保日志与 Log4j 配合



     // SMTP 基本
     props.put("mail.transport.protocol", "smtp");
     props.put("mail.smtp.host", "smtp.qiye.aliyun.com");
//     props.put("mail.smtp.port", "587");

     // 认证
     props.put("mail.smtp.auth", "true");

     // STARTTLS（重点）
     props.put("mail.smtp.starttls.enable", "true");
     props.put("mail.smtp.starttls.required", "true");

     // 超时（很重要）
     props.put("mail.smtp.connectiontimeout", "10000");
     props.put("mail.smtp.timeout", "10000");
     props.put("mail.smtp.writetimeout", "10000");

     // 调试（排错时打开）
//     props.put("mail.debug", "true");


    }


    public void createSession() {
        l_session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });




    }

    public boolean sendMessage(String emailFromUser, String toEmail, String bccEmail, String subject, String messageText) throws Exception {
        try {

			logger.info("toEmail " + toEmail);
			logger.info("bccEmail " + bccEmail);


			// 获取系统类型属性
			String osName = System.getProperty("os.name");
			// 您可以根据不同的系统类型执行不同的操作
//			if (osName.toLowerCase().contains("windows")) {
//				logger.info("这是Windows系统");
//				// 在Windows系统上执行特定操作
//
//				toEmail = "43936834@qq.com";
//				if(!StringUtils.isEmpty(bccEmail)) {
//					bccEmail = "lixiweb@yahoo.co.jp";
//				}
//
//				logger.info("toEmail " + toEmail);
//				logger.info("bccEmail " + bccEmail);
//
//			} else if (osName.toLowerCase().contains("linux")) {
//				logger.info("这是Linux系统");
//				// 在Linux系统上执行特定操作
//			}




            MimeMessage message = new MimeMessage(l_session);
            if(StringUtils.isEmpty(emailFromUser)) {
            	emailFromUser = emailid;
            }
            message.setFrom(new InternetAddress(emailFromUser));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
//            message.addRecipient(Message.RecipientType.CC, new InternetAddress("fukuzawahideyosi@pandaservicejapan.com"));

            if (StringUtils.isEmpty(bccEmail) == true) {

            } else {
            	message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccEmail));
            }





            message.setSubject(subject);
            message.setText(messageText.replaceAll("<br>", "\n"));
//            message.setContent(messageText, "text/html;charset=UTF-8");

            props.setProperty("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

            Transport.send(message);
    		logger.debug("An email has been sent," + toEmail);
//        } catch (MessagingException mex) {
//            mex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }




    public static void main(String[] args) {
        final String username = "your_email@gmail.com"; // 发件人的邮箱
        final String password = "your_password"; // 发件人的密码

        // SMTP服务器信息
        String host = "smtp.gmail.com"; // 例如，Gmail的SMTP服务器
        int port = 587; // Gmail的SMTP端口

        // 收件人的信息
        String toEmail = "recipient@example.com"; // 收件人的邮箱
        String subject = "邮件主题";
        String messageText = "这是一封带有附件的邮件";

        // 指定附件文件夹的路径
        String attachmentFolderPath = "path_to_attachment_folder"; // 指定附件文件夹的路径

        // 创建Properties对象，设置SMTP服务器的相关属性
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // 创建Session对象，用于与SMTP服务器的通信
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 创建MimeMessage对象
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // 创建Multipart对象，用于组合文本和附件
            Multipart multipart = new MimeMultipart();

            // 创建文本部分
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(messageText);

            // 将文本部分添加到Multipart对象
            multipart.addBodyPart(messageBodyPart);

            // 获取附件文件夹中的所有文件并添加为附件
            File folder = new File(attachmentFolderPath);
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(file.getAbsolutePath());
                        attachmentBodyPart.setDataHandler(new DataHandler(source));
                        attachmentBodyPart.setFileName(file.getName());
                        multipart.addBodyPart(attachmentBodyPart);
                    }
                }
            }

            // 将Multipart对象设置为邮件的内容
            message.setContent(multipart);

            // 发送邮件
            Transport.send(message);

            logger.info("邮件发送成功！");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}