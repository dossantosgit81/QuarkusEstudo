package io.github.mendes.socialstudy.rest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateUserRequest {
	
	//Impede strings vazia e string nulas
	@NotBlank(message = "Nome é requerido")
	private String name;
	
	@NotNull(message="Idade é requerida")
	private Integer age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	
	

}
