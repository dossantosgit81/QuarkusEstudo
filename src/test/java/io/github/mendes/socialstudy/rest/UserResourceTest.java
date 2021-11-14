package io.github.mendes.socialstudy.rest;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.mendes.socialstudy.rest.dto.CreateUserRequest;
import io.github.mendes.socialstudy.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceTest {
	
	@TestHTTPResource("/users")
	URL apiURL;
	
	@Test
	@DisplayName("Deve criar um usuario com sucesso")
	@Order(1)
	public void createUserTest() {
		var user = new CreateUserRequest();
		user.setName("Fulano");
		user.setAge(30);
		
		var response = RestAssured
			.given()
				.contentType(ContentType.JSON)
				.body(user)
			.when()
				.post(apiURL)
			.then()
				.extract().response();
		
		Assertions.assertEquals(201, response.statusCode());
		Assertions.assertNotNull(response.jsonPath().getString("id"));
	}
	
	@Test
	@DisplayName("Deve retornar error quando json não é valido")
	@Order(2)
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
						.post(apiURL)
					.then().extract().response();
	 
		Assertions.assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
		Assertions.assertEquals("Validation Errors", response.jsonPath().getString("message"));
		
		List<Map<String, String>> errors = response.jsonPath().getList("errors");
		Assertions.assertNotNull(errors.get(0).get("message"));
		Assertions.assertNotNull(errors.get(1).get("message"));
//		Assertions.assertEquals("Nome é requerido", errors.get(0).get("message"));
	}
	
	@Test
	@DisplayName("Deve listar todos os usuarios")
	@Order(3)
	public void listAllUsersTest() {
		
		RestAssured
			.given()
				.contentType(ContentType.JSON)
			.when()
				.get(apiURL)
			.then()
				.statusCode(200)
				.body("size()", Matchers.is(1));	
	}

}
