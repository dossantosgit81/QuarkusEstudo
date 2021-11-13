package io.github.mendes.socialstudy.rest.dto;

import java.util.List;

import lombok.Data;

@Data
public class FollowersPerUserResponse {
	
	private Integer followerCount;
	
	private List<FollowerResponse> content;
	
}
