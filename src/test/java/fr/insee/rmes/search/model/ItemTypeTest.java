package fr.insee.rmes.search.model;

import org.junit.Test;

import fr.insee.rmes.search.model.DDIItemType;
import fr.insee.rmes.utils.ddi.ItemFormat;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ItemTypeTest {

	/**
	 * This test case check the type of a DDIInstance.
	 */
	@Test
	public void DDIInstanceTest() {
		get("http://localhost:3000/items/5696739a-751c-4603-a0bc-e4c05bd41c83").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.DDI_INSTANCE.getUUID().toLowerCase()));

	}

	/**
	 * This test case check the type of a Data Collection.
	 */
	@Test
	public void DataCollectionTest() {
		get("http://localhost:3000/items/990f2e7b-94d9-4b56-b672-ed352e0d9035").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.DATA_COLLECTION.getUUID().toLowerCase()));

	}

	/**
	 * This test case check the type of a CategoryScheme.
	 */
	@Test
	public void CategorySchemeTest() {
		get("http://localhost:3000/items/a5ca8ceb-cacb-4f1c-8d70-806fa47c3567").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.CATEGORY_SCHEME.getUUID().toLowerCase()));

	}

	/**
	 * This test case check the type of a Category.
	 */
	@Test
	public void CategoryTest() {
		get("http://localhost:3000/items/b154a162-bf6b-4742-936d-f1b65d7acef6").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.CATEGORY.getUUID().toLowerCase()));

	}

	/**
	 * This test case check the type of a Study Unit.
	 */
	@Test
	public void StudyUnitTest() {
		get("http://localhost:3000/items/f8a4332c-9bc1-4a7b-958a-05eada5f9ce1").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.STUDY_UNIT.getUUID().toLowerCase()));

	}

	/**
	 * This test case check the type of a Managed Representation Scheme.
	 */
	@Test
	public void ManagedRepresentationSchemeTest() {
		get("http://localhost:3000/items/bd923fc1-1fef-4e93-bb12-00ea22b49557").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.MANAGEDREPRESENTATION_SCHEME.getUUID().toLowerCase()));

	}

	/**
	 * This test case check the type of a Group.
	 */
	@Test
	public void GroupTest() {
		get("http://localhost:3000/items/41c2eca1-8b18-4932-809c-9330ca00fef3").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.GROUP.getUUID().toLowerCase()));

	}

	/**
	 * This test case check the type of a Group.
	 */
	@Test
	public void RessourcePackageTest() {
		get("http://localhost:3000/items/1dcb97a9-21f2-4f7e-816e-8b9fbdc4923b").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.RESSOURCEPACKAGE.getUUID().toLowerCase()));

	}

	/**
	 * This test case check the type of a QuestionScheme.
	 */
	@Test
	public void QuestionSchemeTest() {
		get("http://localhost:3000/items/08aafca2-0446-48a1-a6be-84c4f5870be7").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.QUESTION_SCHEME.getUUID().toLowerCase()));

	}
	
	/**
	 * This test case check the type of a Question.
	 */
	@Test
	public void QuestionItemTest() {
		get("http://localhost:3000/items/a115ef5f-94ed-4378-b355-43a3ac71da8e").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.QUESTION.getUUID().toLowerCase()));

	}
	
	/**
	 * This test case check the type of a CodeList Scheme.
	 */
	@Test
	public void CodeListSchemeTest() {
		get("http://localhost:3000/items/8a18935d-678c-44f0-b60e-ac82dd265926").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.CODELIST_SCHEME.getUUID().toLowerCase()));

	}
	
	/**
	 * This test case check the type of a Code List.
	 */
	@Test
	public void CodeListTest() {
		get("http://localhost:3000/items/0bb9b25c-dfb5-4c93-836b-c17c0f768843").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.CODE_LIST.getUUID().toLowerCase()));

	}
	
	/**
	 * This test case check the type of a Variable.
	 */
	@Test
	public void VariableTest() {
		get("http://localhost:3000/items/371ea872-7a1e-403a-ae84-bd6bc663bb79").then().statusCode(200).assertThat()
				.body("ItemType", equalTo(DDIItemType.VARIABLE.getUUID().toLowerCase()));

	}
	
	
	

}
