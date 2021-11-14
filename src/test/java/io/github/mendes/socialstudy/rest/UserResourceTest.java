package io.github.mendes.socialstudy.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.mendes.socialstudy.rest.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
public class UserResourceTest {
	
	@Test
	@DisplayName("Cria um usuario com sucesso")
	public void createUserTest() {
		var user = new CreateUserRequest();
		user.setName("Fulano");
		user.setAge(30);
		
		var response = RestAssured
			.given()
				.contentType(ContentType.JSON)
				.body(user)
			.when()
				.post("/users")
			.then()
				.extract().response();
		
		Assertions.assertEquals(201, response.statusCode());
		Assertions.assertNotNull(response.jsonPath().getString("id"));
	}

}
