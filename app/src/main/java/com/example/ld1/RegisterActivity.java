package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ld1.Components.ErrorDialog;
import com.example.ld1.Components.LoadingDialog;
import com.example.ld1.Components.SuccessMessageDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText email,name,password;
    Button backToLogin,createAccountBtn;
    CheckBox termsCheckBox;
    TextView myTermsAndConditionsBtn;
    AlertDialog termsAndConditionDialog;

    FirebaseAuth mAuth;
    DatabaseReference userDbRef;
    LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);;
    ErrorDialog errorDialog = new ErrorDialog(RegisterActivity.this);;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

         backToLogin = findViewById(R.id.goto_login_button);
         email = findViewById(R.id.email_box);
         name = findViewById(R.id.name_box);

         password = findViewById(R.id.password_et);
         createAccountBtn = findViewById(R.id.register_button);
         termsCheckBox = findViewById(R.id.accept_terms_check);
         myTermsAndConditionsBtn = findViewById(R.id.our_terms_and_condition);
         mAuth = FirebaseAuth.getInstance();
         userDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

         myTermsAndConditionsBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 displayTermsAndConditions();
             }
         });





        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.setMessage("Please wait...");


                String str_name = name.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if(TextUtils.isEmpty(str_name)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password)){
                    errorDialog.setErrorMessage("All fields are required");
                    errorDialog.show();
                    return;
                }


                if(str_password.length()<6){
                    errorDialog.setErrorMessage("Password must have at least 6 characters");
                    errorDialog.show();
                    return;
                }

                if(termsCheckBox.isChecked()==false){
                    errorDialog.setErrorMessage("Accept terms and conditions before proceeding further.");
                    errorDialog.show();
                    return;
                }

                registerUser(str_email,str_name,str_password);




            }
        });
    }

    private void displayTermsAndConditions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.terms_and_conditions,null);
        builder.setView(alertView);
        builder.setCancelable(false);
        termsAndConditionDialog = builder.create();
        TextView declineBtn = alertView.findViewById(R.id.decline_agreement);
        TextView acceptBtn = alertView.findViewById(R.id.accept_agreement);

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termsCheckBox.setChecked(false);
                termsAndConditionDialog.dismiss();
            }
        });
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termsCheckBox.setChecked(true);
                termsAndConditionDialog.dismiss();
            }
        });

        termsAndConditionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        termsAndConditionDialog.show();
    }

    private  void registerUser(final String email, final String name, final String password){
        loadingDialog.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    String userId = firebaseUser.getUid();

                    loadingDialog.dismiss();
                    Intent i = new Intent(RegisterActivity.this,NewUserFormActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("name",name);
                    i.putExtra("email",email);
                    finish();
                    startActivity(i);

                }
                else{

                    loadingDialog.dismiss();
                    errorDialog.setErrorMessage(task.getException().getMessage());
                    errorDialog.show();
                }
            }
        });
    }
}
