package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SearchApiTest {
    private static String API_KEY;
    private static String SITE_KEY;


    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://search-dr.unbxd.io";
        API_KEY = System.getProperty("apiKey"); //extracted from the cli via -- mvn test -DapiKey=4c26a092be20e0a237b91e51087453fa -DsiteKey=dev1-gymboree-com800681562072113
        SITE_KEY = System.getProperty("siteKey");
        if (API_KEY == null || SITE_KEY == null) {
            throw new IllegalArgumentException("API Key and Site Key not correct");
        }
        System.out.println("Using API_KEY: " + API_KEY + ", SITE_KEY: " + SITE_KEY);

    }

    @Test
    public void testMultipleQueries() {
        List<String> queries = Arrays.asList("*", "shoes", "t-shirt", "jeans", "jacket");   //2 ways of running multiple samples -- via array and parameterized test

        for (String query : queries) {
            System.out.println("Testing query: " + query);

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/" + API_KEY + "/" + SITE_KEY + "/search?q=" + query)
                    .then()
                    .statusCode(200)
                    .body("searchMetaData.status", equalTo(0))
                    .body("response", notNullValue());  //validate the response
        }
    }

    @ParameterizedTest
    @CsvSource({
            "\"*\", \"categoryPath2_uFilter:Clearance\"",
            "\"shoes\", \"TCPColor_uFilter:GS_10-12\"",
            "\"jeans\", \"gender_uFilter:Men\"",    //running multiple queries which have an expected success output
    })
    public void testSearchWithFilters(String query, String filter) {
        System.out.println("Testing query: " + query + " with filter: " + filter);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + API_KEY + "/" + SITE_KEY + "/search?q=" + query + filter)
                .then()
                .statusCode(200)
                .body("searchMetaData.status", equalTo(0))
                .body("response.numberOfProducts", greaterThan(0)); // Ensures valid results
    }
    @ParameterizedTest
    @CsvSource({
            "\"*\", \"category:abc\"",  //not existing filter thus 0 products
    })
    public void testSearchWithInvalidFilters(String query, String filter) {
        System.out.println("Testing query: " + query + " with filter: " + filter);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/" + API_KEY + "/" + SITE_KEY + "/search?q=" + query + filter)
                .then()
                .statusCode(200)
                .body("searchMetaData.status", equalTo(0))
                .body("response.numberOfProducts", equalTo(0)); // Ensures no output
    }

  @ParameterizedTest
  @CsvSource({"'*', 'favoritedcount', 'desc'"})
  public void testSearchWithSorting(String query, String sortField, String sortOrder) { //test case for sorting descending using query parameters
    System.out.println(
        "Testing query: " + query + " with sorting by: " + sortField + " " + sortOrder);

    given()
        .contentType(ContentType.JSON)
        .queryParam("q", "*")
        .queryParam("fields", "favoritedcount")
        .queryParam("rows", 0)
         .queryParam("sort", "favoritedcount desc")
        .when()
        .get("/" + API_KEY + "/" + SITE_KEY + "/search")
        .then()
        .statusCode(200)
        .body("searchMetaData.status", equalTo(0))
        .body("response.products", not(emptyArray()));
  }

}
