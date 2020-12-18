package com.iiht.training.eloan.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.ProcessingDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.ProcessingInfo;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.AlreadyProcessedException;
import com.iiht.training.eloan.exception.ClerkNotFoundException;
import com.iiht.training.eloan.exception.CustomerNotFoundException;
import com.iiht.training.eloan.exception.InvalidDataException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ClerkService;

@Service
public class ClerkServiceImpl implements ClerkService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository pProcessingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	public ProcessingInfo convertProcessingDtoToEntity(Long clerkId, ProcessingDto processingDto, Long loanAppId) {
	
		ProcessingInfo process = new ProcessingInfo();
	
		
		try {
		process.setLoanAppId(loanAppId);
		process.setLoanClerkId(clerkId);	
		process.setAcresOfLand(processingDto.getAcresOfLand());
		process.setAddressOfProperty(processingDto.getAddressOfProperty());
		process.setAppraisedBy(processingDto.getAppraisedBy());
		process.setLandValue(processingDto.getLandValue());
		process.setValuationDate(processingDto.getValuationDate());
		process.setSuggestedAmountOfLoan(processingDto.getSuggestedAmountOfLoan());
		

		} catch (Exception e) {
			throw new InvalidDataException("Invalid Data " + e.getMessage());
		}
		
		return process;
		
	}
	
	public ProcessingDto convertEntityToProcessingDto(ProcessingInfo processingInfo) {
		
		ProcessingDto processingDto = new ProcessingDto();
		processingDto.setAcresOfLand(processingInfo.getAcresOfLand());
		processingDto.setAddressOfProperty(processingInfo.getAddressOfProperty());
		processingDto.setAppraisedBy(processingInfo.getAppraisedBy());
		processingDto.setLandValue(processingInfo.getLandValue());
		
		processingDto.setSuggestedAmountOfLoan(processingInfo.getSuggestedAmountOfLoan());
		processingDto.setValuationDate(processingInfo.getValuationDate());
		
		
		return processingDto;
	}
	
	@Override
	public List<LoanOutputDto> allAppliedLoans() {
		
		GenericCodeImpl genericCode = new GenericCodeImpl();
		
		List<Loan> loans = this.loanRepository.findAllByStatus(0);
		
		List<LoanOutputDto> loanOutputDtos = 
				loans.stream()
				 .map(genericCode :: convertEntityToLoanOutPutDto)
				 .collect(Collectors.toList());
		
		
		if(loans.isEmpty()) {
			throw new LoanNotFoundException("No Loans Avaiable for Processing");
		}
		
		return loanOutputDtos;
	}
	
	@Override
	@Transactional
	public ProcessingDto processLoan(Long clerkId, Long loanAppId, ProcessingDto processingDto) {

			ProcessingInfo process = this.convertProcessingDtoToEntity(clerkId, processingDto, loanAppId);
			
			Loan loan = this.loanRepository.getStatus(loanAppId);
			
			Users user = this.usersRepository.getRole(clerkId);
			
			String role= user.getRole();
			
			if (role.contains("Clerk")) {

				if (loan.getStatus() == 0) {
					ProcessingInfo newProcessingInfo = this.pProcessingInfoRepository.save(process);

					ProcessingDto newProcessingInfoDto = this.convertEntityToProcessingDto(newProcessingInfo);

					this.loanRepository.upDateStatus(1, loanAppId);

					return newProcessingInfoDto;
				}

				else {
					throw new AlreadyProcessedException("Loan is already Processed");
				}
			}

			else {
				throw new ClerkNotFoundException("Clerk not Found");
			}

		}

}
