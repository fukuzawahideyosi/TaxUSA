package com.panda.efiling.api;



import com.fasterxml.jackson.databind.JsonNode;


public class EfilingTest extends EFilingApiUtil {



	public static void main(String[] args) {
		try {
			//TODO
			String ImportId = "";
			ImportId = importProduct(buildAddProductJson());
			//			ImportId = "ce0ebb1e-f0bc-43a8-9df6-8b3ddf0bca24";
			//			ImportId = "b6d0bd00-b51a-4f36-b356-a7ff13dec778";
			//			ImportId = "2025111b-d4be-4559-8473-c05859324f68";
			//			ImportId = "b2a79ab3-704f-4e6a-a83c-b746cee6af01";
//			ImportId = "b2a79ab3-704f-4e6a-a83c-b746cee6af01";

			Thread.sleep(1000 * 12);
			getImportLog(ImportId, false);
//			getImportStatus(ImportId);

//			String productId = "P_20260619210055";
			String productId = "d1709456-bca2-4004-af1a-b3c5dfa0f606";

			// 2. Get unique version
//			JsonNode JsonNodeValue = getUniqueVersion(productId);
//			String newVersion = JsonNodeValue.get("version").textValue();
//			System.out.println("newVersion = " + newVersion);

			// 3. Update product
			String oldVersion = "745C9351"; // 示例：首次返回/或数据库保存
//			updateProduct(productId, COLLECTION_ID, oldVersion, newVersion);

			//			getCertificates

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static JsonNode getUniqueVersion(
			String productId) throws Exception {
//https://efiling.saferproducts.gov/efiling/services/product/getUniqueVersion?productId=d1709456-bca2-4004-af1a-b3c5dfa0f606&collectionId=6ce12fad-0ada-41c5-9d2b-15d3a911eaa8&isGracePeriod=false
//https://efiling.saferproducts.gov/efiling/services/product/getUniqueVersion?productId=d1709456-bca2-4004-af1a-b3c5dfa0f606&collectionId=6ce12fad-0ada-41c5-9d2b-15d3a911eaa8&isGracePeriod=false
		String url = "https://efiling.saferproducts.gov/efiling/services/product/getUniqueVersion"
				+ "?productId=" + productId
				+ "&collectionId=" + COLLECTION_ID
				+ "&isGracePeriod=false";

		return MAPPER.readTree(get(url));
	}

}