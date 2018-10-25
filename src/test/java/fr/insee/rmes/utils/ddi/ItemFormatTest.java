package fr.insee.rmes.utils.ddi;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import org.junit.Test;

import fr.insee.rmes.search.model.DDIItemType;

public class ItemFormatTest {

	/**
	 * This test cas check the type of a DDIInstance.
	 */
	@Test
	public void format32Test() {
		get("http://localhost:3000/items/5696739a-751c-4603-a0bc-e4c05bd41c83").then().statusCode(200).assertThat()
				.body("ItemFormat", equalTo(ItemFormat.DDI_32.toString().toLowerCase()));
		;

	}

}
