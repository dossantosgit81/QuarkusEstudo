package io.github.mendes.socialstudy.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.mendes.socialstudy.domain.model.Post;
import io.github.mendes.socialstudy.domain.model.User;
import io.github.mendes.socialstudy.domain.repository.PostRepository;
import io.github.mendes.socialstudy.domain.repository.UserRepository;
import io.github.mendes.socialstudy.rest.dto.CreateUserPost;
import io.github.mendes.socialstudy.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
	
	private UserRepository userRepository;
	private PostRepository postRepository;
	
	@Inject
	public PostResource(UserRepository userRepository, PostRepository postRepository) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
	}
	
	@POST
	@Transactional
	public Response savePost(@PathParam("userId") Long userId,
			CreateUserPost createUserPost) {
		User user = userRepository.findById(userId);
		
		if(user == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		Post post = new Post();
		post.setText(createUserPost.getText());
		post.setUser(user);
		
		postRepository.persist(post);
		
		return Response.status(Response.Status.CREATED).build();
	}
	
	@GET
	public Response listPosts(@PathParam("userId") Long userId) {
		User user = userRepository.findById(userId);
		
		if(user == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		PanacheQuery<Post> query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending) ,user);
		
		var list = query.list();
		
		List<PostResponse> postList = list.stream().map(post -> PostResponse.fromEntity(post)).collect(Collectors.toList());
		
		return Response.ok(postList).build();
	}

}
