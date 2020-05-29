package com.truongmg.di.repositories;

import com.truongmg.di.annotations.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDeviceRepositoryImpl implements UserDeviceRepository {

    @Override
    public List<String> getUserDevices() {
        return Arrays.asList(new String[] {"Device 1", "Device 2"});
    }

}
