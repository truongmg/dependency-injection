package com.truongmg.di.services.locators;

import com.truongmg.di.exceptions.ClassLocationException;

import java.util.Set;

public interface ClassLocator {

    Set<Class<?>> locateClasses(String directory) throws ClassLocationException;

}
