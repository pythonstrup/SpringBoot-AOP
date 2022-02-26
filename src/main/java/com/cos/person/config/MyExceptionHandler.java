package com.cos.person.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

// Exception 낚아채기
@RestController
@ControllerAdvice
public class MyExceptionHandler {
	
//	@ExceptionHandler(value=Exception.class)
	@ExceptionHandler(value=IllegalArgumentException.class)
	public String error(IllegalArgumentException e) {
		return "오류: " + e.getMessage().toString();
	}
}
