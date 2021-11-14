package io.github.mendes.socialstudy.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.mendes.socialstudy.domain.model.User;
import io.github.mendes.socialstudy.domain.repository.UserRepository;
import io.github.mendes.socialstudy.rest.dto.CreateUserPost;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
public class PostResourceTest {
	
	@Inject 
	UserRepository repository;
	Long userId;
	
	@BeforeEach
	@Transactional
	public void setUP() {
		var user = new User();
		user.setAge(30);
		user.setName("Fulano");
		repository.persist(user);
		userId = user.getId();
		
	}

	@Test
	@DisplayName("Deve criar um post para um usuario")
	public void createPostTest() {
		var postRequest = new CreateUserPost();
		postRequest.setText("Some text");
		
		RestAssured
			.given()
				.contentType(ContentType.JSON)
				.body(postRequest)
				.pathParam("userId", userId)
			.when()
				.post()
			.then()
				.statusCode(201);
	}
	
	@Test
	@DisplayName("Deve retornar 404 quando tentar cadastrar um post com usuario inexistente")
	public void PostForInexistentUserTest() {
		var postRequest = new CreateUserPost();
		postRequest.setText("Some text");
		
		var inexistenUserId = 999;
		
		RestAssured
			.given()
				.contentType(ContentType.JSON)
				.body(postRequest)
				.pathParam("userId", inexistenUserId)
			.when()
				.post()
			.then()
				.statusCode(404);
	}
	
	@Test
	@DisplayName("Deve retornar 404 quando usuario não existir")
	public void listPostUserNotFoundTest() {
		
	}
	
	@Test
	@DisplayName("Deve retornar 400 quando followerId header não estiver presente")
	public void listPostFollowerHeaderNotFoundSendTest() {
		
	}
	
	@Test
	@DisplayName("Deve retornar 400 quando followerId não existe")
	public void listPostFollowerNotFoundSendTest() {
		
	}
	
	@Test
	@DisplayName("Deve retornar 403 quando o usuario não segue a pessoa na qual quer ver os posts")
	public void listPostNotAFollowerTest() {
		
	}
	
	@Test
	@DisplayName("Deve retornar posts")
	public void listPostTest() {
		
	}
	
}
