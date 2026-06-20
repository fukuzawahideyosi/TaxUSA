package com.panda.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class FuncUtils_encrypted_decrypted {

	private static Logger logger = Logger.getLogger(FuncUtils_encrypted_decrypted.class.toString());

	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";
	private static final String SPECIAL_CHARACTERS = "~!@#$%^&*()-_+=<>?/.";

	private static String encryptionKey = "sasasas";

	private static Map<Character, String> encryptionMap = new HashMap<>();
	private static Map<String, Character> decryptionMap = new HashMap<>();

	static {
		// 初始化加密映射
		int counter = 0;
		for (char ch : SPECIAL_CHARACTERS.toCharArray()) {
			encryptionMap.put(ch, String.valueOf(counter++));
		}

		for (char ch : ALPHABET.toCharArray()) {
			encryptionMap.put(ch, String.valueOf(counter++));
		}

		// 初始化解密映射
		counter = 0;
		for (char ch : SPECIAL_CHARACTERS.toCharArray()) {
			decryptionMap.put(String.valueOf(counter++), ch);
		}

		for (char ch : ALPHABET.toCharArray()) {
			decryptionMap.put(String.valueOf(counter++), ch);
		}
	}

	public static String encrypt(String input, String key) {
		StringBuilder encrypted = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			char inputChar = input.charAt(i);
			char keyChar = key.charAt(i % key.length());

			int charIndex = (ALPHABET.indexOf(inputChar) + ALPHABET.indexOf(keyChar)) % ALPHABET.length();
			encrypted.append(ALPHABET.charAt(charIndex));
		}

		return encrypted.toString();
	}

	public static String decrypt(String input, String key) {
		StringBuilder decrypted = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			char inputChar = input.charAt(i);
			char keyChar = key.charAt(i % key.length());

			int charIndex = (ALPHABET.indexOf(inputChar) - ALPHABET.indexOf(keyChar) + ALPHABET.length()) % ALPHABET.length();
			decrypted.append(ALPHABET.charAt(charIndex));
		}

		return decrypted.toString();
	}

	public static void main(String[] args) {
		try {
			String inputData = "20230523000002";
			// 删除1和2位，以及9和10位
			String trimmedData = inputData.substring(2, 8) + inputData.substring(10);

			// 原始数据列表
			List<String> originalDataList = new ArrayList<>();
			originalDataList.add(trimmedData);

			inputData = "20230216000084";
			// 删除1和2位，以及9和10位
			trimmedData = inputData.substring(2, 8) + inputData.substring(10);
			originalDataList.add(trimmedData);

			inputData = "20230216000085";
			// 删除1和2位，以及9和10位
			trimmedData = inputData.substring(2, 8) + inputData.substring(10);
			originalDataList.add(trimmedData);

			// 加密和解密
			for (String originalData : originalDataList) {
				String encryptedData = encrypt(originalData, encryptionKey);
				String decryptedData = decrypt(encryptedData, encryptionKey);

				logger.info("原始数据: " + originalData + " 加密后: " + encryptedData + " 解密后: " + decryptedData);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
