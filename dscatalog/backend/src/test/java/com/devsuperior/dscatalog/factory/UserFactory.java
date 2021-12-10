package com.devsuperior.dscatalog.factory;

import com.devsuperior.dscatalog.dtos.UserDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;

public class UserFactory {

	public static User createUser() {
		User user = new User(1L, "Ana", "Paula", "Ana37@hotmail.com", "ana123");
		user.getRoles().add(new Role(1L, "ROLE_OPERATOR"));
		return user;
	}
	
	public static UserDTO createUserDTO() {
		return new UserDTO(createUser());
	}

}
