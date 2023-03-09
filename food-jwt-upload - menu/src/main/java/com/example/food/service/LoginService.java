package com.example.food.service;

import com.example.food.repository.UserRepository;
import com.example.food.service.imp.LoginServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements LoginServiceImp {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean login(String username, String password) {
        return userRepository.findByEmailAndPassword(username, password).size() > 0;
    }
}
