package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;


import com.example.ld1.MicroBackgroundServices.BackgroundLoadImageDirectoryService;
import com.example.ld1.TempDataStore.StoreImageCache;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


    }


    protected void onStart() {
        super.onStart();
//mAuth.signOut();

        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(currentUser.getUid())) {
                        if(ContextCompat.checkSelfPermission(SplashScreenActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                            startService(new Intent(SplashScreenActivity.this, BackgroundLoadImageDirectoryService.class));
                        }
                        gotoMainActivity();
                    }else{
                        mAuth.signOut();
                        gotoLoginActivity();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    // do something
                    gotoLoginActivity();
                }
            }, 1000);

        }
    }

    private void gotoLoginActivity() {
        Intent i = new Intent(SplashScreenActivity.this,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void gotoMainActivity() {
        Intent i = new Intent(SplashScreenActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }

}
