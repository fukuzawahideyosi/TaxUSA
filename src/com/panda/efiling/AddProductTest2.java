package com.panda.efiling;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class AddProductTest2 {
	/*

	{"businessAccountList":[{"certifierId":"ForeverCo.,Ltd.","companyName":"Forever Co., Ltd.","collectionList":[{"userRoles":["Business Account Administrator"],"collectionId":"6ce12fad-0ada-41c5-9d2b-15d3a911eaa8","collectionName":"TEST"}]}],"statusMessage":"OK","statusCode":200}

	 */

	private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJFRklMSU5HX0FQSSIsInN1YiI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIiwiZ2xvYmFsUGVybWlzc2lvbnMiOltdLCJ1c2VyRGF0YSI6eyJmaXJzdE5hbWUiOiJoaWRleW9zaGkiLCJsYXN0TmFtZSI6ImZ1a3V6YXdhIiwicGhvbmUiOiIwMDgxMDgwMzU1NjY4ODYiLCJ1c2VySWQiOiI0MTI5OWZmZS1kNjQzLTRjMGItYjM3Ny02NmNhMzY2M2RhNWQiLCJlbWFpbCI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIn0sIngtYXBpLXNlY3JldCI6Imo3aWlUV0hMYVhUUm9TWnpUdzRuV2c9PTpoekpwSlQzVEpSaUdhRXp0ZnRVTnN6MW5xNzFuaGdwR2p6ZTBqK2FSYU1FPSIsImdsb2JhbFJvbGVzIjpbXSwib2JqZWN0UGVybWlzc2lvbnMiOltdLCJ2ZXJzaW9uIjozLCJleHAiOjE3ODk1NzYxMTMsImlhdCI6MTc4MTgwMDExM30.lAUojFNFNNia-pr7AKczAKltpQnIFpwDsTp3cLYcKQtTO7rbCjznouL0bwsxKJ5DOlP5FtsDdvbA4dKNZLfFtQ";

	private static final String API_SECRET = "A68!uP86@kL568#xM868";

	private static final String CERTIFIER_ID = "ForeverCo.,Ltd.";

	private static final String COLLECTION_ID = "6ce12fad-0ada-41c5-9d2b-15d3a911eaa8";

	public static void main(String[] args) {

		try {


		String ImportId = "ce0ebb1e-f0bc-43a8-9df6-8b3ddf0bca24";

			getImportStatus(ImportId);
			// 1. Add Product → return productId
//			String ImportId = addProduct();
			System.out.println("ImportId = " + ImportId);


//			String productId = getProductIdByImportId(ImportId);
//			System.out.println("productId = " + productId);
//
//			// 2. Get unique version
//			String newVersion = getUniqueVersion(productId, COLLECTION_ID);
//
//			System.out.println("newVersion = " + newVersion);
//
//			// 3. Update product
//			String oldVersion = "745C9351"; // 示例：首次返回/或数据库保存
//			updateProduct(productId, COLLECTION_ID, oldVersion, newVersion);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * =========================
	 * 1. ADD PRODUCT（核心封装）
	 * =========================
	 */
	public static String addProduct() throws Exception {

	    String time = ts();

	    String url =
	            "https://efiling.saferproducts.gov/efiling/api/import"
	                    + "?certifierId=" + URLEncoder.encode(CERTIFIER_ID, StandardCharsets.UTF_8)
	                    + "&collectionId=" + URLEncoder.encode(COLLECTION_ID, StandardCharsets.UTF_8);

	    String versionId = "V" + time;

	    String json =
	            "{"
	                    + "\"productList\":[{"
	                    + "\"coreProduct\":{"

	                    + "\"versionId\":\"" + versionId + "\","
	                    + "\"primaryProductId\":\"P_" + time + "\","
	                    + "\"primaryProductIdType\":\"Model #\","
	                    + "\"certificateType\":\"GCC\","

	                    + "\"name\":\"TEST_PRODUCT_" + time + "\","
	                    + "\"tradeBrandName\":\"FOREVER\","
	                    + "\"description\":\"Created By Java API " + time + "\","

	                    + "\"manufacturer\":{"
	                    + "\"name\":\"name_"+time+"\","
	                    + "\"alternateId\":\"MFG_" + time + "\","
	                    + "\"country\":\"Japan\""
	                    + "},"

	                    + "\"manufactureDate\":\"06/2026\","

	                    + "\"lotNumber\":\"LOT_" + time + "\""

	                    + "},"

	                    + "\"directives\":{"
	                    + "\"productUpdate\":\"N\""
	                    + "}"

	                    + "}]"
	                    + "}";

	    HttpClient client = HttpClient.newHttpClient();

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Authorization", TOKEN)
	            .header("x-api-secret", API_SECRET)
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.ofString(json))
	            .build();

	    HttpResponse<String> response =
	            client.send(request, HttpResponse.BodyHandlers.ofString());

	    System.out.println("Add Product Response:");
	    System.out.println(response.body());

	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode root = mapper.readTree(response.body());

	    return root.get("importId").asText();
	}

	/**
	 * JSON 单独抽出来（避免 main 巨长）
	 */
	private static String buildAddProductJson(String versionId) {

		return "{"
				+ "\"productList\":[{"
				+ "\"coreProduct\":{"
				+ "\"versionId\":\"" + versionId + "\","
				+ "\"primaryProductId\":\"A1\","
				+ "\"primaryProductIdType\":\"Model #\","
				+ "\"certificateType\":\"GCC\","
				+ "\"name\":\"TEST PRODUCT\","
				+ "\"tradeBrandName\":\"FOREVER\","
				+ "\"description\":\"Created By Java API\","

				+ "\"manufacturer\":{"
				+ "\"name\":\"Forever Co.,Ltd.\","
				+ "\"country\":\"Japan\""
				+ "},"

				+ "\"manufactureDate\":\"06/2026\""

				+ "},"

				+ "\"directives\":{"
				+ "\"productUpdate\":\"N\""
				+ "}"

				+ "}]"
				+ "}";
	}

	/**
	 * =========================
	 * 2. GET UNIQUE VERSION
	 * =========================
	 */
	public static String getUniqueVersion(
			String productId,
			String collectionId) throws Exception {

		String url = "https://efiling.saferproducts.gov/efiling/api/product/getUniqueVersion"
				+ "?productId=" + productId
				+ "&collectionId=" + collectionId
				+ "&isGracePeriod=false";

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.GET()
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response.body());

		return root.get("version").asText();
	}

	/**
	 * =========================
	 * 3. UPDATE PRODUCT
	 * =========================
	 */
	public static void updateProduct(
			String productId,
			String collectionId,
			String oldVersion,
			String newVersion) throws Exception {

		String url = "https://efiling.saferproducts.gov/efiling/api/product/updateProduct"
				+ "?id=" + productId
				+ "&collectionId=" + collectionId
				+ "&previousVersion=" + oldVersion;

		String json = "{ \"version\":\"" + newVersion + "\" }";

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println("Update Response:");
		System.out.println(response.body());
	}

	/**
	 * Version生成
	 */
	public static String generateVersionId() {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

		return "V" + LocalDateTime.now().format(formatter);
	}


	public static String ts() {
	    return LocalDateTime.now()
	            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	}





	public static String getProductIdByImportId(String importId) throws Exception {

	    String url =
	            "https://efiling.saferproducts.gov/efiling/api/status"
	                    + "?importId=" + importId;

	    HttpClient client = HttpClient.newHttpClient();

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Authorization", TOKEN)
	            .header("x-api-secret", API_SECRET)
	            .GET()
	            .build();

	    HttpResponse<String> response =
	            client.send(request, HttpResponse.BodyHandlers.ofString());

	    System.out.println("Import Status Response:");
	    System.out.println(response.body());

	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode root = mapper.readTree(response.body());

	    /**
	     * ⚠️ 不同系统可能结构不同，这里做兼容处理
	     */

	    // 情况1：直接返回 productId
	    if (root.has("productId") && !root.get("productId").isNull()) {
	        return root.get("productId").asText();
	    }

	    // 情况2：嵌套结构（常见）
	    if (root.has("productList")) {
	        JsonNode list = root.get("productList");
	        if (list.size() > 0 && list.get(0).has("productId")) {
	            return list.get(0).get("productId").asText();
	        }
	    }

	    // 情况3：还在处理中
	    if (root.has("importStatus")) {
	        String status = root.get("importStatus").asText();
	        throw new RuntimeException("Import not ready yet. status = " + status);
	    }

	    throw new RuntimeException("productId not found in response");
	}


	public static JsonNode getImportStatus(String importId) throws Exception {

	    String url =
	            "https://efiling.saferproducts.gov/efiling/api/getImportStatus"
	                    + "?importId=" + importId;

	    HttpClient client = HttpClient.newHttpClient();

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Authorization", TOKEN)
	            .header("x-api-secret", API_SECRET)
	            .GET()
	            .build();

	    HttpResponse<String> response =
	            client.send(request, HttpResponse.BodyHandlers.ofString());

	    System.out.println("Import Status Response:");
	    System.out.println(response.body());

	    ObjectMapper mapper = new ObjectMapper();
	    return mapper.readTree(response.body());
	}

}