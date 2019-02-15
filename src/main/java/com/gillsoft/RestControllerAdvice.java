package com.gillsoft;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gillsoft.matrix.model.Response;

@ControllerAdvice
@RestController
public class RestControllerAdvice {
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Response<String> allExceptions(Exception e) {
		Response<String> response = new Response<>();
		response.setStatus(false);
		response.setMessage(e.getMessage());
		return response;
	}

}
