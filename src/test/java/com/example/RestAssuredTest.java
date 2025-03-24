package com.example;

import io.restassured.RestAssured;  //Main class for testing which provides the methods given(), when(), then() and base uri is set
import io.restassured.http.ContentType; //Specifies the format of response like json(in our case), xml, etc.
import org.junit.jupiter.api.BeforeAll; //Runs only once before all tests
import org.junit.jupiter.api.Test;  //To distinguish between test case methods and rest
import static io.restassured.RestAssured.*; //So that we dont have to add prefix of RestAssured everywhere with given(), then(), etc.
import static org.hamcrest.Matchers.*;  //Validate json using assertions like equalTo()

public class RestAssuredTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";   //Mock api used for testing
    }

    @Test
    public void testGetRequest() {  //get request to check if there is a valid response
        given() //used for request body, headers and authorization, etc., not specified in this example
                .when() // initiates the execution of the request
                .get("/posts/1")    //sends a get request to https://jsonplaceholder.typicode.com/posts/1
                .then() //after executing the request it validates if the outcome statuscode is as expected
                .statusCode(200)    //since there is a successful response in the api, this is true else test fails
                .body("id", equalTo(1)) //after validating that the response is valid, check if body of response is as expected using matcher
                .body("userId", equalTo(1))
                .body("title", notNullValue());
    }

    @Test
    public void testPostRequest() { //post request to validate if output matches the data posted
        String requestBody = "{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }";    //request in json format similar to data in the mock api

        given()
                .contentType(ContentType.JSON)// the request body format is json
                .body(requestBody)  //request body sent in given section before executing the request
                .when()
                .post("/posts") //post request sent to https://jsonplaceholder.typicode.com/posts
                .then()
                .statusCode(201)    //response for successful creation of record
                .body("title", equalTo("foo"))  //validate the response body posted
                .body("userId", equalTo(1));
    }

    @Test
    public void testGetAllPosts() {
        given()
                .when()
                .get("/posts")  //sends a get request to /posts
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));    //makes sure the response has atleast one record
    }

    @Test
    public void testGetNonExistentPost() {  //validate invalid requests
        given()
                .when()
                .get("/posts/9999")
                .then()
                .statusCode(404);   //not found, matches the expected output
    }

    @Test
    public void testUpdatePostWithPut() {   //put request to update certain sections of existing post
        String requestBody = "{ \"id\": 1, \"title\": \"put\", \"body\": \"put update\", \"userId\": 1 }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/posts/1")    //replaces only the content in the respone with one sent in request body
                .then()
                .statusCode(200)
                .body("title", equalTo("put"))
                .body("body", equalTo("put update"));   //ensures the response contains the data as in the request body
    }

    @Test
    public void testDeletePost() { //delete request to remove specific id if present
        given()
                .when()
                .delete("/posts/1")
                .then()
                .statusCode(200);   //successfully deleted the post with id 1
    }

    @Test
    public void testGetCommentsForPost() {  //similar to testGetAllPosts
        given()
                .when()
                .get("/posts/1/comments")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    public void testFilterPostsByUserId() { //get request to filter posts by a specific userId using query parameter
        given()
                .queryParam("userId", 1)    //query parameter added thus the request url points to /posts?userId=1
                .when()
                .get("/posts")
                .then()
                .statusCode(200)    //valid response was present
                .body("userId", everyItem(equalTo(1))); //ensures the id of every item is 1 which was passed in query parameter
    }
}