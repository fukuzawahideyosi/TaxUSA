package com.panda.efiling;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.panda.efiling.api.EFilingApiUtil;

public class EfilingTest {
	/*

	{"businessAccountList":[{"certifierId":"ForeverCo.,Ltd.","companyName":"Forever Co., Ltd.","collectionList":[{"userRoles":["Business Account Administrator"],"collectionId":"6ce12fad-0ada-41c5-9d2b-15d3a911eaa8","collectionName":"TEST"}]}],"statusMessage":"OK","statusCode":200}
	{"businessAccountList":[{"certifierId":"ForeverCo.,Ltd.","companyName":"Forever Co., Ltd.","collectionList":[{"userRoles":["Business Account Administrator"],"collectionId":"62258ad9-d9f2-4020-b773-dd262f86a4e2","collectionName":"TEST2"},{"userRoles":["Business Account Administrator"],"collectionId":"6ce12fad-0ada-41c5-9d2b-15d3a911eaa8","collectionName":"TEST"}]}],"statusMessage":"OK","statusCode":200}

	 */

	private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJFRklMSU5HX0FQSSIsInN1YiI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIiwiZ2xvYmFsUGVybWlzc2lvbnMiOltdLCJ1c2VyRGF0YSI6eyJmaXJzdE5hbWUiOiJoaWRleW9zaGkiLCJsYXN0TmFtZSI6ImZ1a3V6YXdhIiwicGhvbmUiOiIwMDgxMDgwMzU1NjY4ODYiLCJ1c2VySWQiOiI0MTI5OWZmZS1kNjQzLTRjMGItYjM3Ny02NmNhMzY2M2RhNWQiLCJlbWFpbCI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIn0sIngtYXBpLXNlY3JldCI6Imo3aWlUV0hMYVhUUm9TWnpUdzRuV2c9PTpoekpwSlQzVEpSaUdhRXp0ZnRVTnN6MW5xNzFuaGdwR2p6ZTBqK2FSYU1FPSIsImdsb2JhbFJvbGVzIjpbXSwib2JqZWN0UGVybWlzc2lvbnMiOltdLCJ2ZXJzaW9uIjozLCJleHAiOjE3ODk1NzYxMTMsImlhdCI6MTc4MTgwMDExM30.lAUojFNFNNia-pr7AKczAKltpQnIFpwDsTp3cLYcKQtTO7rbCjznouL0bwsxKJ5DOlP5FtsDdvbA4dKNZLfFtQ";

	private static final String API_SECRET = "A68!uP86@kL568#xM868";

	private static final String CERTIFIER_ID = "ForeverCo.,Ltd.";

	private static final String COLLECTION_ID = "6ce12fad-0ada-41c5-9d2b-15d3a911eaa8";
	//	private static final String COLLECTION_ID = "62258ad9-d9f2-4020-b773-dd262f86a4e2";

	private static final String BASE_URL = "https://efiling.saferproducts.gov/efiling/api";

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final HttpClient CLIENT = HttpClient.newBuilder().build();

	private static String get(String url) throws Exception {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Authorization", TOKEN)
				.header("x-api-secret", API_SECRET)
				.GET()
				.build();

		HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body();
	}

	private static String post(String url, String json) throws Exception {

	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Authorization", TOKEN)
	            .header("x-api-secret", API_SECRET)
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.ofString(json))
	            .build();

	    HttpResponse<String> response =
	            CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

	    return response.body();
	}

	public static JsonNode getCollections() throws Exception {

		String url = BASE_URL + "/getCollections";

		String result = get(url);

		return MAPPER.readTree(result);
	}

	public static String importProduct(String json)
	        throws Exception {

	    String url =
	            BASE_URL
	            + "/import"
	            + "?certifierId=" + CERTIFIER_ID
	            + "&collectionId=" + COLLECTION_ID;

	    String result = post(url, json);

	    JsonNode root = MAPPER.readTree(result);

	    return root.path("importId").asText();
	}

	public static JsonNode getImportStatus(String importId)
	        throws Exception {

	    String url =
	            BASE_URL
	            + "/getImportStatus"
	            + "?importId="
	            + URLEncoder.encode(importId, StandardCharsets.UTF_8);

	    return MAPPER.readTree(get(url));
	}
	public static void main(String[] args) {
		try {
			JsonNode valueJSON = getCollections();
			EFilingApiUtil EFilingApiUtil = new EFilingApiUtil();

			System.out.println(valueJSON.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}