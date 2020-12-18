package com.iiht.training.eloan.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.RejectDto;
import com.iiht.training.eloan.dto.SanctionDto;
import com.iiht.training.eloan.dto.SanctionOutputDto;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.SanctionInfo;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.exception.AlreadyFinalizedException;
import com.iiht.training.eloan.exception.AlreadyProcessedException;
import com.iiht.training.eloan.exception.ClerkNotFoundException;
import com.iiht.training.eloan.exception.LoanNotFoundException;
import com.iiht.training.eloan.exception.ManagerNotFoundException;
import com.iiht.training.eloan.repository.LoanRepository;
import com.iiht.training.eloan.repository.ProcessingInfoRepository;
import com.iiht.training.eloan.repository.SanctionInfoRepository;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.ManagerService;

@Service
public class ManagerServiceImpl implements ManagerService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private LoanRepository loanRepository;
	
	@Autowired
	private ProcessingInfoRepository pProcessingInfoRepository;
	
	@Autowired
	private SanctionInfoRepository sanctionInfoRepository;
	
	private Loan convertToLoanInputDto( RejectDto rejectDto) {
		
		Loan reject = new Loan();
		reject.setRemark(rejectDto.getRemark());
	
		return reject;
		
	}
	
	private RejectDto convertToRejectOutputDto(Loan status) {
		
		RejectDto rejected = new RejectDto();
		rejected.setRemark(status.getRemark());	
		return rejected;
		
	}
	
	
	private SanctionInfo convertSanctionDtoToEntity(Long managerId, Long loanAppId, SanctionDto sanctionDto) {
		
		SanctionInfo info= new SanctionInfo();
		info.setLoanAppId(loanAppId);
		info.setManagerId(managerId);
		info.setLoanAmountSanctioned(sanctionDto.getLoanAmountSanctioned());
		info.setTermOfLoan(sanctionDto.getTermOfLoan());
		info.setPaymentStartDate(sanctionDto.getPaymentStartDate());
		
  	  
      double loanAmountSanctioned = sanctionDto.getLoanAmountSanctioned();
      double termOfLoan  = sanctionDto.getTermOfLoan();
      double doublelroi = sanctionDto.getRateOfInterest();
      double roipm = doublelroi/(12*100);
      double TenureMonths = termOfLoan*12;
      
      String  paymentStartDate = sanctionDto.getPaymentStartDate();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy");
      LocalDate dateTime = LocalDate.parse(paymentStartDate, format);

      
      
      
      /*Monthly payment amount Calculation */
      double  monthlyPayment = Math.round(((loanAmountSanctioned*roipm*Math.pow(1+roipm,TenureMonths))/(Math.pow(1+roipm,TenureMonths)-1))*100)/100;
      
      /*Term payment amount Calculation */
      double  termPaymentAmount  =Math.round((monthlyPayment*TenureMonths)*100)/100;
      System.out.print(" EMI is= "+monthlyPayment+"\n" +"total amt = " + termPaymentAmount);
      
      /*Loan End date Calculation */
     
      String loanClosureDate = (dateTime.plusMonths((long) TenureMonths)).format(format); 
	
      info.setLoanClosureDate(loanClosureDate);
      info.setMonthlyPayment(monthlyPayment);
     
		
		
		return info;
		
	}
	
	private SanctionOutputDto convertEntityToSanctionOutputDto(SanctionInfo sanctionInfo) {
		SanctionOutputDto outputDto = new SanctionOutputDto();
		
		outputDto.setLoanAmountSanctioned(sanctionInfo.getLoanAmountSanctioned());
		outputDto.setLoanClosureDate(sanctionInfo.getLoanClosureDate());
		outputDto.setMonthlyPayment(sanctionInfo.getMonthlyPayment());
		outputDto.setPaymentStartDate(sanctionInfo.getPaymentStartDate());
		outputDto.setTermOfLoan(sanctionInfo.getTermOfLoan());
		
		return outputDto;
		
		
	}
	
	
	@Override
	public List<LoanOutputDto> allProcessedLoans() {
		
	GenericCodeImpl genericCode = new GenericCodeImpl();
		
		List<Loan> loans = this.loanRepository.findAllByStatus(1);
		
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
	public RejectDto rejectLoan(Long managerId, Long loanAppId, RejectDto rejectDto) {
		
		Loan loan = this.loanRepository.getStatus(loanAppId);
		
		Users user = this.usersRepository.getRole(managerId);
		
		String role= user.getRole();
		
		if (role.contains("Manager")) {
			
			if (loan.getStatus() == 1) {
				
				Loan reject = this.convertToLoanInputDto(rejectDto);
				String remark = reject.getRemark();
				this.loanRepository.upDateStatusReject(-1,remark,loanAppId);
				Loan status = this.loanRepository.getStatus(loanAppId);
				RejectDto rejected = this.convertToRejectOutputDto(status);
				return rejected;
				
			} else {
				throw new AlreadyFinalizedException("Loan is either in Applied or Rejectd or Finalized status");
			}
		}
		else {
			throw new ManagerNotFoundException("Manager not Found");
		
		}
		
	}

	@Override
	public SanctionOutputDto sanctionLoan(Long managerId, Long loanAppId, SanctionDto sanctionDto) {
		
		Loan loan = this.loanRepository.getStatus(loanAppId);
		
		Users user = this.usersRepository.getRole(managerId);
		
		String role= user.getRole();
		
		if (role.contains("Manager")) {
			
			if (loan.getStatus() == 1) {
		
		SanctionInfo sanctionInfo = this.convertSanctionDtoToEntity(managerId, loanAppId, sanctionDto);
		
		SanctionInfo newsanctionInfo =  this.sanctionInfoRepository.save(sanctionInfo);
		
		SanctionOutputDto outputDto = this.convertEntityToSanctionOutputDto(newsanctionInfo);
		
//		this.loanRepository.upDateStatus(2, loanAppId);
		String remark = "Approved";
		
		this.loanRepository.upDateStatusReject(2,remark,loanAppId);
		
		return outputDto;
			} else {
				throw new AlreadyFinalizedException("Loan is either in Applied or Rejectd or Finalized status");
			}
		}
		else {
			throw new ManagerNotFoundException("Manager not Found");
		
		}
	}

}
