package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ld1.Components.ErrorDialog;
import com.example.ld1.Components.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NewUserFormActivity extends AppCompatActivity {

    TextView name,username;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference rootRef;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_account_form_panel);

         name = findViewById(R.id.user_name);
         rootRef = FirebaseDatabase.getInstance().getReference();
         next = findViewById(R.id.nextBtn);
         username = findViewById(R.id.user_username);
         firebaseAuth = FirebaseAuth.getInstance();
         firebaseUser = firebaseAuth.getCurrentUser();
         Intent intent = getIntent();
         String fullname="",email="",phone="";
          if(intent.hasExtra("name"))
              fullname = intent.getStringExtra("name");
        if(intent.hasExtra("email"))
            email = intent.getStringExtra("email");
        if(intent.hasExtra("phone"))
            phone = intent.getStringExtra("phone");

         name.setText(fullname);


        final String finalEmail = email;
        final String finalPhone = phone;
        next.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final String fname = name.getText().toString();
                 final String uname = username.getText().toString();

                 if(fname.equals("")||uname.equals("")){

                 }

                 final LoadingDialog loading = new LoadingDialog(NewUserFormActivity.this);
                 loading.setMessage("Please wait...");
                 loading.show();
                 rootRef.child("Usernames").addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if(dataSnapshot.hasChild(uname)){
                             loading.dismiss();
                             ErrorDialog ed = new ErrorDialog(NewUserFormActivity.this);
                             ed.setErrorMessage("Username Already Exits");
                             ed.show();
                         }
                         else {

                             HashMap<String, Object> hmap = new HashMap<>();
                             hmap.put("id", firebaseUser.getUid());
                             hmap.put("name", fname);
                             hmap.put("username", uname);
                             hmap.put("image", "https://firebasestorage.googleapis.com/v0/b/social-media-app-brahmjit24.appspot.com/o/profile_img.png?alt=media&token=1994452f-57ea-4286-9b1b-3db98615024f");
                             hmap.put("bio", "");
                             if (!finalEmail.equals("")) {
                                 hmap.put("email", finalEmail);
                                 hmap.put("phone", "");
                             }
                             if (!finalPhone.equals(""))
                             {   hmap.put("phone", finalPhone);
                                 hmap.put("email", "");
                             }
                             rootRef.child("Users").child(firebaseUser.getUid()).updateChildren(hmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if(task.isSuccessful()){
                                         HashMap<String,Object> hmap2 = new HashMap<>();
                                         hmap2.put(uname,firebaseUser.getUid());
                                        rootRef.child("Usernames").updateChildren(hmap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loading.dismiss();
                                                if(task.isSuccessful())
                                                    gotoMainActivity();
                                                else{
                                                    //show error
                                                    ErrorDialog ed = new ErrorDialog(NewUserFormActivity.this);
                                                    ed.setErrorMessage(task.getException().getMessage());
                                                    ed.show();
                                                }
                                            }
                                        });
                                     }else
                                     {
                                         loading.dismiss();
                                      //show error
                                         ErrorDialog ed = new ErrorDialog(NewUserFormActivity.this);
                                         ed.setErrorMessage(task.getException().getMessage());
                                         ed.show();
                                     }
                                 }
                             });
                         }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
             }
         });




    }

    private void gotoMainActivity() {
        Intent i = new Intent(NewUserFormActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    @Override
    protected void onStart() {
        super.onStart();


    }
}
