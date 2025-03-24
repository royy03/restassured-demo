package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RestAssuredTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void testGetRequest() {
        given()
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", notNullValue());
    }

    @Test
    public void testPostRequest() {
        String requestBody = "{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("title", equalTo("foo"))
                .body("userId", equalTo(1));
    }

    @Test
    public void testGetAllPosts() {
        given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    public void testGetNonExistentPost() {
        given()
                .when()
                .get("/posts/9999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testUpdatePostWithPut() {
        String requestBody = "{ \"id\": 1, \"title\": \"put\", \"body\": \"put update\", \"userId\": 1 }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/posts/1")
                .then()
                .statusCode(200)
                .body("title", equalTo("put"))
                .body("body", equalTo("put update"));
    }

    @Test
    public void testDeletePost() {
        given()
                .when()
                .delete("/posts/1")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetCommentsForPost() {
        given()
                .when()
                .get("/posts/1/comments")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    public void testFilterPostsByUserId() {
        given()
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("userId", everyItem(equalTo(1)));
    }
}