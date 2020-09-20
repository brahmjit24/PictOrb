package com.example.ld1.Utilities;

import android.media.Image;
import android.provider.ContactsContract;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

public class FileSearch {


    public static ArrayList<String> fullDirList=new ArrayList<String>();

    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<String>();
        File file = new File(directory);
        File[] filesList = file.listFiles();

        for(int i=0;filesList!=null&&i<filesList.length;i++){
            if(filesList[i].isDirectory()){
                String name = filesList[i].getAbsolutePath();
                char ch = name.charAt(name.lastIndexOf('/')+1);
                if(ch!='.'&&notANumber(ch)&&(!DirectoryNamesFilter.getOnlyDirectoryName(name).equals("Android"))) {

                    if(containsImageFile(filesList[i]))
                    pathArray.add(filesList[i].getAbsolutePath());

                    ArrayList<String> newPaths = getDirectoryPaths(filesList[i].getAbsolutePath());

                    if(newPaths!=null&&newPaths.size()!=0){
                        for(String path:newPaths){
                            pathArray.add(path);
                        }
                    }
                }
            }
        }
        return pathArray;
    }

    private static boolean notANumber(char ch) {
        if(ch=='1'||ch=='2'||ch=='3'||ch=='4'||ch=='5'||ch=='6'||ch=='7'||ch=='8'||ch=='9'||ch=='0')
            return false;

        return true;
    }

    private static boolean containsImageFile(File file) {

        File[] filesList = file.listFiles();
        for(int i=0;i<filesList.length;i++){
            if(filesList[i].isFile()){
                if(ImageFileFilter.accept(filesList[i])){
                   return true;
                }
            }
        }
        return false;
    }



    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<String>();
        File file = new File(directory);
        File[] filesList = file.listFiles();
        for(int i=0;i<filesList.length;i++){
            if(filesList[i].isFile()){
                if(ImageFileFilter.accept(filesList[i])){
                    pathArray.add(filesList[i].getAbsolutePath());
                }
            }
        }
        return pathArray;
    }


    public static class ImageFileFilter{
        private static final String[] okFileExtensions = new String[] {
                "jpg",
                "png",
                "gif",
                "jpeg"
        };


        public static boolean accept(File file) {
            for (String extension: okFileExtensions) {
                if (file.getName().toLowerCase().endsWith(extension)) {
                    return true;
                }
            }
            return false;
        }

    }

}
