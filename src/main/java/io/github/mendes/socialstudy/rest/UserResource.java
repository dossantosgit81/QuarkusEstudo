package io.github.mendes.socialstudy.rest;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.mendes.socialstudy.domain.model.User;
import io.github.mendes.socialstudy.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	
	@POST
	@Transactional
	public Response createUser(CreateUserRequest userRequest) {
		User user = new User();
		user.setAge(userRequest.getAge());
		user.setName(userRequest.getName());
		user.persist();
		return Response.ok(user).build();
	}
	
	@GET
	public Response listAllUsers() {
		PanacheQuery<User> query = User.findAll(); 
		return Response.ok(query.list()).build();
	}

}
