package com.truongmg.di.services;

import com.truongmg.di.annotations.Autowired;
import com.truongmg.di.annotations.Service;
import com.truongmg.di.repositories.UserDeviceRepository;
import com.truongmg.di.repositories.UserRepository;

import java.util.List;

@Service
public class UserDeviceServiceImpl implements UserDeviceService {

    private final UserDeviceRepository userDeviceRepo;
    private final UserRepository userRepo;

    @Autowired
    public UserDeviceServiceImpl(UserDeviceRepository userDeviceRepo, UserRepository userRepo) {
        this.userDeviceRepo = userDeviceRepo;
        this.userRepo = userRepo;
        System.out.println("creating UserDeviceServiceImpl");
    }

    @Override
    public List<String> getUserDevices() {
        return userDeviceRepo.getUserDevices();
    }
}
