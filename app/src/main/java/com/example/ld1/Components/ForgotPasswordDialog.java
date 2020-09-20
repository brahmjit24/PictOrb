package com.example.ld1.Components;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ld1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialog {
    Activity activity;
    AlertDialog dialog;
    public ForgotPasswordDialog(Activity activity){
        this.activity = activity;
    }


    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.custom_forgot_password_dialog,null);
        builder.setView(alertView);
        builder.setCancelable(false);
        dialog = builder.create();
        final EditText userEmail = alertView.findViewById(R.id.user_email);
        TextView cancelBtn = alertView.findViewById(R.id.cancelBtn);
        TextView submitBtn = alertView.findViewById(R.id.submitBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                if(email.equals("")){
                    dismiss();
                    ErrorDialog ed = new ErrorDialog(activity);ed.setErrorMessage("Email missing");ed.show();
                    return;
                }
                dismiss();
                final LoadingDialog ld = new LoadingDialog(activity);
                ld.setMessage("Sending...");
                ld.show();

                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            ld.dismiss();
                            SuccessMessageDialog smd = new SuccessMessageDialog(activity);
                            smd.setMessage("Link has been sent to your mail");
                            smd.show();
                        }
                        else{
                            ld.dismiss();
                            ErrorDialog ed  = new ErrorDialog(activity);
                            ed.setErrorMessage(task.getException().getMessage());
                            ed.show();
                        }
                    }
                });
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void dismiss(){
        this.dialog.dismiss();
    }


}
