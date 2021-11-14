package io.github.mendes.socialstudy.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.mendes.socialstudy.domain.model.Follower;
import io.github.mendes.socialstudy.domain.model.User;
import io.github.mendes.socialstudy.domain.repository.FollowerRepository;
import io.github.mendes.socialstudy.domain.repository.UserRepository;
import io.github.mendes.socialstudy.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FollowerResourceTest {
	
	@Inject UserRepository userRepository;
	
	@Inject FollowerRepository followerRepository;
	
	Long userId;
	
	Long followerId;
	
	@BeforeEach
	@Transactional
	void setUp() {
		var user = new User();
		user.setAge(30);
		user.setName("Fulano");
		userRepository.persist(user);
		userId = user.getId();
		
		var follower = new User();
		follower.setAge(30);
		follower.setName("Cicrano");
		userRepository.persist(follower);
		followerId = follower.getId();
		
		var followerEntity = new Follower();
		followerEntity.setFollower(follower);
		followerEntity.setUser(user);
		followerRepository.persist(followerEntity);
	}
	
	@Test
	@DisplayName("Deve retornar 409 quando followerIfor igual ao user id")
	@Order(1)
	public void sameUserAsFollowerTest() {
		var body = new FollowerRequest();
		
		body.setFollowerId(userId);
		
		RestAssured
			.given()
				.contentType(ContentType.JSON)
				.body(body)
				.pathParam("userId", userId)
			.when()
				.put()
			.then()
				.statusCode(Response.Status.CONFLICT.getStatusCode())
				.body(Matchers.is("Você não pode seguir vocês mesmo"));
		
	}
	
	@Test
	@DisplayName("Deve retornar 409 quando  user id não existe")
	@Order(2)
	public void userIdNotFoundTryngToFollowTeste() {
		
		var body = new FollowerRequest();
		body.setFollowerId(userId);
		
		var inexistentUserId = 999;
		
		RestAssured
			.given()
				.contentType(ContentType.JSON)
				.body(body)
				.pathParam("userId", inexistentUserId)
			.when()
				.put()
			.then()
				.statusCode(Response.Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	@DisplayName("Deve seguir um usuario com sucesso")
	@Order(3)
	public void followerUserTest() {
		
		var body = new FollowerRequest();
		body.setFollowerId(followerId);
		
		RestAssured
			.given()
				.contentType(ContentType.JSON)
				.body(body)
				.pathParam("userId", userId)
			.when()
				.put()
			.then()
				.statusCode(Response.Status.NO_CONTENT.getStatusCode());
	}
	
	@Test
	@DisplayName("Deve retornar404 on list user followers a user when User id dosent exist")
	@Order(4)
	public void userIdNotFoundListFollowTeste() {
		
		
		var inexistentUserId = 999;
		
		RestAssured
			.given()
				.contentType(ContentType.JSON)
				.pathParam("userId", inexistentUserId)
			.when()
				.get()
			.then()
				.statusCode(Response.Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	@DisplayName("Deve retornar a lista de seguidores")
	@Order(5)
	public void listFollowersTest() {
		
		var response = RestAssured
			.given()
				.contentType(ContentType.JSON)
				.pathParam("userId", userId)
			.when()
				.get()
			.then()
				.statusCode(Response.Status.OK.getStatusCode())
				.extract().response();
		var followerCount = response.jsonPath().get("followerCount");
				Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
				Assertions.assertEquals(1, followerCount);
	}	
	
	@Test
	@DisplayName("Deve retornar on unfollow user and user when User id dosent exist")
	public void userNotFOundNotFollowAUserTest() {
		
		
		var inexistentUserId = 999;
		
		RestAssured
			.given()
				.contentType(ContentType.JSON)
				.pathParam("userId", inexistentUserId)
				.queryParam("followerId", followerId)
			.when()
				.delete()
			.then()
				.statusCode(Response.Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	@DisplayName("Deve deixar de seguir um usuario")
	public void unfollowerUserTest() {
		
		RestAssured
			.given()
				.pathParam("userId", userId)
				.queryParam("followerId", followerId)
			.when()
				.delete()
			.then()
				.statusCode(Response.Status.NO_CONTENT.getStatusCode());
	}
	

}
