package io.github.mendes.socialstudy.rest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateUserRequest {
	
	//Impede strings vazias e string nulas
	@NotBlank(message = "Nome é requerido")
	private String name;
	
	@NotNull(message="Idade é requerida")
	private Integer age;
	

}
