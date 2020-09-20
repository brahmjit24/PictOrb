package com.example.ld1.Utilities;

import java.util.ArrayList;

public class DirectoryNamesFilter {
    public static ArrayList<String> getOnlyNames(ArrayList<String> list){
        ArrayList<String> newList = new ArrayList<String>(list);
        for(int i=0;i<newList.size();i++){
            String name = newList.get(i);
            name=name.substring(name.lastIndexOf('/')+1);
            newList.set(i,name);
        }
        return newList;
    }
    public static String getOnlyDirectoryName(String file){
        String name = file;
        name=name.substring(name.lastIndexOf('/')+1);
        return name;
    }
}
