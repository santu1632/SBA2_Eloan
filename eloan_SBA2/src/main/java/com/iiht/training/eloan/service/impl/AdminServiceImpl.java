package com.iiht.training.eloan.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iiht.training.eloan.dto.LoanOutputDto;
import com.iiht.training.eloan.dto.UserDto;
import com.iiht.training.eloan.entity.Users;
import com.iiht.training.eloan.repository.UsersRepository;
import com.iiht.training.eloan.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Override
	public UserDto registerClerk(UserDto userDto) {

		GenericCodeImpl genericCode = new GenericCodeImpl();
		
		Users clerk= genericCode.convertInputDtoToEntity(userDto);
		
		Users newClerk =this.usersRepository.save(clerk);
		
		UserDto newClerkDto = genericCode.convertEntityToUsersDto(newClerk);
		
		return newClerkDto;
		
		
	}

	@Override
	public UserDto registerManager(UserDto userDto) {
		GenericCodeImpl genericCode = new GenericCodeImpl();
		
		Users users= genericCode.convertInputDtoToEntity(userDto);
		
		Users newManager=this.usersRepository.save(users);
		
		UserDto newManagerDto = genericCode.convertEntityToUsersDto(newManager);
		
		return newManagerDto;
	}

	@Override
	public List<UserDto> getAllClerks() {
		GenericCodeImpl genericCode = new GenericCodeImpl();
		List<Users> users = this.usersRepository.findAllByRole("Clerk");
		
		List<UserDto> userDtos = 
				users.stream()
				 .map(genericCode :: convertEntityToUsersDto)
				 .collect(Collectors.toList());
		
		return userDtos;
	}

	@Override
	public List<UserDto> getAllManagers() {
		GenericCodeImpl genericCode = new GenericCodeImpl();
		List<Users> users = this.usersRepository.findAllByRole("Manager");
		
		List<UserDto> userDtos = 
				users.stream()
				 .map(genericCode :: convertEntityToUsersDto)
				 .collect(Collectors.toList());
		
		return userDtos;
	}

}
