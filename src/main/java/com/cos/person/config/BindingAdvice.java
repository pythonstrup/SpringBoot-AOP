package com.cos.person.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cos.person.domain.CommonDto;

import io.sentry.Sentry;

// @Controller, @RestController, @Component, @Configuration
// @Configuration => 설정할 때 사용, 그 외에는 @Component
@Component
@Aspect
public class BindingAdvice {

	private static final Logger log = LoggerFactory.getLogger(BindingAdvice.class);
	
	@Before("execution(* com.cos.person.web..*Controller.*(..))") 
	public void testCheck() {
		
		// request 값을 처리하는법		
		HttpServletRequest request = 
				((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		System.out.println("주소: "+request.getRequestURI());

		// log 처리는? 파일로 어떻게 남기나?
		System.out.println("전처리 로그를 남겼습니다.");
	}
	
	@After("execution(* com.cos.person.web..*Controller.*(..))") 
	public void testCheck2() {
		System.out.println("후처리 로그를 남겼습니다.");
	}
	
	// 함수: 전후처리 모두 or 전처리만 or 후처리만
	//@Before
	//@After
	@Around("execution(* com.cos.person.web..*Controller.*(..))")  // web패키지에 있는 'Controller'로 끝나는 모든 클래스의 모든 메소드 전후처리하기
	public Object validCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		String type = proceedingJoinPoint.getSignature().getDeclaringTypeName();
		String method = proceedingJoinPoint.getSignature().getName();
		
		System.out.println("type: "+type);
		System.out.println("method: "+method);
		
		Object[] args = proceedingJoinPoint.getArgs();
		for (Object arg : args) {
			if (arg instanceof BindingResult) {
				BindingResult bindingResult = (BindingResult) arg;
				
				// 서비스: 정상적인 화면 -> 사용자 요청
				if (bindingResult.hasErrors()) {
					Map<String, String> errorMap = new HashMap<>();
					
					for (FieldError error: bindingResult.getFieldErrors()) {
						errorMap.put(error.getField(), error.getDefaultMessage());
						
						// 로그 레벨(심각도 순서): error - warn - info - debug
						log.warn(type+"."+method+"() => 필드: "+error.getField()+", 메시지: "+error.getDefaultMessage());
						Sentry.captureMessage(type+"."+method+"() => 필드: "+error.getField()+", 메시지: "+error.getDefaultMessage());
						
						// DB 연결 -> DB 남기기
						// File file = new File(); ===> 좋은 방식은 아님.
					}
					
					return new CommonDto<>(HttpStatus.BAD_REQUEST.value(), errorMap);
				}
			}
		}
		return proceedingJoinPoint.proceed(); // 함수의 스택을 실행하라.
	}
}
