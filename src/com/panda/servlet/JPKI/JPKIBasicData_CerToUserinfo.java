package com.panda.servlet.JPKI;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.OtherName;
import org.bouncycastle.cert.X509CertificateHolder;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.security.PdfSignature;
import com.spire.pdf.widget.PdfFormFieldWidgetCollection;
import com.spire.pdf.widget.PdfFormWidget;
import com.spire.pdf.widget.PdfSignatureFieldWidget;

/**
 * 署名用電子証明書の基本4情報
 */
public class JPKIBasicData_CerToUserinfo {

	private static Logger logger = Logger.getLogger(JPKIBasicData_CerToUserinfo.class.toString());
	/**
	 * 署名用電子証明書から基本4情報を取得する
	 * @param cert
	 * @return
	 * @throws IOException
	 */
	public static JPKIBasicData_CerToUserinfo parseBasicData(byte[] cert) throws IOException {
		//　BouncyCastleの証明書オブジェクト
		X509CertificateHolder certHldr = new X509CertificateHolder(cert);
		// このクラスのインスタンスを生成
		JPKIBasicData_CerToUserinfo ret = new JPKIBasicData_CerToUserinfo();

		// 証明書の拡張領域からsubjectAltNameを抽出
		GeneralNames subjectAltName = GeneralNames.fromExtensions(
				certHldr.getExtensions(), Extension.subjectAlternativeName);

		if (subjectAltName != null) {
			// GeneralNameの要素ごとに処理
			for (GeneralName gnm : subjectAltName.getNames()) {
				if (gnm.getTagNo() == GeneralName.otherName) {
					// otherName のtypeに応じて値を抽出
					OtherName oName = OtherName.getInstance(gnm.getName());
					String oValue = oName.getValue().toString();

					logger.info(oName.getTypeID().getId()+":"+oValue);

					switch (oName.getTypeID().getId()) {
					// 氏名
					case "1.2.392.200149.8.5.5.1":
						ret.name = oValue;
						break;
					// 生年月日
					case "1.2.392.200149.8.5.5.4":
						ret.dateOfBirth = oValue;
						break;
					// 性別
					case "1.2.392.200149.8.5.5.3":
						ret.gender = oValue;
						break;
					// 住所
					case "1.2.392.200149.8.5.5.5":
						ret.address = oValue;
						break;
					// 利用者の氏名代替文字の使用位置情
					case "1.2.392.200149.8.5.5.2":
						ret.substituteCharacterOfAddress = oValue;
						break;
					// 利用者の住所代替文字の使用位置情報
					case "1.2.392.200149.8.5.5.6":
						ret.substituteCharacterOfName = oValue;
						break;
					}
				}
			}
		}
		return ret;
	}

	private JPKIBasicData_CerToUserinfo() {
	}

	private String name = "";
	private String dateOfBirth = "";
	private String gender = "";
	private String address = "";
	private String substituteCharacterOfAddress = "";
	private String substituteCharacterOfName = "";

	/**
	 * 氏名
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 生年月日
	 * @return
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * 性別
	 * @return
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * 住所
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 利用者の氏名代替文字の使用位置情
	 * @return
	 */
	public String getSubstituteCharacterOfAddress() {
		return substituteCharacterOfAddress;
	}

	/**
	 * 利用者の住所代替文字の使用位置情報
	 * @return
	 */
	public String getSubstituteCharacterOfName() {
		return substituteCharacterOfName;
	}

