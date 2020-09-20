package com.example.ld1.TempDataStore;

import com.example.ld1.Utilities.FilePaths;
import com.example.ld1.Utilities.FileSearch;

import java.util.ArrayList;

public class StoreImageCache {
    private static ArrayList<String> directories;
    private static boolean executed=false;
    public static void executeFillCache(){
        if(executed)
            return;
        FilePaths filePaths = new FilePaths();
        ArrayList<String> ImageDir = FileSearch.getDirectoryPaths(filePaths.ROOT_DIR);
        if(ImageDir !=null){
            directories = ImageDir;
        }
        executed=true;
    }
    public static ArrayList<String> getDirectories(){
        if(!executed)
            executeFillCache();
        return directories;
    }
    public static void forceRefreshCache(){
        executed=false;
        executeFillCache();
    }


}
