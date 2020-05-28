package com.truongmg.di.services.locators;

import com.truongmg.di.models.Directory;

public interface DirectoryResolver {

    Directory resolveDirectory(Class<?> startupClass);

}
