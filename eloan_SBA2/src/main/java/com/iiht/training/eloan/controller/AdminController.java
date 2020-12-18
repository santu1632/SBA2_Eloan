package com.iiht.training.eloan.controller;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.ExceptionResponse;
import com.iiht.training.eloan.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	
	@PostMapping("/register-clerk")
	public UserDto registerClerk(@RequestBody UserDto userDto){

		UserDto userdto = this.adminService.registerClerk(userDto);
		return userdto;
	}
	
	@PostMapping("/register-manager")
	public UserDto registerManager(@RequestBody UserDto userDto){
		
		UserDto userdto = this.adminService.registerManager(userDto);
		return userdto;
	}
	
	@GetMapping("/all-clerks")
	public List<UserDto> getAllClerks(){

		 List<UserDto> userDto = this.adminService.getAllClerks();
		 
		
		return userDto;
	}
	
	@GetMapping("/all-managers")
	public List<UserDto> getAllManagers(){
List<UserDto> userDto = this.adminService.getAllManagers();
		 
		
		return userDto;
	}
	
	@ExceptionHandler(ConstraintViolationException.class)

	public ResponseEntity<ExceptionResponse> handler(ConstraintViolationException ex){
		ExceptionResponse exception = 
				new ExceptionResponse(ex.getMessage(),
									  System.currentTimeMillis(),
									  HttpStatus.NOT_FOUND.value());
		ResponseEntity<ExceptionResponse> response =
				new ResponseEntity<ExceptionResponse>(exception, HttpStatus.NOT_FOUND);
		return response;
	}
}
