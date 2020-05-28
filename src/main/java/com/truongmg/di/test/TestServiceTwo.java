package com.truongmg.di.test;

import com.truongmg.di.annotations.Scope;
import com.truongmg.di.annotations.Service;

@Service
@Scope
public class TestServiceTwo {

    private final TestServiceOne serviceOne;

    public TestServiceTwo(TestServiceOne serviceOne) {
        this.serviceOne = serviceOne;
        System.out.println("create service two");
        System.out.println("serviceOne: " + serviceOne);
    }


}
