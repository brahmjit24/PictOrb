package com.example.ld1.Components;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ld1.R;

public class ErrorDialog {
    Activity activity;
    AlertDialog dialog;
    String message;
    public ErrorDialog(Activity activity){
        this.activity = activity;
        message="Loading...";
    }

    public void setErrorMessage(String message) {
        this.message = message;
    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.custom_error_dialog,null);
        builder.setView(alertView);
        builder.setCancelable(true);
        dialog = builder.create();
        TextView messageToBeDisplayed = alertView.findViewById(R.id.errorMessageToBeDisplayed);
        final TextView dismissBtn = alertView.findViewById(R.id.dismissBtn);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        messageToBeDisplayed.setText(message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void dismiss(){
        this.dialog.dismiss();
    }


}
