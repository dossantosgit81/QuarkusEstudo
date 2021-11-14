package io.github.mendes.socialstudy.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.mendes.socialstudy.domain.model.Follower;
import io.github.mendes.socialstudy.domain.model.Post;
import io.github.mendes.socialstudy.domain.model.User;
import io.github.mendes.socialstudy.domain.repository.FollowerRepository;
import io.github.mendes.socialstudy.domain.repository.PostRepository;
import io.github.mendes.socialstudy.domain.repository.UserRepository;
import io.github.mendes.socialstudy.rest.dto.CreateUserPost;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
public class PostResourceTest {
	
	@Inject UserRepository repository;
	
	@Inject FollowerRepository followerRepository;
	
	@Inject PostRepository postsRepository;
	
	Long userId;
	Long userNotFollowerId;
	Long userFollowerId;
	
	@BeforeEach
	@Transactional
	public void setUP() {
		//Usuario padrão para o teste
		var user = new User();
		user.setAge(30);
		user.setName("Fulano");
		repository.persist(user);
		userId = user.getId();
		
		Post post = new Post();
		post.setText("Hello Text");
		post.setUser(user);
		postsRepository.persist(post);
		
		//Usuario que não segue ninguém
		var userNotFollower = new User();
		userNotFollower.setAge(33);
		userNotFollower.setName("Cicrano");
		repository.persist(userNotFollower);
		userNotFollowerId = userNotFollower.getId();
		
		//Usuario follower
		var userFollower = new User();
		userFollower.setAge(33);
		userFollower.setName("Cicrano");
		repository.persist(userFollower);
		userFollowerId = userFollower.getId();
		
		Follower follower = new Follower();
		follower.setUser(user);
		follower.setFollower(userFollower);
		
		followerRepository.persist(follower);
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
		var insexistentUserId = 999;
		
		RestAssured
			.given()
				.pathParam("userId", insexistentUserId)
			.when()
				.get()
			.then()
				.statusCode(404);
	}
	
	@Test
	@DisplayName("Deve retornar 400 quando followerId header não estiver presente")
	public void listPostFollowerHeaderNotFoundSendTest() {
		
		RestAssured
		.given()
			.pathParam("userId", userId)
		.when()
			.get()
		.then()
			.statusCode(400)
			.body(Matchers.is("Você esqueceu do Header"));
	}
	
	@Test
	@DisplayName("Deve retornar 400 quando follower não existe")
	public void listPostFollowerNotFoundTest() {
		
		var inexistentFollowerd = 999;
		
		RestAssured
		.given()
			.pathParam("userId", userId)
			.header("followerId", inexistentFollowerd)
		.when()
			.get()
		.then()
			.statusCode(400)
			.body(Matchers.is("Inexistent followerId"));
	}
	
	@Test
	@DisplayName("Deve retornar 403 quando o usuario não segue a pessoa na qual quer ver os posts")
	public void listPostNotAFollowerTest() {
		
		
		RestAssured
		.given()
			.pathParam("userId", userId)
			.header("followerId", userNotFollowerId)
		.when()
			.get()
		.then()
			.statusCode(403)
			.body(Matchers.is("Você não pode ver isso"));
		
	}
	
	@Test
	@DisplayName("Deve retornar posts")
	public void listPostTest() {

		RestAssured
		.given()
			.pathParam("userId", userId)
			.header("followerId", userFollowerId)
		.when()
			.get()
		.then()
			.statusCode(200)
			.body("size()", Matchers.is(1));
	}
	
}
