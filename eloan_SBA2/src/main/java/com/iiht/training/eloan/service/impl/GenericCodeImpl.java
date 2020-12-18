package com.iiht.training.eloan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanDto;
import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;

@Service
public class GenericCodeImpl {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository pProcessingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	// utility method
		public UserDto convertEntityToUsersDto(Users users) {
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
		
		public Users convertInputDtoToEntity(UserDto userDto) {
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
	
		public LoanOutputDto convertEntityToLoanOutPutDto(Loan loan) {
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
			
			
			loanOutputDto.setLoanDto(loanDto);
			
			return loanOutputDto;
			
		}
}
