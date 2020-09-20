package com.example.ld1.Utilities;

import java.util.ArrayList;

public class FileNamesFilter {
    public static String getOnlyFileName(String file){
            String name = file;
            name=name.substring(name.lastIndexOf('/')+1);
            return name;
    }
}
