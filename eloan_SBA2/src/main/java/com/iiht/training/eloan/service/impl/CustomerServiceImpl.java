package com.iiht.training.eloan.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.exception.InvalidDataException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.CustomerService;


@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository pProcessingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	
	// utility method
	private UserDto convertEntityToUsersDto(Users users) {
		UserDto userDto = new UserDto();
		userDto.setId(users.getId());
		userDto.setFirstName(users.getFirstName());
		userDto.setLastName(users.getLastName());
		userDto.setEmail(users.getEmail());
		userDto.setMobile(users.getMobile());
		userDto.setRole(users.getRole());
		userDto.setUserName(users.getUserName());
		userDto.setPassword(users.getPassword());
		
		return userDto;
	}
	
	private Users convertInputDtoToEntity(UserDto userDto) {
		Users user = new Users();
		user.setId(userDto.getId());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		user.setMobile(userDto.getMobile());
		user.setRole(userDto.getRole());
		user.setUserName(userDto.getUserName());
		user.setPassword(userDto.getPassword());
		
		return user;
	}
	
	
	private LoanOutputDto convertEntityToLoanOutPutDto(Loan loan) {
		LoanOutputDto loanOutputDto = new LoanOutputDto();
		LoanDto loanDto = new LoanDto();
		UserDto userDto = new UserDto();
		
		
		
		loanOutputDto.setLoanAppId(loan.getId());
		loanOutputDto.setCustomerId(loan.getCustomerId());
		
		loanDto.setBusinessStructure(loan.getBusinessStructure());
		loanDto.setLoanName(loan.getLoanName());
		loanDto.setLoanAmount(loan.getLoanAmount());
		loanDto.setBillingIndicator(loan.getBillingIndicator());
		loanDto.setLoanApplicationDate(loan.getLoanApplicationDate());
		loanDto.setBusinessStructure(loan.getBusinessStructure());
		loanDto.setTaxIndicator(loan.getTaxIndicator());
		
		
		
		
		int status = loan.getStatus();
		
		switch (status) {
		case 0 : 
			loanOutputDto.setStatus("Applied");
			break;
		case 1 : 
			loanOutputDto.setStatus("Processed");
			break;
		case 2 : 
			loanOutputDto.setStatus("Sanctioned");
			break;
		case -1 : 
			loanOutputDto.setStatus("Rejected");
			break;
		 default:
			loanOutputDto.setStatus(""); 
		}
		
		loanOutputDto.setUserDto(userDto);
		loanOutputDto.setLoanDto(loanDto);
		
		return loanOutputDto;
		
	}
	
	private Loan convertLoanDtoToEntity(LoanDto loanDto) {
			Loan loan = new Loan();
		
		try {
			
			loan.setLoanName(loanDto.getLoanName());
			loan.setLoanAmount(loanDto.getLoanAmount());
			loan.setLoanApplicationDate(loanDto.getLoanApplicationDate());
			loan.setBillingIndicator(loanDto.getBillingIndicator());
			loan.setBusinessStructure(loanDto.getBusinessStructure());
			loan.setTaxIndicator(loanDto.getTaxIndicator());
		} catch (Exception e) {
			throw new InvalidDataException("Invalid Data " + e.getMessage());
		}
			
		return loan;
		
	}
	
		
	@Override
	public UserDto register(UserDto userDto) {
		
		
		GenericCodeImpl genericCode = new GenericCodeImpl();
		
		Users users= genericCode.convertInputDtoToEntity(userDto);
		
		Users newUsers=this.usersRepository.save(users);
		
		UserDto newUserDto = genericCode.convertEntityToUsersDto(newUsers);
		
		return newUserDto;
	}
	
	
	@Override
	public LoanOutputDto applyLoan(Long customerId, LoanDto loanDto) {
		
		Loan loan = this.convertLoanDtoToEntity(loanDto);
		
		loan.setCustomerId(customerId);
		loan.setStatus(0);
		loan.setLoanApplicationDate(String.valueOf(LocalDateTime.now()));
		
		
		
		Loan newLoan = this.loanRepository.save(loan);
		
		LoanOutputDto newLoanOutputDto = this.convertEntityToLoanOutPutDto(newLoan);
		
		return newLoanOutputDto;
	}

	@Override
	public LoanOutputDto getStatus(Long loanAppId) {
		
		Loan newLoan =this.loanRepository.findById(loanAppId).orElse(null);
		
				
		LoanOutputDto newLoanOutputDto = this.convertEntityToLoanOutPutDto(newLoan);
		
		return newLoanOutputDto;
	}

	@Override
	public List<LoanOutputDto> getStatusAll(Long customerId) {
		
		
		List<Loan> loans = this.loanRepository.findAllByCustomerId(customerId);
		
		
		List<LoanOutputDto> loanOutputDtos = 
				loans.stream()
				 .map(this :: convertEntityToLoanOutPutDto)
				 .collect(Collectors.toList());
		
		if(loans.isEmpty()) {
			throw new CustomerNotFoundException("Customer Not Found");
		}
		
		
		return loanOutputDtos;
	}

}
