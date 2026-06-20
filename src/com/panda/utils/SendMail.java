package com.panda.utils;

import java.io.File;
import java.util.Properties;
import java.util.regex.Pattern;

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
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SendMail {


	private static Logger logger = Logger.getLogger(SendMail.class.toString());
//	private static Logger logger = Logger.getLogger("javax.mail");

    String host, port, emailid,username, password;
    Properties props = System.getProperties();
    Session l_session = null;

    public SendMail() {
//        host = "smtp.mail.yahoo.co.jp";
//        port = "465";
//        emailid = "w12345678qa@yahoo.co.jp";
//        username = "w12345678qa";
//        password = "100001Hy";


//      host = "mail44.onamae.ne.jp";
//      port = "465";
//      emailid = "info@japanetax.net";
//      username = "info@japanetax.net";
//      password = "pjsWgBYzaVv#3!3";

//        host = "smtp.qiye.aliyun.com";
//        port = "465";
//        emailid = "info@pandaservicejapan.com";
//        username = "info@pandaservicejapan.com";
//        password = "@yC8&BD-?-J_YW2";

    	//5g满了
//        host = "smtp.qiye.aliyun.com";
//        port = "465";
//        emailid = "mail2@pandaservicejapan.com";
//        username = "mail2@pandaservicejapan.com";
//        password = "@yC8&BD-?-J_YW2";

        host = "smtp.qiye.aliyun.com";
        port = "465";
        emailid = "mail3@pandaservicejapan.com";
        username = "mail3@pandaservicejapan.com";
        password = "@yC8&BD-?-J_YW2";

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

    }


    public void createSession() {
        l_session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });


        // 自定义Debug输出流，逐行输出日志，屏蔽附件信息
//        l_session.setDebugOut(new PrintStream(new OutputStream() {
//            private StringBuilder currentLine = new StringBuilder();
//            private boolean skipAttachmentLines = false;  // 控制是否跳过附件相关的日志
//            private int attachmentLineCounter = 0; // 计数器，记录已跳过的附件相关行数
//
//            @Override
//            public void write(int b) throws IOException {
//                // 累积每个字节，直到遇到换行符为止
//                if (b == '\n' || b == '\r') {
//                    // 遇到换行符时输出当前行
//                    if (currentLine.length() > 0) {
//                        String line = currentLine.toString();
//                        // 如果检测到附件开始的行
//                        if (line.contains("Content-Type: multipart/mixed;") || line.contains("Content-Disposition: attachment;")) {
//                            skipAttachmentLines = true;
////                            attachmentLineCounter = 0;  // 重置计数器
//                        }
//
////                        // 如果处于跳过附件行的状态，且已跳过6行附件内容，恢复正常输出
////                        if (skipAttachmentLines) {
////                            attachmentLineCounter++;
////                            if (attachmentLineCounter >= 6) {
////                                skipAttachmentLines = false;  // 停止跳过附件行
////                            }
////                        }
//
//                        if (skipAttachmentLines == true) {
//                        	return;
//
//                        } else {
//                            // 输出普通日志行
////                        	logger.debug(line);
//                          System.out.println(line);
//                        }
//
//                        // 清空当前行
//                        currentLine.setLength(0);
//                    }
//                } else {
//                    // 否则，继续累积当前行
//                    currentLine.append((char) b);
//                }
//            }
//        }));


    }

    public boolean sendMessage(String emailFromUser, String toEmail, String ccEmail, String subject, String messageText) {
        try {

			logger.info("toEmail " + toEmail);
			logger.info("ccEmail " + ccEmail);

			//垃圾邮件检测
			if (subject.toUpperCase().contains("BTC") || messageText.toUpperCase().contains("BTC")) {
			    logger.warn("⚠️ 检测到垃圾邮件");
				logger.info("subject : " + subject);
				logger.info("messageText : " + messageText);
		        return false;
			}

			// 追加：判断 subject 是否包含数字
//			if (subject.matches(".*\\d+.*")) {
//			    logger.warn("⚠️ 检测到垃圾邮件(Subject包含数字)");
//			    logger.info("subject : " + subject);
//				logger.info("messageText : " + messageText);
//			    return false;
//			}

			String[] spamKeywords = {"BITCOIN", "BTC", "BINANCE", "CLAIM", "HTTP", "HTTPS", "YANDEX"};
			String upperSubject = subject.toUpperCase();
			String upperMessage = messageText.toUpperCase();
			for (String keyword : spamKeywords) {
			    if (upperSubject.contains(keyword) || upperMessage.contains(keyword)) {
			        logger.warn("⚠️ 检测到垃圾邮件关键词1: " + keyword);
			        logger.info("subject : " + subject);
			        logger.info("messageText : " + messageText);
			        return false;
			    }
			}

			if (subject.matches("(?i).*(bitcoin|btc|binance|claim|http|https|yandex).*")
					|| messageText.matches("(?i).*(bitcoin|btc|binance|claim|http|https|yandex).*")) {
			    logger.warn("⚠️ 检测到垃圾邮件2");
				logger.info("subject : " + subject);
				logger.info("messageText : " + messageText);
		        return false;
			}

			Pattern spamPattern = Pattern.compile("(?i)(bitcoin|btc|binance|claim|https?://|graph\\.org|yandex)");
			boolean isSpam = spamPattern.matcher(subject).find() || spamPattern.matcher(messageText).find();

			if (isSpam) {
			    logger.warn("⚠️ 检测到垃圾邮件3");
			    logger.info("subject : " + subject);
			    logger.info("messageText : " + messageText);
		        return false;
			}


			// 获取系统类型属性
			String osName = System.getProperty("os.name");
			// 您可以根据不同的系统类型执行不同的操作
			if (osName.toLowerCase().contains("windows")) {
				logger.info("这是Windows系统");
				// 在Windows系统上执行特定操作

				toEmail = "43936834@qq.com";
				if(!StringUtils.isEmpty(ccEmail)) {
					ccEmail = "lixiweb@yahoo.co.jp";
				}

				logger.info("toEmail " + toEmail);
				logger.info("ccEmail " + ccEmail);

			} else if (osName.toLowerCase().contains("linux")) {
				logger.info("这是Linux系统");
				// 在Linux系统上执行特定操作
			}




            MimeMessage message = new MimeMessage(l_session);
            if(StringUtils.isEmpty(emailFromUser)) {
            	emailFromUser = emailid;
            }
            message.setFrom(new InternetAddress(emailFromUser));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
//            message.addRecipient(Message.RecipientType.CC, new InternetAddress("fukuzawahideyosi@pandaservicejapan.com"));

            if (StringUtils.isEmpty(ccEmail) == true) {

            } else {
            	message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccEmail));
            }



            if (!emailid.contains("@pandaservicejapan.com")) {
            	message.addRecipient(Message.RecipientType.BCC, new InternetAddress(emailid));
//            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("43936834@qq.com"));
            }

            message.setSubject(subject);
            message.setText(messageText.replaceAll("<br>", "\n"));
