package org.jugistanbul;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class AuthorizedUserResourceTest
{
    @Test
    public void testAllUsersEndpoint() {
        given()
          .when().get("/api/secured/allUsers")
          .then()
             .statusCode(401);
    }
}
