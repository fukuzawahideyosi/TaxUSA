package com.panda.efiling;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class AddProductTest {
	/*

	{"businessAccountList":[{"certifierId":"ForeverCo.,Ltd.","companyName":"Forever Co., Ltd.","collectionList":[{"userRoles":["Business Account Administrator"],"collectionId":"6ce12fad-0ada-41c5-9d2b-15d3a911eaa8","collectionName":"TEST"}]}],"statusMessage":"OK","statusCode":200}
	{"businessAccountList":[{"certifierId":"ForeverCo.,Ltd.","companyName":"Forever Co., Ltd.","collectionList":[{"userRoles":["Business Account Administrator"],"collectionId":"62258ad9-d9f2-4020-b773-dd262f86a4e2","collectionName":"TEST2"},{"userRoles":["Business Account Administrator"],"collectionId":"6ce12fad-0ada-41c5-9d2b-15d3a911eaa8","collectionName":"TEST"}]}],"statusMessage":"OK","statusCode":200}

	 */

	private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJFRklMSU5HX0FQSSIsInN1YiI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIiwiZ2xvYmFsUGVybWlzc2lvbnMiOltdLCJ1c2VyRGF0YSI6eyJmaXJzdE5hbWUiOiJoaWRleW9zaGkiLCJsYXN0TmFtZSI6ImZ1a3V6YXdhIiwicGhvbmUiOiIwMDgxMDgwMzU1NjY4ODYiLCJ1c2VySWQiOiI0MTI5OWZmZS1kNjQzLTRjMGItYjM3Ny02NmNhMzY2M2RhNWQiLCJlbWFpbCI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIn0sIngtYXBpLXNlY3JldCI6Imo3aWlUV0hMYVhUUm9TWnpUdzRuV2c9PTpoekpwSlQzVEpSaUdhRXp0ZnRVTnN6MW5xNzFuaGdwR2p6ZTBqK2FSYU1FPSIsImdsb2JhbFJvbGVzIjpbXSwib2JqZWN0UGVybWlzc2lvbnMiOltdLCJ2ZXJzaW9uIjozLCJleHAiOjE3ODk1NzYxMTMsImlhdCI6MTc4MTgwMDExM30.lAUojFNFNNia-pr7AKczAKltpQnIFpwDsTp3cLYcKQtTO7rbCjznouL0bwsxKJ5DOlP5FtsDdvbA4dKNZLfFtQ";

	private static final String API_SECRET = "A68!uP86@kL568#xM868";

	private static final String CERTIFIER_ID = "ForeverCo.,Ltd.";

	private static final String COLLECTION_ID = "6ce12fad-0ada-41c5-9d2b-15d3a911eaa8";
//	private static final String COLLECTION_ID = "62258ad9-d9f2-4020-b773-dd262f86a4e2";

	public static void main(String[] args) {

		try {

			//        	getCollections();

			String url = "https://efiling.saferproducts.gov/efiling/api/import"
					+ "?certifierId="
					+ URLEncoder.encode(
							CERTIFIER_ID,
							StandardCharsets.UTF_8)
					+ "&collectionId="
					+ URLEncoder.encode(
							COLLECTION_ID,
							StandardCharsets.UTF_8);

			String json = "{"
					+ "\"productList\":["
					+ "{"
					+ "\"coreProduct\":{"

					+ "\"versionId\":\"V2\","

					+ "\"primaryProductId\":\"A3\","

					+ "\"primaryProductIdType\":\"Model #\","

					+ "\"certificateType\":\"GCC\","

					+ "\"name\":\"TEST PRODUCT\","

					+ "\"tradeBrandName\":\"FOREVER\","

					+ "\"description\":\"Created By Java API\","

					+ "\"manufacturer\":{"
					+ "\"gln\":\"\","
					+ "\"alternateId\":\"FOREVER-MFG\","
					+ "\"sbmId\":\"\","
					+ "\"name\":\"Forever Co.,Ltd.\","
					+ "\"addressLine1\":\"Tokyo\","
					+ "\"addressLine2\":\"\","
					+ "\"aptNumber\":\"\","
					+ "\"city\":\"Tokyo\","
					+ "\"stateProvince\":\"Tokyo\","
					+ "\"country\":\"Japan\","
					+ "\"postalCode\":\"1000001\","
					+ "\"phone\":\"0000000000\","
					+ "\"email\":\"test@test.com\""
					+ "},"

					+ "\"manufactureDate\":\"06/2026\","

					+ "\"productionStartDate\":\"06/01/2026\","

					+ "\"productionEndDate\":\"06/30/2026\","

					+ "\"lotNumber\":\"LOT001\","

					+ "\"lotNumberAssignedBy\":\"Seller\","

					+ "\"lastTestDate\":\"06/18/2026\","

					+ "\"labs\":["
					+ "{"
					+ "\"type\":\"LAB\","
					+ "\"cpscId\":\"\","
					+ "\"gln\":\"\","
					+ "\"alternateId\":\"TEST-LAB\","
					+ "\"name\":\"TEST LAB\","
					+ "\"addressLine1\":\"Tokyo\","
					+ "\"addressLine2\":\"\","
					+ "\"aptNumber\":\"\","
					+ "\"city\":\"Tokyo\","
					+ "\"stateProvince\":\"Tokyo\","
					+ "\"country\":\"Japan\","
					+ "\"postalCode\":\"1000001\","
					+ "\"phone\":\"0000000000\","
					+ "\"email\":\"lab@test.com\","
					+ "\"citationCodes\":[\"1201\"],"
					+ "\"testReportId\":\"RPT001\","
					+ "\"testURL\":\"https://example.com/report\","
					+ "\"testReportAccessKey\":\"\","
					+ "\"isComponent\":false,"
					+ "\"componentDescription\":\"\""
					+ "}"
					+ "],"

					+ "\"exemptions\":[],"

					+ "\"poc\":{"
					+ "\"type\":\"Importer\","
					+ "\"gln\":\"\","
					+ "\"alternateId\":\"\","
					+ "\"name\":\"\","
					+ "\"addressLine1\":\"\","
					+ "\"addressLine2\":\"\","
					+ "\"aptNumber\":\"\","
					+ "\"city\":\"\","
					+ "\"stateProvince\":\"\","
					+ "\"country\":\"\","
					+ "\"postalCode\":\"\","
					+ "\"phone\":\"\","
					+ "\"email\":\"\""
					+ "}"

					+ "},"

					+ "\"directives\":{"

					+ "\"productUpdate\":\"N\","

					+ "\"versionIdToUpdate\":\"\","

					+ "\"manufacturer\":{"
					+ "\"isNew\":\"Y\","
					+ "\"gln\":\"\","
					+ "\"alternateId\":\"FOREVER-MFG\""
					+ "},"

					+ "\"labs\":["
					+ "{"
					+ "\"isNew\":\"Y\","
					+ "\"gln\":\"\","
					+ "\"alternateId\":\"TEST-LAB\""
					+ "}"
					+ "],"

					+ "\"poc\":{"
					+ "\"isNew\":\"N\","
					+ "\"gln\":\"\","
					+ "\"alternateId\":\"\""
					+ "}"

					+ "}"

					+ "}"

					+ "]"
					+ "}";

			HttpClient client = HttpClient.newBuilder()
					.version(HttpClient.Version.HTTP_1_1)
					.build();

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("Authorization", TOKEN)
					.header("x-api-secret", API_SECRET)
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			System.out.println("====================================");
			System.out.println("Request URL:");
			System.out.println(url);
			System.out.println("====================================");

			HttpResponse<String> response = client.send(
					request,
					HttpResponse.BodyHandlers.ofString());

			System.out.println("HTTP Status:");
			System.out.println(response.statusCode());

			System.out.println("Response:");
			System.out.println(response.body());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getCollections() throws Exception {

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://efiling.saferproducts.gov/efiling/api/getCollections"))
				.header("Authorization", TOKEN)
				.header("x-api-secret", API_SECRET)
				.GET()
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println(response.body());
	}

	/**
	 * 获取系统自动生成的 Version ID
	 */
	public static String getNextVersion() throws Exception {

		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(
						"https://efiling.saferproducts.gov/efiling/api/nextVersion"))
				.header("Authorization", TOKEN)
				.header("x-api-secret", API_SECRET)
				.GET()
				.build();

		HttpResponse<String> response = client.send(
				request,
				HttpResponse.BodyHandlers.ofString());

		System.out.println("HTTP Status: "
				+ response.statusCode());

		System.out.println("Response:");
		System.out.println(response.body());

		return response.body();
	}
}