package com.truongmg.di.services.locators;

import com.truongmg.di.constants.Constants;
import com.truongmg.di.exceptions.ClassLocationException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ClassLocatorForDirectory implements ClassLocator {

    private static final String INVALID_DIRECTORY_MSG = "Invalid directory %s.";

    private final Set<Class<?>> locatedClasses;

    public ClassLocatorForDirectory() {
        this.locatedClasses = new HashSet<>();
    }

    public Set<Class<?>> locateClasses(String directory) throws ClassLocationException {
        File file = new File(directory);
        if (!file.isDirectory()) {
            throw new ClassLocationException(String.format(INVALID_DIRECTORY_MSG, directory));
        }

        try {
            for (File nestedFile : file.listFiles()) {
                this.scanDir(nestedFile, "");
            }
        } catch (ClassNotFoundException e) {
            throw new ClassLocationException(e.getMessage(), e);
        }

        return this.locatedClasses;
    }

    private void scanDir(File file, String packageName) throws ClassLocationException, ClassNotFoundException {
        if (file.isDirectory()) {
            packageName += file.getName() + ".";
            for (File nestedFile : file.listFiles()) {
                this.scanDir(nestedFile, packageName);
            }
        } else {
            if (!file.getName().endsWith(Constants.JAVA_BINARY_EXTENSION)) {
                return;
            }
            final String className = packageName + file.getName()
                    .replace(Constants.JAVA_BINARY_EXTENSION, "");

            this.locatedClasses.add(Class.forName(className));
        }
    }

}
