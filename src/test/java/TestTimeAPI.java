import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import org.junit.Test;

import static org.junit.Assert.*;
import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.http.ContentType.*;
import static org.hamcrest.Matchers.*;

public class TestTimeAPI {

    public static String API_END_POINT = "http://json-time.appspot.com/";
    public static String TIMEZONES = "timezones.json";
    public static String TIME = "time.json";

    @Test
    public void testTimezones() {
        given().
                contentType(JSON).
                log().all().
        when().
                get(API_END_POINT + TIMEZONES).
        then().
                log().all().
                statusCode(200).
                body("", hasItems("Europe/Kiev"));
    }

    @Test
    public void testTime() {
        Response response = given().
                                    contentType(JSON).
                                    log().all().
                            when().
                                    get(API_END_POINT + TIME).
                            then().
                                    log().all().
                                    statusCode(200).
                                    contentType(JSON).
                                    header("Content-Type", equalTo("text/javascript")).
                                    body("tz", equalTo("UTC")).
                                    body("error", equalTo(false)).
                            extract().
                                    response();

        JsonPath jsonPath = new JsonPath(response.asString());

        String hourString = jsonPath.getString("hour");
        try {
            Integer hour = Integer.valueOf(hourString);
            assertTrue("Attribute hour has wrong format", hour.toString().equals(hourString));
            assertTrue("Value for hour attribute can not be less than 0 and grate than 23, but: " + hourString + " was given", hour.intValue() >= 0 && hour.intValue() <= 23);
        } catch (NumberFormatException e) {
            fail("Problem during parsing value: " + hourString + " was happen" + e.toString());
        }
    }
}