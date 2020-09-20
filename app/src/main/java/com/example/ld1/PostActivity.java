package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.ld1.Adapter.GridImageAdapter;
import com.example.ld1.Components.LoadingDialog;
import com.example.ld1.Components.SuccessMessageDialog;
import com.example.ld1.TempDataStore.StoreImageCache;
import com.example.ld1.Utilities.DirectoryNamesFilter;
import com.example.ld1.Utilities.FileNamesFilter;
import com.example.ld1.Utilities.FilePaths;
import com.example.ld1.Utilities.FileSearch;
import com.example.ld1.Utilities.SquareImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {


    private static final int NUM_GRID_COLUMNS = 4 ;
    private static final int MY_READ_PERMISSION_CODE = 101 ;
    private GridView gridView;
private ImageView galleryImage;
private ProgressBar progressBar;
private Spinner directorySpinner;
private String mAppend="file:/";
private CircleImageView cropImageBtn;
private String currentImage="";
private Uri currentUri;
private ArrayList<String> directories;
    private final String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean imageCropped=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        galleryImage = findViewById(R.id.galleryImageView);
        gridView = findViewById(R.id.grid_view);
        progressBar = findViewById(R.id.progressBar);
        directorySpinner = findViewById(R.id.spinner_directory);
        progressBar.setVisibility(View.GONE);
        directories = new ArrayList<String>();
        ImageView closePost = findViewById(R.id.close_post);
        cropImageBtn = findViewById(R.id.cropImageBtn);
        cropImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Direct current uri cant be used because if used .... then when we crop it just shifts to croped image..
                CropImage.activity(Uri.fromFile(new File(currentImage))).setAspectRatio(1,1).setActivityTitle("Adjust image")
                        .start(PostActivity.this);
            }
        });
        closePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostActivity.this.onBackPressed();
            }
        });
        TextView nextScreen = findViewById(R.id.nextBtn);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageCropped) {
                    Intent i = new Intent(PostActivity.this, FilterPostActivity.class);
                    i.putExtra("Uri", currentUri);
                    startActivity(i);
                }
                else{
                    SuccessMessageDialog smd = new SuccessMessageDialog(PostActivity.this);
                    smd.setBtnName("Okay");
                    smd.setMessage("To proceed click button at bottom-left corner of image and crop the desired ratio.");
                    smd.setTitle("Adjust image");
                    smd.show();
                }
            }
        });




        if(ContextCompat.checkSelfPermission(PostActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(PostActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(PostActivity.this,
                        Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PostActivity.this,
                    permissions,MY_READ_PERMISSION_CODE);
        }
        else{
            init();
        }
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                currentUri = resultUri;
                imageCropped=true;
                Picasso.get().load(resultUri).into(galleryImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                init();
            }
            else {

                Toast.makeText(PostActivity.this, ""+(grantResults[2]==PackageManager.PERMISSION_GRANTED), Toast.LENGTH_SHORT).show();

//                onBackPressed();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition( R.anim.slide_in_down, R.anim.slide_out_down );

    }



    private void init(){
        ArrayList<String> ImageDir = StoreImageCache.getDirectories();
        if(ImageDir !=null){
            directories = ImageDir;
        }
        Collections.sort(directories, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String n1 = DirectoryNamesFilter.getOnlyDirectoryName(o1).toLowerCase();
                String n2 = DirectoryNamesFilter.getOnlyDirectoryName(o2).toLowerCase();
                return n1.compareTo(n2);
            }
        });
//        directories.add(filePaths.CAMERA);
//        directories.add(filePaths.SCREENSHOTS);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, DirectoryNamesFilter.getOnlyNames(directories));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);



        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //todo: grid view gallery
//                Toast.makeText(PostActivity.this, "Selected: "+directories.get(position), Toast.LENGTH_SHORT).show();
                setGridView(directories.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        try{
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(directorySpinner);
            popupWindow.setHeight(300);
        }catch(Exception e){

        }

    }


    private void setGridView(String selectedDirectory){

        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);
        Collections.sort(imgURLs, new Comparator<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public int compare(String o1, String o2) {
                try {
                Path f1 = Paths.get(o1);
                Path f2 = Paths.get(o2);

                    BasicFileAttributes attr1 = Files.readAttributes(f1,BasicFileAttributes.class);
                    BasicFileAttributes attr2 = Files.readAttributes(f2,BasicFileAttributes.class);

                    return attr2.lastAccessTime().compareTo(attr1.lastAccessTime());//to get recent to be displayed first

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use grid adapter to adapt images
        GridImageAdapter adapter = new GridImageAdapter(this,R.layout.layout_grid_imageview,mAppend,imgURLs);
        gridView.setAdapter(adapter);

        if(imgURLs.size()!=0) {
            setImage(imgURLs.get(0), galleryImage, mAppend);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setImage(imgURLs.get(position), galleryImage, mAppend);
                }
            });

        }

    }


    private  void setImage(String imageUrl,ImageView imageView, String append){
        Glide.with(this).load(new File(imageUrl)).into(imageView);
        currentImage = imageUrl;
        currentUri = Uri.fromFile(new File(currentImage));
        imageCropped=false;
    }


}
