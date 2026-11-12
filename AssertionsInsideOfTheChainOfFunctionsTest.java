package API;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AssertionsInsideOfTheChainOfFunctionsTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://v3.football.api-sports.io";
    }

    @Test
    void getTimeZone() {
        given()
                .header("Accept", "application/json")
                .header("x-apisports-key", System.getenv("API_KEY"))
        .when()
                .get("/timezone")
        .then()
                .log()
                .ifValidationFails()
                .statusCode(200)
                .body("errors", empty())
                .body("results", greaterThan(0))
                .body("response", hasItem("Europe/London"))
                .log()
                .body();
    }

    @Test
    void getCountries() {
        given()
                .header("Accept", "application/json")
                .header("x-apisports-key", System.getenv("API_KEY"))
                .queryParam("name", "England")
        .when()
                .get("/countries")
        .then()
                .statusCode(200)
                .body("errors", empty())
                .body("results", equalTo(1))
                .body("response[0].name", equalTo("England"))
                .body("response[0].code", equalTo("GB-ENG"))
                .log()
                .body();
    }

    @Test
    void getPremierLeagueById() {
        given()
                .header("Accept", "application/json")
                .header("x-apisports-key", System.getenv("API_KEY"))
                .queryParam("id", 39)
        .when()
                .get("/leagues")
        .then()
                .statusCode(200)
                .body("errors", empty())
                .body("results", equalTo(1))
                .body("response[0].league.id", equalTo(39))
                .body("response[0].league.name", equalTo("Premier League"))
                .body("response[0].league.type", equalTo("League"))
                .body("response[0].country.name", equalTo("England"))
                .body("response[0].seasons", not(empty()))
                .log()
                .body();
    }

    @Test
    void getAvailableSeasons() {
        given()
                .header("Accept", "application/json")
                .header("x-apisports-key", System.getenv("API_KEY"))
        .when()
                .get("/leagues/seasons")
        .then()
                .statusCode(200)
                .body("errors", empty())
                .body("results", greaterThan(0))
                .body("response", not(empty()))
                .body("response", hasItem(2020))
                .body("response", everyItem(instanceOf(Integer.class)))
                .log()
                .body();
    }

    @Test
    void getManchesterUnitedById() {
        given()
                .header("Accept", "application/json")
                .header("x-apisports-key", System.getenv("API_KEY"))
                .queryParam("id", 33)
        .when()
                .get("/teams")
        .then()
                .log()
                .body()
                .statusCode(200)
                .body("errors", empty())
                .body("results", equalTo(1))
                .body("response[0].team.id", equalTo(33))
                .body("response[0].team.name", equalTo("Manchester United"))
                .body("response[0].team.country",equalTo("England"))
                .body("response[0].team.founded", greaterThan(1800))
                .body("response[0].team.national", equalTo(false))
                .body("response[0].venue.name", containsString("Old Trafford"));
    }
}
