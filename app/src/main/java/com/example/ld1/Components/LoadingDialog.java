package com.example.ld1.Components;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ld1.R;

public class LoadingDialog {
    Activity activity;
    AlertDialog dialog;
    String message;
    public LoadingDialog(Activity activity){
        this.activity = activity;
        message="Loading...";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.custom_loading_dialog,null);
        builder.setView(alertView);
        builder.setCancelable(false);
        dialog = builder.create();
        TextView messageToBeDisplayed = alertView.findViewById(R.id.messageToBeDisplayed);
        messageToBeDisplayed.setText(message);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void dismiss(){
        if(dialog!=null&&this.dialog.isShowing())
            this.dialog.dismiss();
    }


}