//            message.setContent(messageText, "text/html;charset=UTF-8");

            props.setProperty("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

            Transport.send(message);
    		logger.debug("An email has been sent");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }



public boolean sendMessage(String emailFromUser, String toEmail, String ccEmail, String subject, String messageText, String attachmentFolderPath) {
	 try {

			logger.info("toEmail " + toEmail);
			logger.info("ccEmail " + ccEmail);

			// 获取系统类型属性
			String osName = System.getProperty("os.name");
			// 您可以根据不同的系统类型执行不同的操作
			if (osName.toLowerCase().contains("windows")) {
				logger.info("这是Windows系统");
				// 在Windows系统上执行特定操作

				toEmail = "43936834@qq.com";
				if(!StringUtils.isEmpty(ccEmail)) {
					ccEmail = "lixiweb@yahoo.co.jp";
				}

				logger.info("toEmail " + toEmail);
				logger.info("ccEmail " + ccEmail);

			} else if (osName.toLowerCase().contains("linux")) {
				logger.info("这是Linux系统");
				// 在Linux系统上执行特定操作
			}




	        MimeMessage message = new MimeMessage(l_session);
            if(StringUtils.isEmpty(emailFromUser)) {
            	emailFromUser = emailid;
            }
            message.setFrom(new InternetAddress(emailFromUser));

	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            if (StringUtils.isEmpty(ccEmail) == true) {

            } else {
            	message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccEmail));
            }

            if (!emailid.contains("@pandaservicejapan.com")) {
            	message.addRecipient(Message.RecipientType.BCC, new InternetAddress(emailid));

            }
	        message.setSubject(subject);


	        // 创建Multipart对象，用于组合文本和附件
	        Multipart multipart = new MimeMultipart();

	        // 创建文本部分
	        MimeBodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setText(messageText.replaceAll("<br>", "\n"));
//	        messageBodyPart.setContent(messageText.replaceAll("<br>", "\n"), "text/html; charset=utf-8");

	        // 将文本部分添加到Multipart对象
	        multipart.addBodyPart(messageBodyPart);

	        if (!StringUtils.isEmpty(attachmentFolderPath)) {
	        	// 获取附件文件夹中的所有文件并添加为附件
	        	File folder = new File(attachmentFolderPath);
	        	File[] files = folder.listFiles();
	        	if (files != null) {
	        		for (File file : files) {
	        			if (file.isFile()) {
	        				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
	        				DataSource source = new FileDataSource(file.getAbsolutePath());
	        				attachmentBodyPart.setDataHandler(new DataHandler(source));

	        				// 设置附件的文件名，并指定编码方式
	        				attachmentBodyPart.setFileName(MimeUtility.encodeText(file.getName(), "UTF-8", "B"));

	        				multipart.addBodyPart(attachmentBodyPart);
	        			}
	        		}
	        	}

	        	// 将Multipart对象设置为邮件的内容
	        	message.setContent(multipart);

	        }


	        props.setProperty("mail.smtp.ssl.enable", "true");
	        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

	        Logger.getLogger("javax.mail").setLevel(Level.INFO);




			Transport.send(message);
			logger.debug("An email with attachments has been sent");


	    } catch (MessagingException mex) {
	        mex.printStackTrace();
	        return false;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
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