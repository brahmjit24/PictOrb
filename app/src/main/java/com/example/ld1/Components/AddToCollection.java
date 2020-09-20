package com.example.ld1.Components;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ld1.R;

public class AddToCollection {
    Activity activity;
    AlertDialog dialog;

    public AddToCollection(Activity activity){
        this.activity = activity;

    }


    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.custom_add_to_collection,null);
        builder.setView(alertView);
        builder.setCancelable(false);
        dialog = builder.create();

        final TextView dismissBtn = alertView.findViewById(R.id.closeBtn);
        final TextView doneBtn = alertView.findViewById(R.id.doneBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:LOGIC
                dismiss();
            }
        });
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void dismiss(){
        this.dialog.dismiss();
    }
}
