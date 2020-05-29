package com.truongmg.di.services.locators;

import com.truongmg.di.enums.DirectoryType;
import com.truongmg.di.models.Directory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class DirectoryResolverImpl implements DirectoryResolver {

    private static final String JAR_FILE_EXTENSION = ".jar";

    public Directory resolveDirectory(Class<?> startupClass) {
        final String directory = this.getDirectory(startupClass);
        DirectoryType directoryType = this.getDirectoryType(directory);
        return new Directory(directory, directoryType);
    }

    @Override
    public Directory resolveDirectory(File directory) {
        try {
            return new Directory(directory.getCanonicalPath(), this.getDirectoryType(directory.getCanonicalPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DirectoryType getDirectoryType(String directory) {
        File file = new File(directory);
        if (!file.isDirectory() && directory.endsWith(JAR_FILE_EXTENSION)) {
            return DirectoryType.JAR_FILE;
        }
        return DirectoryType.DIRECTORY;
    }

    private String getDirectory(Class<?> cls) {
        try {
            return URLDecoder.decode(cls.getProtectionDomain().getCodeSource().getLocation().getFile(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
