package com.panda.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class SearchYahooAuctions {

	private static Logger logger = Logger.getLogger(SearchYahooAuctions.class.toString());

	static String[] line1;

	public static void main(String[] args) throws Exception {

		System.setProperty("https.protocols", "TLSv1.2");

		sendGet();
	}


	private static void sendGet() throws Exception {

		URL url;
		try {

			String url_s = "https://page.auctions.yahoo.co.jp/jp/auction/r1070127149";
			url = new URL(url_s);
			URLConnection conn;
			conn = url.openConnection();
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = br.readLine()) != null) {
				logger.info(line);
			}
			br.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
