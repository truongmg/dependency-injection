package com.truongmg.di.services.locators;

import com.truongmg.di.enums.DirectoryType;
import com.truongmg.di.models.Directory;

import java.io.File;

public class DirectoryResolverImpl implements DirectoryResolver {

    private static final String JAR_FILE_EXTENSION = ".jar";

    public Directory resolveDirectory(Class<?> startupClass) {
        final String directory = this.getDirectory(startupClass);
        DirectoryType directoryType = this.getDirectoryType(directory);
        return new Directory(directory, directoryType);
    }

    private DirectoryType getDirectoryType(String directory) {
        File file = new File(directory);
        if (!file.isDirectory() && directory.endsWith(JAR_FILE_EXTENSION)) {
            return DirectoryType.JAR_FILE;
        }
        return DirectoryType.DIRECTORY;
    }

    private String getDirectory(Class<?> cls) {
        return cls.getProtectionDomain().getCodeSource().getLocation().getFile();
    }

}
