package io.github.mendes.socialstudy.rest;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.mendes.socialstudy.domain.model.Follower;
import io.github.mendes.socialstudy.domain.repository.FollowerRepository;
import io.github.mendes.socialstudy.domain.repository.UserRepository;
import io.github.mendes.socialstudy.rest.dto.FollowerRequest;
import io.github.mendes.socialstudy.rest.dto.FollowerResponse;
import io.github.mendes.socialstudy.rest.dto.FollowersPerUserResponse;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {
	
	private FollowerRepository followerRepository;
	
	private UserRepository userRepository;
	
	@Inject
	public FollowerResource(FollowerRepository followerRepository, 
			UserRepository userRepository) {
		this.followerRepository = followerRepository;
		this.userRepository = userRepository;
	}
	
	@PUT
	@Transactional
	public Response follomUser(
			@PathParam("userId") Long userId, FollowerRequest followerRequest) {
		
		
		if(userId.equals(followerRequest.getFollowerId())) {
			return Response
					.status(Response.Status.CONFLICT)
					.entity("Você não pode seguir vocês mesmo")
					.build();
		}
		
		var user = userRepository.findById(userId);
		if(user == null) {
			return Response
					.status(Response.Status.NOT_FOUND).build();
		}
		
		var follower = userRepository.findById(followerRequest.getFollowerId());
		
		boolean follows = followerRepository.follows(follower, user);
		
		if(!follows) {
			var entity = new Follower();
			entity.setUser(user);
			entity.setFollower(follower);
			
			followerRepository.persist(entity);
		}
		
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@GET
	public Response listFollowers(@PathParam("userId") Long userId) {
		
		var user = userRepository.findById(userId);
		if(user == null) {
			return Response
					.status(Response.Status.NOT_FOUND).build();
		}
		
		var list = followerRepository.findByUser(userId);
		
		FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
		responseObject.setFollowerCount(list.size());
		
		var followersList = list.stream()
				.map(FollowerResponse::new)
				.collect(Collectors.toList());
		
		responseObject.setContent(followersList);
		
		return Response.ok(responseObject).build();
		
	}
	
	@DELETE
	@Transactional
	public Response unfollowUser(@PathParam("userId") 
	
	Long userId, @QueryParam("followerId") Long followerId) {
		
		var user = userRepository.findById(userId);
		
		if(user == null) {
			return Response
					.status(Response.Status.NOT_FOUND).build();
		}
		
		followerRepository.deleteByFollowerAndUser(followerId, userId);
		
		return Response
				.status(Response.Status.NO_CONTENT)
				.build();
	}
	
}
