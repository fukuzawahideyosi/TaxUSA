package com.panda.batch;

public class batchAPI {

	public static void main(String[] args) {
		try {

			PDFToImageToPDF PDFToImageToPDF = new PDFToImageToPDF();
			PDFToImageToPDF.main(args);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
