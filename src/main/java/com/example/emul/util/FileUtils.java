package com.example.emul.util;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class FileUtils {
    public static String getStringFromFile(String path) throws IOException {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        String stringFromFile;
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            stringFromFile = IOUtils.toString(inputStream, Charset.defaultCharset());
        }
        return stringFromFile;
    }
}
