package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ld1.Adapter.ViewPagerAdapter;
import com.example.ld1.Fragment.ImageEditorFragments.EditImageFragment;
import com.example.ld1.Fragment.ImageEditorFragments.FiltersListFragment;
import com.example.ld1.Interface.EditImageFragmentListener;
import com.example.ld1.Interface.FiltersListFragmentListener;
import com.example.ld1.Utilities.BitmapUtils;
import com.example.ld1.Utilities.SquareImageView;
import com.google.android.material.tabs.TabLayout;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FilterPostActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener {

    private SquareImageView imageToBeFiltered;
    private ImageButton backButton;
    private TextView nextBtn;
    public static final String pictureName= "Check2.jpg";
    public Uri resultImage;
    public static final int PERMISSION_PICK_IMAGE = 1000;
    TabLayout tabLayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;

    Bitmap orignalBitmap,filterBitmap,finalBitmap;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;
    //...2934

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float constrantFinal = 1.0f;

    //Load native image filter library
    static{
        System.loadLibrary("NativeImageProcessor");
    }

    private final String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE = 12345; //Some random number




    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_filter);
        //TODO:TOOLBAR
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPager);
//        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        nextBtn = findViewById(R.id.nextBtn);
        backButton = findViewById(R.id.back_post);
        imageToBeFiltered = findViewById(R.id.image_to_be_filtered);

        Intent i = getIntent();
         resultImage = (Uri)i.getExtras().get("Uri");

        Glide.with(this).load(resultImage).into(imageToBeFiltered);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(FilterPostActivity.this, "Going Next", Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                String path = MediaStore.Images.Media.insertImage(getContentResolver(), finalBitmap, "Post", null);
                Uri imageTransfer = Uri.parse(path);

                Intent i = new Intent(FilterPostActivity.this,PostDetailsActivity.class);
                i.putExtra("imageUri",imageTransfer);
                startActivity(i);
            }
        });


        if (ActivityCompat.checkSelfPermission(FilterPostActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(FilterPostActivity.this, permissions[1]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(FilterPostActivity.this, permissions[2]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FilterPostActivity.this, permissions, REQUEST_CODE);
        }


        loadImage();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);


//        filtersListFragment.displayThumbnail(orignalBitmap);


    }

    private void loadImage() {

        try {
            orignalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        filterBitmap =  orignalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        finalBitmap =   orignalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        imageToBeFiltered.setImageBitmap(orignalBitmap);
//        filtersListFragment.displayThumbnail(orignalBitmap);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FiltersListFragment(orignalBitmap);
        filtersListFragment.setListener(this);



        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment,"FILTERS");
        adapter.addFragment(editImageFragment,"EDIT");

        viewPager.setAdapter(adapter);

    }




    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        imageToBeFiltered.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        imageToBeFiltered.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onConstrantChanged(float constrant) {
        constrantFinal = constrant;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constrant));
        imageToBeFiltered.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
    Bitmap bitmap = filterBitmap.copy(Bitmap.Config.ARGB_8888,true);
    Filter myFilter = new Filter();
    myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
    myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
    myFilter.addSubFilter(new ContrastSubFilter(constrantFinal));

    finalBitmap = myFilter.processFilter(bitmap);

    }

    @Override
    public void onFilterSelected(Filter filter) {
        restControl();
        filterBitmap = orignalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        imageToBeFiltered.setImageBitmap(filter.processFilter(filterBitmap));
        finalBitmap = filterBitmap.copy(Bitmap.Config.ARGB_8888,true);
    }

    private void restControl() {
        if(editImageFragment!=null){
            editImageFragment.resetControls();
        }

        brightnessFinal=0;
        saturationFinal=1.0f;
        constrantFinal=1.0f;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FilterPostActivity.this, "Permission granted!!!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(FilterPostActivity.this, "Necessary permissions not granted...", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


}
