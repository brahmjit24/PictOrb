package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.ld1.Fragment.ExploreFragment;
import com.example.ld1.Fragment.HomeFragment;
import com.example.ld1.Fragment.NotificationFragment;
import com.example.ld1.Fragment.ProfileFragment;
import com.example.ld1.MicroBackgroundServices.BackgroundLoadImageDirectoryService;
import com.example.ld1.TempDataStore.StoreImageCache;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        Bundle intent = getIntent().getExtras();

        if(intent!=null){
            String publisher = intent.get("publisherid").toString();
            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }

    }


    private  BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {




                    switch (menuItem.getItemId()){
                        case R.id.nav_home:{
                            selectedFragment = new HomeFragment();
                            break;}
                        case R.id.nav_explore:{

                            selectedFragment = new ExploreFragment();
                            break;}
                        case R.id.nav_add:{


                            selectedFragment = null;
                            startActivity(new Intent(MainActivity.this,PostActivity.class));
                            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );

                            break;}
                        case R.id.nav_notification:{


                            selectedFragment = new NotificationFragment();
                            break;}
                        case R.id.nav_profile:{

                            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();
                            break;}
                    }

                    if(selectedFragment!=null){
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();


                        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                            fragmentManager.popBackStack();
                        }

                        return true;
                    }
                    else{
                        return false;
                    }




                }
            };

    @Override
    protected void onStart() {
        super.onStart();

    }

}
