package com.example.ld1.Components;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ld1.R;

public class SuccessMessageDialog {
    Activity activity;
    AlertDialog dialog;
    String message;
    String btnName;
    String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public SuccessMessageDialog(Activity activity){
        this.activity = activity;
        message="Loading...";
        btnName = "Dismiss";
        title="Success";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBtnName(String btnName) {
        this.btnName = btnName;
    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.custom_message_dialog,null);
        builder.setView(alertView);
        builder.setCancelable(false);
        dialog = builder.create();
        TextView messageToBeDisplayed = alertView.findViewById(R.id.messageToBeDisplayed);
        final TextView dismissBtn = alertView.findViewById(R.id.dismissBtn);
        final TextView titleView = alertView.findViewById(R.id.title);
        dismissBtn.setText(btnName);
        titleView.setText(title);
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
