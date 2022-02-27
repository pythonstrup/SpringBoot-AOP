package com.cos.person.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateReqDto {

	@NotNull(message="password가 입력되지 않았습니다.")
	@NotBlank(message="password가 입력되지 않았습니다.")
	private String password;
	
	private String phone;
}
