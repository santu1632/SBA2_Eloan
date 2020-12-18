package com.iiht.training.eloan.controller;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.dto.exception.ExceptionResponse;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.exception.InvalidDataException;
import com.iiht.training.eloan.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@PostMapping("/register")
	public UserDto register(@RequestBody UserDto userDto){
		UserDto userdto = this.customerService.register(userDto);
		
		
		return userdto;
	}
	
	@PostMapping("/apply-loan/{customerId}")
	public LoanOutputDto applyLoan(@PathVariable Long customerId,
												 @RequestBody LoanDto loanDto){
		
		LoanOutputDto loanOutputDto = this.customerService.applyLoan(customerId, loanDto);
		return loanOutputDto;
	}
	
	@GetMapping("/loan-status/{loanAppId}")
	public LoanOutputDto getStatus(@PathVariable Long loanAppId){
		
		LoanOutputDto loanOutputDto = this.customerService.getStatus(loanAppId);
		
		return loanOutputDto;
	}
	
	@GetMapping("/loan-status-all/{customerId}")
	public List<LoanOutputDto> getStatusAll(@PathVariable Long customerId){
		List<LoanOutputDto> loanOutputDto = this.customerService.getStatusAll(customerId);
		
		return loanOutputDto;
	}
	
	@ExceptionHandler(CustomerNotFoundException.class)

	public ResponseEntity<ExceptionResponse> handler(CustomerNotFoundException ex){
		ExceptionResponse exception = 
				new ExceptionResponse(ex.getMessage(),
									  System.currentTimeMillis(),
									  HttpStatus.NOT_FOUND.value());
		ResponseEntity<ExceptionResponse> response =
				new ResponseEntity<ExceptionResponse>(exception, HttpStatus.NOT_FOUND);
		return response;
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
