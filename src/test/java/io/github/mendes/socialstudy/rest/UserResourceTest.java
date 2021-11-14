package io.github.mendes.socialstudy.rest;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.mendes.socialstudy.rest.dto.CreateUserRequest;
import io.github.mendes.socialstudy.rest.dto.ResponseError;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
public class UserResourceTest {
	
	@Test
	@DisplayName("Deve criar um usuario com sucesso")
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
	
	@Test
	@DisplayName("Deve retornar error quando json não é valido")
	public void createUserValidationErrorTest() {
		var user = new CreateUserRequest();
		user.setAge(null);
		user.setName(null);
		
		var response = 
				RestAssured
					.given()
						.contentType(ContentType.JSON)
						.body(user)
					.when()
						.post("/users")
					.then().extract().response();
	 
		Assertions.assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
		Assertions.assertEquals("Validation Errors", response.jsonPath().getString("message"));
		
		List<Map<String, String>> errors = response.jsonPath().getList("errors");
		Assertions.assertNotNull(errors.get(0).get("message"));
		Assertions.assertNotNull(errors.get(1).get("message"));
//		Assertions.assertEquals("Nome é requerido", errors.get(0).get("message"));
	}

}
