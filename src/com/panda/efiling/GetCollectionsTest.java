package com.panda.efiling;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GetCollectionsTest {

    public static void main(String[] args) throws Exception {
/*

{"businessAccountList":[{"certifierId":"ForeverCo.,Ltd.","companyName":"Forever Co., Ltd.","collectionList":[{"userRoles":["Business Account Administrator"],"collectionId":"6ce12fad-0ada-41c5-9d2b-15d3a911eaa8","collectionName":"TEST"}]}],"statusMessage":"OK","statusCode":200}

 */
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJFRklMSU5HX0FQSSIsInN1YiI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIiwiZ2xvYmFsUGVybWlzc2lvbnMiOltdLCJ1c2VyRGF0YSI6eyJmaXJzdE5hbWUiOiJoaWRleW9zaGkiLCJsYXN0TmFtZSI6ImZ1a3V6YXdhIiwicGhvbmUiOiIwMDgxMDgwMzU1NjY4ODYiLCJ1c2VySWQiOiI0MTI5OWZmZS1kNjQzLTRjMGItYjM3Ny02NmNhMzY2M2RhNWQiLCJlbWFpbCI6ImZ1a3V6YXdhaGlkZXlvc2lAZ21haWwuY29tIn0sIngtYXBpLXNlY3JldCI6Imo3aWlUV0hMYVhUUm9TWnpUdzRuV2c9PTpoekpwSlQzVEpSaUdhRXp0ZnRVTnN6MW5xNzFuaGdwR2p6ZTBqK2FSYU1FPSIsImdsb2JhbFJvbGVzIjpbXSwib2JqZWN0UGVybWlzc2lvbnMiOltdLCJ2ZXJzaW9uIjozLCJleHAiOjE3ODk1NzYxMTMsImlhdCI6MTc4MTgwMDExM30.lAUojFNFNNia-pr7AKczAKltpQnIFpwDsTp3cLYcKQtTO7rbCjznouL0bwsxKJ5DOlP5FtsDdvbA4dKNZLfFtQ";
        String secret = "A68!uP86@kL568#xM868";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://efiling.saferproducts.gov/efiling/api/getCollections"))
                .header("Authorization", token)
                .header("x-api-secret", secret)
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }
}