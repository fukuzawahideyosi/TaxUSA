package com.panda.utils;



import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class FuncUtilsP12ReaderWithValidity {
    public static void main(String[] args) {
        try {
            // 无密码 .p12 文件路径
            String p12FilePath = "I:\\我的云端硬盘\\日本-PANDASERVICE株式会社\\商業登記電子証明書\\666666.p12";

            // 加载 .p12 文件到 KeyStore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(p12FilePath);

            // 密码为空时，传递 null 或空数组
            keyStore.load(fis, null); // 或使用 keyStore.load(fis, "".toCharArray());


            // 遍历 KeyStore 中的别名
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                System.out.println("Alias: " + alias);

                // 获取证书
                Certificate cert = keyStore.getCertificate(alias);
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;

                    // 获取证书有效期
                    System.out.println("Certificate Valid From: " + x509Cert.getNotBefore());
                    System.out.println("Certificate Valid Until: " + x509Cert.getNotAfter());

                    // 打印证书信息
                    System.out.println("Certificate Details:");
                    System.out.println(x509Cert.toString());
                }
            }

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
