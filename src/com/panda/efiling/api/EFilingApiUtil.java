package com.panda.efiling.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * CPSC eFiling API 工具类
 *
 * 功能：
 * 1. 查询产品集合
 * 2. 导入产品证书
 * 3. 查询导入状态
 * 4. 查询导入日志
 * 5. 同步导出证书
 * 6. 异步导出证书
 * 7. 查询异步导出状态
 * 8. 获取异步导出数据
 * 9. 根据产品查询证书
 * 10. 查询贸易方信息
 * 11. 查询Token过期时间
 *
 * 技术要求：
 * JDK11+
 * Jackson Databind
 */
public class EFilingApiUtil {

	private static Logger logger = Logger.getLogger(EFilingApiUtil.class.toString());

	private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJFRklMSU5HX0FQSSIsInN1YiI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIiwiZ2xvYmFsUGVybWlzc2lvbnMiOltdLCJ1c2VyRGF0YSI6eyJmaXJzdE5hbWUiOiJoaWRleW9zaGkiLCJsYXN0TmFtZSI6ImZ1a3V6YXdhIiwicGhvbmUiOiIwMDgxMDgwMzU1NjY4ODYiLCJ1c2VySWQiOiI0MTI5OWZmZS1kNjQzLTRjMGItYjM3Ny02NmNhMzY2M2RhNWQiLCJlbWFpbCI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIn0sIngtYXBpLXNlY3JldCI6Imo3aWlUV0hMYVhUUm9TWnpUdzRuV2c9PTpoekpwSlQzVEpSaUdhRXp0ZnRVTnN6MW5xNzFuaGdwR2p6ZTBqK2FSYU1FPSIsImdsb2JhbFJvbGVzIjpbXSwib2JqZWN0UGVybWlzc2lvbnMiOltdLCJ2ZXJzaW9uIjozLCJleHAiOjE3ODk1NzYxMTMsImlhdCI6MTc4MTgwMDExM30.lAUojFNFNNia-pr7AKczAKltpQnIFpwDsTp3cLYcKQtTO7rbCjznouL0bwsxKJ5DOlP5FtsDdvbA4dKNZLfFtQ";

	private static final String API_SECRET = "A68!uP86@kL568#xM868";

	private static final String CERTIFIER_ID = "ForeverCo.,Ltd.";

	protected static final String COLLECTION_ID = "6ce12fad-0ada-41c5-9d2b-15d3a911eaa8";
	//	private static final String COLLECTION_ID = "62258ad9-d9f2-4020-b773-dd262f86a4e2";

	private static final String BASE_URL = "https://efiling.saferproducts.gov/efiling/api";

	protected static final ObjectMapper MAPPER = new ObjectMapper();

	private static final HttpClient CLIENT = HttpClient.newHttpClient();

