package com.truongmg.di.services;

import com.truongmg.di.annotations.Autowired;
import com.truongmg.di.annotations.Service;
import com.truongmg.di.repositories.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final OtherService otherService;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, OtherService otherService) {
        this.userRepo = userRepo;
        this.otherService = otherService;
        System.out.println("creating UserServiceImpl");
    }

    @Override
    public List<String> getUsers() {
        return userRepo.getUsers();
    }

    @Override
    public void sayHello() {
        otherService.printMessage();
    }
}
