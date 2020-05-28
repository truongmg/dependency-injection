package com.truongmg.di.test;

import com.truongmg.di.annotations.Scope;
import com.truongmg.di.annotations.Service;

@Service
@Scope
public class TestServiceThree {

    private final TestServiceOne serviceOne;

    public TestServiceThree(TestServiceOne serviceOne) {
        this.serviceOne = serviceOne;
        System.out.println("create service three");
        System.out.println("serviceOne: " + serviceOne);
    }


}
