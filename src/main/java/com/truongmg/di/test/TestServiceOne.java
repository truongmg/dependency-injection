package com.truongmg.di.test;

import com.truongmg.di.annotations.Scope;
import com.truongmg.di.annotations.Service;
import com.truongmg.di.enums.ScopeType;

@Service
@Scope(value = ScopeType.PROTOTYPE)
public class TestServiceOne {

    public TestServiceOne() {
        System.out.println("create service one");
    }

}
