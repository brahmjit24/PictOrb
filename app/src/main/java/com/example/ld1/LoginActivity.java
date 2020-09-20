package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ld1.Components.ErrorDialog;
import com.example.ld1.Components.ForgotPasswordDialog;
import com.example.ld1.Components.LoadingDialog;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    CheckBox rememberMe;
    TextView forgotPasswordBtn;
    Button login,goToRegister;
    LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
    ErrorDialog errorDialog = new ErrorDialog(LoginActivity.this);
    GoogleSignInClient mGoogleSignInClient;
    ImageButton loginGoogleBtn,loginGithubBtn,loginPhoneBtn;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    int RC_SIGN_IN = 0;
    String codeSent;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {}
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent=s;
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {}
    };
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        initializeValues();

        loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.login_google:
                        signInGoogle();
                        break;
                }
            }
        });


        loginPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
                final View alertView = inflater.inflate(R.layout.custom_phone_login,null);
                builder.setView(alertView);
                builder.setCancelable(false);
                dialog = builder.create();

                TextView cancelBtn = alertView.findViewById(R.id.cancelBtn);
                final TextView continueBtn = alertView.findViewById(R.id.continueBtn);
                final TextView verifyBtn = alertView.findViewById(R.id.verifyBtn);
                final EditText otpBox = alertView.findViewById(R.id.OTP_Box);
                final EditText phoneBox = alertView.findViewById(R.id.user_phoneno);
                final CountryCodePicker ccp = alertView.findViewById(R.id.countryCodePicker);
                ccp.registerCarrierNumberEditText(phoneBox);

                otpBox.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.GONE);
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                continueBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str_number = phoneBox.getText().toString();
                        if(str_number.equals("")){
                            Toast.makeText(LoginActivity.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                        }else{
                            String phonenumber = ccp.getFullNumberWithPlus();
                            sendVerificationCode(phonenumber);
                            otpBox.setVisibility(View.VISIBLE);
                            alertView.findViewById(R.id.phoneBoxFrame).setVisibility(View.GONE);
                            continueBtn.setVisibility(View.GONE);
                            verifyBtn.setVisibility(View.VISIBLE);
                            TextView message = alertView.findViewById(R.id.messageToBeDisplayed);
                            message.setText("OTP has been sent on "+phonenumber);
                        }
                    }
                });

                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code = otpBox.getText().toString();
                        verifySiginCode(code,ccp.getFullNumberWithPlus());
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });





        loginGithubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithGithub();
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordDialog fpd = new ForgotPasswordDialog(LoginActivity.this);
                fpd.show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                if(TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password)){
                    errorDialog.setErrorMessage("All fields are required");
                    errorDialog.show();
                    return;
                }

                if(str_password.length()<6){
                    errorDialog.setErrorMessage("Password must have at least 6 characters");
                    errorDialog.show();
                    return;
                }

                loadingDialog.setMessage("Logging in...");
                loadingDialog.show();
                loginUser(str_email,str_password);
            }
        });

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRegisterActivity();
            }
        });
    }

    private void verifySiginCode(String userCode,String phonenumber) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, userCode);
        signInWithPhoneAuthCredentials(credential,phonenumber);
    }

    private void loginWithGithub() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
        List<String> scopes = new ArrayList<String>() {{add("user:email");}};
        provider.setScopes(scopes);
        mAuth.startActivityForSignInWithProvider(this, provider.build()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadingDialog.setMessage("Please wait...");
                    loadingDialog.show();
                    final String email = task.getResult().getUser().getEmail();
                    final String name = task.getResult().getUser().getDisplayName();

                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(firebaseUser.getUid())){
                                loadingDialog.dismiss();
                                gotoMainActivity();
                            }
                            else{
                                loadingDialog.dismiss();
                                Intent i = new Intent(LoginActivity.this,NewUserFormActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("name",name);
                                i.putExtra("email",email);
                                finish();
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {


                        errorDialog.setErrorMessage(task.getException().getMessage());
                        errorDialog.show();

                }
            }
        });
    }

    private void initializeValues() {
        goToRegister=findViewById(R.id.goto_register_button);
        email = findViewById(R.id.email_box);
        password = findViewById(R.id.password_et);
        rememberMe = findViewById(R.id.remember_me);
        forgotPasswordBtn = findViewById(R.id.forgot_password_btn);
        login = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loginGoogleBtn = findViewById(R.id.login_google);
        loginGithubBtn = findViewById(R.id.login_github);
        loginPhoneBtn = findViewById(R.id.login_twitter);





        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInGoogle() {
        loadingDialog.setMessage("Loading...");
        loadingDialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            loadingDialog.dismiss();
            loadingDialog.setMessage("Please wait...");
            loadingDialog.show();
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseLoginWithGoogle(account);
        } catch (ApiException e) {
            loadingDialog.dismiss();
        }
    }

    private void firebaseLoginWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
                                if (acct != null) {
                                    final String personName = acct.getDisplayName();
                                    final String personEmail = acct.getEmail();

                                    FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(firebaseUser.getUid())){
                                                loadingDialog.dismiss();
                                                gotoMainActivity();
                                            }
                                            else{
                                                loadingDialog.dismiss();
                                                Intent i = new Intent(LoginActivity.this,NewUserFormActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                i.putExtra("name",personName);
                                                i.putExtra("email",personEmail);
                                                finish();
                                                startActivity(i);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                                else{
                                    loadingDialog.dismiss();
                                }
                            }

                    }
        });
    }

    private void loginUser(final String str_email, String str_password) {
        mAuth.signInWithEmailAndPassword(str_email,str_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                loadingDialog.dismiss();
                                gotoMainActivity();
                            }
                            else{
                                loadingDialog.dismiss();
                                Intent i = new Intent(LoginActivity.this,NewUserFormActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("email",str_email);
                                finish();
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    loadingDialog.dismiss();
                    errorDialog.setErrorMessage(task.getException().getMessage());
                    errorDialog.show();
                }
            }
        });
    }

    private void gotoRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void gotoMainActivity(){
        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleSignInClient.signOut();

    }

    public void sendVerificationCode(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);

    }

    private void signInWithPhoneAuthCredentials(final PhoneAuthCredential credential, final String phonenumber){

        loadingDialog.setMessage("Verifying...");
        loadingDialog.show();
        //we add Credentials to firebaseAuth object and add an OnComplete Listener to
        // redirect if Success OR show error message if failed
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            final FirebaseUser firebaseUser = mAuth.getCurrentUser();


                            FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(firebaseUser.getUid())){
                                        loadingDialog.dismiss();
                                        gotoMainActivity();
                                    }
                                    else{
                                        loadingDialog.dismiss();
                                        Intent i = new Intent(LoginActivity.this,NewUserFormActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.putExtra("phone",phonenumber);
                                        finish();
                                        startActivity(i);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else{

                            String e =task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Error : "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
