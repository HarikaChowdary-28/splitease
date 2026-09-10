package com.splitease.service;

import com.splitease.dto.CreateUserRequest;
import com.splitease.dto.UserResponse;
import com.splitease.model.User;
import com.splitease.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    public UserResponse createUser(CreateUserRequest request){
        //DTO->entity conversion
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword()); //we will do pwd hashing in phase 3

        //save the row in postgres
        User saved=userRepository.save(user);

        //entity->dto response without pwd hash
        return toResponse(saved);
    }
    private UserResponse toResponse(User user){
        UserResponse resp=new UserResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setEmail(user.getEmail());
        resp.setCreatedAt(user.getCreatedAt());
        return resp;
    }
}