	public static void main1(String[] args) {
		try {
			// 署名用電子証明書の準備（ここではファイルから読み込み）
//			byte[] cert = Files.readAllBytes(Paths.get("E:\\IT-日本电子认证\\pdf\\wd署名用電子証明書20220527013502.cer"));
//			byte[] cert = Files.readAllBytes(Paths.get("E:\\IT-日本电子认证\\pdf\\CertExchangeCH.cer"));
			byte[] cert = Files.readAllBytes(Paths.get("E:\\IT-日本电子认证\\pdf\\CertExchangeAN.cer"));

			// 基本４情報を取得
			JPKIBasicData_CerToUserinfo res = JPKIBasicData_CerToUserinfo.parseBasicData(cert);
			//
			logger.info("氏名=" + res.getName());
			logger.info("生年月日=" + res.getDateOfBirth());
			logger.info("性別=" + res.getGender());
			logger.info("住所=" + res.getAddress());
			logger.info("氏名の代替文字=" + res.getSubstituteCharacterOfAddress());
			logger.info("住所の代替文字=" + res.getSubstituteCharacterOfName());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		//创建PdfDocument实例
		PdfDocument doc = new PdfDocument();
		//加载含有签名的PDF文件
		doc.loadFromFile("E:\\IT-日本电子认证\\pdf\\契約書OK.pdf");

		//获取域集合
		PdfFormWidget pdfFormWidget = (PdfFormWidget) doc.getForm();
		PdfFormFieldWidgetCollection pdfFormFieldWidgetCollection = pdfFormWidget.getFieldsWidget();

		//遍历域
		for (int i = 0; i < pdfFormFieldWidgetCollection.getCount(); i++) {
			//判定是否为签名域
			if (pdfFormFieldWidgetCollection.get(i) instanceof PdfSignatureFieldWidget) {
				//获取签名域
				PdfSignatureFieldWidget signatureFieldWidget = (PdfSignatureFieldWidget) pdfFormFieldWidgetCollection
						.get(i);
				//获取签名
				PdfSignature signature = signatureFieldWidget.getSignature();

				//获取签名证书信息
				byte[] certificateInfo = signatureFieldWidget.getSignature().getCertificate().toString().getBytes();


				try {


					// 署名用電子証明書の準備（ここではファイルから読み込み）
//					byte[] cert = Files.readAllBytes(Paths.get("E:\\IT-日本电子认证\\pdf\\CertExchange.cer"));
//					byte[] cert = Files.readAllBytes(Paths.get("E:\\IT-日本电子认证\\pdf\\CertExchangeCH.cer"));
					byte[] cert = Files.readAllBytes(Paths.get("E:\\IT-日本电子认证\\pdf\\CertExchangeAN.cer"));

					// 基本４情報を取得
					JPKIBasicData_CerToUserinfo res = JPKIBasicData_CerToUserinfo.parseBasicData(cert);
					//
					logger.info("氏名=" + res.getName());
					logger.info("生年月日=" + res.getDateOfBirth());
					logger.info("性別=" + res.getGender());
					logger.info("住所=" + res.getAddress());
					logger.info("氏名の代替文字=" + res.getSubstituteCharacterOfAddress());
					logger.info("住所の代替文字=" + res.getSubstituteCharacterOfName());
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}

	}

	//	public static void main(String[] args) {
	//		try {
	//
	//
	//
	//			//创建PdfDocument实例
	//			PdfDocument doc = new PdfDocument();
	//			//加载含有签名的PDF文件
	//			doc.loadFromFile("E:\\IT-日本电子认证\\pdf\\契約書OK.pdf");
	//
	//			//获取域集合
	//			PdfFormWidget pdfFormWidget = (PdfFormWidget) doc.getForm();
	//			PdfFormFieldWidgetCollection pdfFormFieldWidgetCollection = pdfFormWidget.getFieldsWidget();
	//
	//			//遍历域
	//			for (int i = 0; i < pdfFormFieldWidgetCollection.getCount(); i++) {
	//				//判定是否为签名域
	//				if (pdfFormFieldWidgetCollection.get(i) instanceof PdfSignatureFieldWidget) {
	//					//获取签名域
	//					PdfSignatureFieldWidget signatureFieldWidget = (PdfSignatureFieldWidget) pdfFormFieldWidgetCollection
	//							.get(i);
	//	                //获取签名证书
	//	                PdfCertificate cer = signatureFieldWidget.getSignature().getCertificate();
	//
	//
	//				}
	//
	//
	//
	//			// 署名用電子証明書の準備（ここではファイルから読み込み）
	//			byte[] cert = Files.readAllBytes(Paths.get("E:\\IT-日本电子认证\\pdf\\wd署名用電子証明書20220527013502.cer"));
	//
	//			// 基本４情報を取得
	//			JPKIBasicData_CerToUserinfo res = JPKIBasicData_CerToUserinfo.parseBasicData(cert);
	//			//
	//			logger.info("氏名=" + res.getName());
	//			logger.info("生年月日=" + res.getDateOfBirth());
	//			logger.info("性別=" + res.getGender());
	//			logger.info("住所=" + res.getAddress());
	//			logger.info("氏名の代替文字=" + res.getSubstituteCharacterOfAddress());
	//			logger.info("住所の代替文字=" + res.getSubstituteCharacterOfName());
	//		} catch (IOException e) {
	//			// TODO 自動生成された catch ブロック
	//			e.printStackTrace();
	//		}
	//	}
}