package org.jugistanbul;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;
import org.jugistanbul.dto.UserDTO;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class AuthorizedUserResourceTest
{
    private static final String ADMIN_USERNAME = "hakdogan";
    private static final String GUEST_USERNAME = "guest";
    private static final String ADMIN_ROLE_NAME = "admin";
    private static final String GUEST_ROLE_NAME = "user";
    private static final String PASSWORD = "12345";

    @Test
    public void testAllUsersEndpointNonAuthorized() {
        given()
          .when().get("/api/secured/allUsers")
          .then()
             .statusCode(401);
    }

    @Test
    public void testAllUsersEndpointAuthorized(){
        var token = getAccessToken(ADMIN_USERNAME, PASSWORD);
        given()
                .header("Authorization", token)
                .when().get("/api/secured/allUsers")
                .then()
                .statusCode(200);
    }

    @Test
    public void testFindUsersByRoleEndpointNonAuthorized(){
        given()
                .pathParam("role", ADMIN_ROLE_NAME)
                .when().get("/api/secured/findUsersByRole/{role}")
                .then()
                .statusCode(401);
    }

    @Test
    public void testFindUsersByRoleEndpointAuthorized(){
        var token = getAccessToken(ADMIN_USERNAME, PASSWORD);
        given()
                .header("Authorization", token)
                .pathParam("role", ADMIN_ROLE_NAME)
                .when().get("/api/secured/findUsersByRole/{role}")
                .then()
                .statusCode(200);
    }

    @Test
    public void testAddUserUserRole(){
        var token = getAccessToken(GUEST_USERNAME, PASSWORD);
        given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .body(getTestUser())
                .when().post("/api/secured/addUser")
                .then()
                .statusCode(401);
    }

    @Test
    public void testAddUserAdminRole(){
        var token = getAccessToken(ADMIN_USERNAME, PASSWORD);
        given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .body(getTestUser())
                .when().post("/api/secured/addUser")
                .then()
                .statusCode(200);
    }

    private UserDTO getTestUser(){
        var user = new UserDTO();
        user.setUsername("testUser");
        user.setPassword(PASSWORD);
        user.setRole(GUEST_ROLE_NAME);

        return user;
    }

    private String getAccessToken(final String username, final String password){
        ResponseBody responseBody = given()
                .pathParams("username", username, "password", password)
                .when().post("/api/signIn/{username}/{password}")
                .body();

        return "Bearer " + responseBody.prettyPrint();
    }
}
