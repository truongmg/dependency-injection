package com.truongmg.di.repositories;

import com.truongmg.di.annotations.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserRepositoryImpl implements UserRepository {
    @Override
    public List<String> getUsers() {
        return Arrays.asList(new String[] {"User 1", "User 2"});
    }
}
