import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import fr.insee.rmes.metadata.model.Unit;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class UnitsTest {

	@Test
	public void UnitsTest() {
		Unit[] units = given().when().get("http://localhost:3000/api/meta-data/units").as(Unit[].class);
		assertNotEquals(units.length, 2);
		assertNotEquals(units.length, 1);
		assertNotEquals(units.length, 0);

	}
}
