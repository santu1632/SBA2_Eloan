package com.iiht.training.eloan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.iiht.training.eloan.entity.Loan;
import com.iiht.training.eloan.entity.Users;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>{

	public List<Loan> findAllByCustomerId(Long customerId);
	public List<Loan> findAllByStatus(int status);
	
	
	@Modifying
	@Transactional 
	@Query("UPDATE Loan l SET l.status =:status WHERE l.id =:loanid")
	void upDateStatus(@Param("status")int status,@Param("loanid") Long loanAppId);
	
	
	@Query("FROM Loan l WHERE l.id =:loanid")
	Loan getStatus(@Param("loanid")Long loanAppId);
	
	@Modifying
	@Transactional 
	@Query("UPDATE Loan l SET l.status =:status,l.remark =:remark WHERE l.id =:loanid")
	void upDateStatusReject(@Param("status")int status,@Param("remark")String remark,@Param("loanid")Long loanAppId);
	
}
