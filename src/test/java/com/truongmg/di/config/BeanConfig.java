package com.truongmg.di.config;

import com.truongmg.di.annotations.Bean;
import com.truongmg.di.annotations.Service;
import com.truongmg.di.services.OtherService;
import com.truongmg.di.services.OtherServiceImpl;

@Service
public class BeanConfig {

    @Bean
    public OtherService otherService() {
        return new OtherServiceImpl();
    }

}