	public static void main(String[] args) {
		try {
			//TODO
			String ImportId = "";
			ImportId = importProduct(buildAddProductJson());
			//			ImportId = "ce0ebb1e-f0bc-43a8-9df6-8b3ddf0bca24";
			//			ImportId = "b6d0bd00-b51a-4f36-b356-a7ff13dec778";
			//			ImportId = "2025111b-d4be-4559-8473-c05859324f68";
			//			ImportId = "b2a79ab3-704f-4e6a-a83c-b746cee6af01";
			ImportId = "b2a79ab3-704f-4e6a-a83c-b746cee6af01";

			Thread.sleep(1000 * 12);
			getImportLog(ImportId, false);
			getImportStatus(ImportId);

			//			getCertificates

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 输出API调用日志
	 *
	 * @param apiName API名称
	 * @param url 请求地址
	 * @param requestBody 请求报文
	 * @param responseBody 返回报文
	 */
	private static void printApiLog(
			String url,
			String requestBody,
			String responseBody) {

		responseBody = printJson(responseBody);

		logger.debug("");
		logger.debug("==================================================");
		logger.debug("URL : " + url);

		if (requestBody != null) {

			logger.debug("REQUEST:");
			logger.debug(requestBody);
		}

		logger.debug("RESPONSE:");
		logger.debug(responseBody);

		logger.debug("==================================================");
		logger.debug("");
	}

	/**
	 * 发送GET请求
	 *
	 * @param url 请求地址
	 * @return 返回JSON字符串
	 * @throws Exception 网络异常
	 */
	protected static String get(
			String url)
			throws Exception {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", TOKEN)
				.header("x-api-secret", API_SECRET)
				.GET()
				.build();

		HttpResponse<String> response = CLIENT.send(
				request,
				HttpResponse.BodyHandlers.ofString());

		printApiLog(
				url,
				null,
				response.body());

		return response.body();
	}

	/**
	 * 发送POST请求
	 *
	 * @param url 请求地址
	 * @param json 请求JSON
	 * @return 返回JSON字符串
	 * @throws Exception 网络异常
	 */
	private static String post(
			String url,
			String json)
			throws Exception {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", TOKEN)
				.header("x-api-secret", API_SECRET)
				.header("Content-Type", "application/json")
				.POST(
						HttpRequest.BodyPublishers
								.ofString(json))
				.build();

		HttpResponse<String> response = CLIENT.send(
				request,
				HttpResponse.BodyHandlers.ofString());

		printApiLog(
				url,
				json,
				response.body());

		return response.body();
	}

	/** 查询当前用户可访问的Collection列表 */
	public static JsonNode getCollections() throws Exception {
		return MAPPER.readTree(get(BASE_URL + "/getCollections"));
	}

	/**
	 * 导入产品证书
	 * @param json productList完整JSON
	 * @return importId
	 */
	public static String importProduct(String json) throws Exception {

		String url = BASE_URL + "/import"
				+ "?certifierId=" + CERTIFIER_ID
				+ "&collectionId=" + COLLECTION_ID
				+ "&doCertify=true"
				;

		JsonNode root = MAPPER.readTree(post(url, json));

		return root.path("importId").asText();
	}

	/**
	 * 查询导入任务状态
	 *
	 * @param importId 导入任务ID
	 * @return 状态JSON
	 */
	public static JsonNode getImportStatus(String importId)
			throws Exception {

		String url = BASE_URL + "/getImportStatus"
				+ "?importId="
				+ URLEncoder.encode(importId, StandardCharsets.UTF_8);

		return MAPPER.readTree(get(url));
	}

	/**
	 * 查询导入日志
	 *
	 * @param importId 导入任务ID
	 * @param errorsOnly true仅错误记录 false全部记录
	 * @return 导入日志
	 */
	public static JsonNode getImportLog(
			String importId,
			boolean errorsOnly)
			throws Exception {

		String url = BASE_URL + "/getImportLog"
				+ "?importId="
				+ URLEncoder.encode(importId, StandardCharsets.UTF_8)
				+ "&errorsOnly=" + errorsOnly;

		return MAPPER.readTree(get(url));
	}

	/** 同步导出产品证书 */
	public static JsonNode exportCertificates()
			throws Exception {

		String url = BASE_URL + "/export"
				+ "?certifierId=" + CERTIFIER_ID
				+ "&collectionId=" + COLLECTION_ID;

		return MAPPER.readTree(get(url));
	}

	/**
	 * 创建异步导出任务
	 *
	 * @return exportId
	 */
	public static String exportAsync()
			throws Exception {

		String url = BASE_URL + "/exportAsync"
				+ "?certifierId=" + CERTIFIER_ID
				+ "&collectionId=" + COLLECTION_ID;

		JsonNode root = MAPPER.readTree(get(url));

		return root.path("exportId").asText();
	}

	/**
	 * 查询异步导出状态
	 *
	 * @param exportId 导出任务ID
	 * @return 状态JSON
	 */
	public static JsonNode exportAsyncStatus(String exportId)
			throws Exception {

		String url = BASE_URL + "/exportAsyncStatus"
				+ "?exportId="
				+ URLEncoder.encode(exportId, StandardCharsets.UTF_8);

		return MAPPER.readTree(get(url));
	}

	/**
	 * 获取异步导出结果
	 *
	 * @param exportId 导出任务ID
	 * @return 产品数据
	 */
	public static JsonNode exportAsyncData(String exportId)
			throws Exception {

		String url = BASE_URL + "/exportAsyncData"
				+ "?exportId="
				+ URLEncoder.encode(exportId, StandardCharsets.UTF_8);

		return MAPPER.readTree(get(url));
	}

	/**
	 * 查询指定产品证书
	 *
	 * @param productId 产品ID
	 * @param version 版本号，空字符串表示全部版本
	 * @return 产品证书数据
	 */
	public static JsonNode getCertificates(
			String productId,
			String version)
			throws Exception {

		String url = BASE_URL + "/getCertificates"
				+ "?certifierId=" + CERTIFIER_ID
				+ "&collectionId=" + COLLECTION_ID;

		String json = "[{\"productId\":\""
				+ productId
				+ "\",\"version\":\""
				+ version
				+ "\"}]";

		return MAPPER.readTree(post(url, json));
	}

	/**
	 * 查询贸易方
	 *
	 * @param tradePartyType
	 * All
	 * Manufacturer
	 * Laboratory
	 * Point of Contact
	 *
	 * @return 贸易方列表
	 */
	public static JsonNode getTradeParties(
			String tradePartyType)
			throws Exception {

		String url = BASE_URL + "/getTradeParties"
				+ "?certifierId=" + CERTIFIER_ID
				+ "&collectionId=" + COLLECTION_ID;

		if (tradePartyType != null
				&& !tradePartyType.isBlank()) {

			url += "&tradePartyType="
					+ URLEncoder.encode(
							tradePartyType,
							StandardCharsets.UTF_8);
		}

		return MAPPER.readTree(get(url));
	}

	/**
	 * 查询Token过期时间
	 *
	 * @return Token过期时间
	 */
	public static String getTokenExpiration()
			throws Exception {

		JsonNode root = MAPPER.readTree(
				get(BASE_URL
						+ "/getTokenExpiration"));

		return root.path("tokenExpiration")
				.asText();
	}

	/**
	 * 等待导入任务完成
	 *
	 * @param importId 导入任务ID
	 */
	public static void waitImportComplete(
			String importId)
			throws Exception {

		while (true) {

			JsonNode status = getImportStatus(importId);

			String state = status.path("importStatus")
					.asText();

			if (state.contains("Complete")) {
				return;
			}

			if (state.contains("Failed")) {
				throw new RuntimeException(
						"Import Failed");
			}

			Thread.sleep(3000);
		}
	}

	/**
	 * 等待导出任务完成
	 *
	 * @param exportId 导出任务ID
	 */
	public static void waitExportComplete(
			String exportId)
			throws Exception {

		while (true) {

			JsonNode status = exportAsyncStatus(exportId);

			String state = status.path("exportStatus")
					.asText();

			if (state.toLowerCase()
					.contains("completed")) {
				return;
			}

			if (state.toLowerCase()
					.contains("failed")) {

				throw new RuntimeException(
						state);
			}

			Thread.sleep(3000);
		}
	}

	private static String printJson(String json) {
		String prettyJson = "";
		try {
			ObjectMapper mapper = new ObjectMapper();

			Object obj = mapper.readValue(json, Object.class);

			prettyJson = mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return prettyJson;
	}

	/**
	 * JSON 单独抽出来（避免 main 巨长）
	 */

	protected static String buildAddProductJson() {

		String time = ts();
		String versionId = "V" + time;
		return "{"
				+ "  \"productList\":[{"

				+ "    \"coreProduct\":{"

				+ "      \"versionId\":\"" + versionId + "\","
				+ "      \"primaryProductId\":\"P_" + time + "\","
				+ "      \"primaryProductIdType\":\"Model #\","
				+ "      \"certificateType\":\"GCC\","

				+ "      \"name\":\"TEST_PRODUCT_" + time + "\","
				+ "      \"tradeBrandName\":\"FOREVER\","
				+ "      \"description\":\"Created By Java API " + time + "\","

				+ "      \"manufacturer\":{"
				+ "        \"alternateId\":\"MFG_" + time + "\","
				+ "        \"name\":\"name_" + time + "\","
				+ "        \"addressLine1\":\"Tokyo\","
				+ "        \"addressLine2\":\"\","
				+ "        \"aptNumber\":\"\","
				+ "        \"city\":\"Tokyo\","
				+ "        \"stateProvince\":\"Tokyo\","
				+ "        \"country\":\"Japan\","
				+ "        \"postalCode\":\"1000001\","
				+ "        \"phone\":\"0000000000\","
				+ "        \"email\":\"test@test.com\""
				+ "      },"

				+ "      \"manufactureDate\":\"06/2026\","

				+ "      \"lastTestDate\":\"06/19/2026\","

				+ "      \"lotNumber\":\"LOT_" + time + "\","

				+ "      \"labs\":[{"
				+ "        \"type\":\"LAB\","
				+ "        \"alternateId\":\"TEST-LAB\","
				+ "        \"name\":\"TEST LAB\","
				+ "        \"addressLine1\":\"Tokyo\","
				+ "        \"addressLine2\":\"\","
				+ "        \"aptNumber\":\"\","
				+ "        \"city\":\"Tokyo\","
				+ "        \"stateProvince\":\"Tokyo\","
				+ "        \"country\":\"Japan\","
				+ "        \"postalCode\":\"1000001\","
				+ "        \"phone\":\"0000000000\","
				+ "        \"email\":\"lab@test.com\","
				+ "        \"citationCodes\":[\"1201\"],"
				+ "        \"testReportId\":\"RPT_" + time + "\","
				+ "        \"isComponent\":false,"
				+ "        \"componentDescription\":\"\""
				+ "      }],"

				+ "      \"exemptions\":["
				+ "        \"1610.1(d)(1)\""
				+ "      ],"

				+ "      \"poc\":{"
				+ "        \"type\":\"Importer\","
				+ "        \"name\":\"TEST IMPORTER\","
				+ "        \"email\":\"test@test.com\""
				+ "      }"

				+ "    },"

				+ "    \"directives\":{"

				+ "      \"productUpdate\":\"N\","

				+ "      \"manufacturer\":{"
				+ "        \"isNew\":\"Y\","
				+ "        \"alternateId\":\"MFG_" + time + "\""
				+ "      },"

				+ "      \"labs\":[{"
				+ "        \"isNew\":\"N\","
				+ "        \"alternateId\":\"TEST-LAB\""
				+ "      }],"

				+ "      \"poc\":{"
				+ "        \"isNew\":\"N\""
				+ "      }"

				+ "    }"

				+ "  }]"
				+ "}";
	}

	public static String ts() {
		return LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	}
}

/*


API	中文名称	作用
/getCollections	查询产品集合	查询当前账号下可用的 Product Collection（产品集合）
/import	导入产品证书	向 Product Registry 导入 GCC/CPC 产品证书
/getImportStatus	查询导入状态	根据 importId 查询导入进度
/getImportLog	查询导入日志	查看导入结果及具体错误信息
/export	同步导出证书	立即导出产品证书数据
/exportAsync	创建异步导出任务	大批量数据导出时创建后台任务
/exportAsyncStatus	查询导出任务状态	查看异步导出是否完成
/exportAsyncData	获取导出数据	下载异步导出的产品证书数据
/getCertificates	查询产品证书	根据 ProductId 查询已导入证书
/getTradeParties	查询贸易主体	查询制造商、实验室、联系人等资料
/getTokenExpiration	查询Token过期时间	查看JWT Token失效时间

*/