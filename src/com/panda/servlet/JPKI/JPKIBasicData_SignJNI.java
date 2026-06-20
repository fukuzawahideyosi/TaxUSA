package com.panda.servlet.JPKI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.go.jpki.appli.JPKICryptAuthJNI;
import jp.go.jpki.appli.JPKICryptAuthJNIException;
import jp.go.jpki.appli.JPKICryptSignJNI;
import jp.go.jpki.appli.JPKICryptSignJNIException;

/**
 * 署名用電子証明書の基本4情報
 */
public class JPKIBasicData_SignJNI {
	/**
	 * 署名用電子証明書の取得サンプル
	 *
	 * @return
	 * @throws JPKICryptSignJNIException
	 */
	public static byte[] getSignCertificateValue_Sample() throws JPKICryptSignJNIException {
		JPKICryptSignJNI jni = new JPKICryptSignJNI();
		// (1)プロバイダハンドルを取得
		// パスワードを入力するダイアログが表示される
		long hProvider = jni.cryptAcquireContext(0);
		try {
			// (2)プロバイダの秘密鍵ハンドルを取得
			long hKey = jni.cryptGetUserKey(hProvider);
			try {
				// (3)秘密鍵に対応する利用者証明書を取得
				return jni.cryptGetCertificateValue(hKey);
			} finally {
				// (4)鍵ハンドルを解放
				jni.cryptDestroyKey(hKey);
			}
		} finally {
			// (5)プロバイダハンドルを解放
			jni.cryptReleaseContext(hProvider);
		}
	}


	/**
	 * 署名用認証局の自己署名証明書の取得サンプル
	 * @return
	 * @throws JPKICryptSignJNIException
	 */
	public static byte[] getSignRootCertificateValue_Sample() throws JPKICryptSignJNIException{
	    JPKICryptSignJNI jni = new JPKICryptSignJNI();
	    //(1)プロバイダハンドルを取得
	    // 引数にJPKICryptAuthJNI.JPKI_VERIFYCONTEXTを指定できるのでパスワード不要で取得できる
	    long hProvider  = jni.cryptAcquireContext(JPKICryptAuthJNI.JPKI_VERIFYCONTEXT);
	    try {
	         //(2)署名用認証局の自己署名証明書を取得
	        return jni.cryptGetRootCertificateValue(hProvider);
	    } finally {
	        //(3)プロバイダハンドルを解放
	        jni.cryptReleaseContext(hProvider);
	    }
	}

	/**
	 * 利用者証明用電子証明書の取得サンプル
	 * @return
	 * @throws JPKICryptAuthJNIException
	 */
	public static byte[] getAuthCertificateValue_Sample() throws JPKICryptAuthJNIException{
	    JPKICryptAuthJNI jni = new JPKICryptAuthJNI();
	    //(1)プロバイダハンドルを取得
	    // パスワードを入力するダイアログが表示される
	    long hProvider  = jni.cryptAcquireContext(0);
	    // API仕様サンプルでは引数に0を指定している（パスワードの入力が必要になる）が、
	    // JPKICryptAuthJNI.JPKI_VERIFYCONTEXTを指定しても取得できるみたい
	    // （この場合、パスワード入力は不要）
	    //long hProvider  = jni.cryptAcquireContext(JPKICryptAuthJNI.JPKI_VERIFYCONTEXT);
	    try {
	        //(2)プロバイダの秘密鍵ハンドルを取得
	        long hKey = jni.cryptGetUserKey(hProvider);
	        try {
	             //(3)秘密鍵に対応する利用者証明書を取得
	            return jni.cryptGetCertificateValue(hKey);
	        } finally {
	            //(4)鍵ハンドルを解放
	            jni.cryptDestroyKey(hKey);
	        }
	    } finally {
	        //(5)プロバイダハンドルを解放
	        jni.cryptReleaseContext(hProvider);
	    }
	}

	/**
	 * 利用者証明用電子証明書の自己署名証明書の取得サンプル
	 * @return
	 * @throws JPKICryptAuthJNIException
	 */
	public static byte[] getAuthRootCertificateValue_Sample() throws JPKICryptAuthJNIException{
	    JPKICryptAuthJNI jni = new JPKICryptAuthJNI();
	    //(1)プロバイダハンドルを取得
	    // 引数にJPKICryptAuthJNI.JPKI_VERIFYCONTEXTを指定できるのでパスワード不要で取得できる
	    long hProvider  = jni.cryptAcquireContext(JPKICryptAuthJNI.JPKI_VERIFYCONTEXT);
	    try {
	         //(2)署名用認証局の自己署名証明書を取得
	        return jni.cryptGetRootCertificateValue(hProvider);
	    } finally {
	        //(3)プロバイダハンドルを解放
	        jni.cryptReleaseContext(hProvider);
	    }
	}

	public static void main(String[] args) {
		try {
			String dtformatDate = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

			byte[] cert=null;
			//PW必須
//			cert = getSignCertificateValue_Sample();
//			Files.write(Paths.get("E:\\IT-日本电子认证\\pdf\\wd署名用電子証明書" + dtformatDate + ".cer"), cert);
			//PW不要
			cert = getSignRootCertificateValue_Sample();
			Files.write(Paths.get("E:\\IT-日本电子认证\\pdf\\wd署名用電子証明書NoPw" + dtformatDate + ".cer"), cert);


			byte[] certAuth=null;
			//PW必須
//			certAuth = getAuthCertificateValue_Sample();
//			Files.write(Paths.get("E:\\IT-日本电子认证\\pdf\\wd利用者証明用電子証明書" + dtformatDate + ".cer"), certAuth);
			//PW不要
			certAuth = getAuthCertificateValue_Sample();
			Files.write(Paths.get("E:\\IT-日本电子认证\\pdf\\wd利用者証明用電子証明書NoPw" + dtformatDate + ".cer"), certAuth);
		} catch (JPKICryptSignJNIException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (JPKICryptAuthJNIException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
}