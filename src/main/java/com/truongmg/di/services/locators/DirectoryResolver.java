package com.truongmg.di.services.locators;

import com.truongmg.di.models.Directory;

import java.io.File;

public interface DirectoryResolver {

    Directory resolveDirectory(Class<?> startupClass);

    Directory resolveDirectory(File directory);

}
