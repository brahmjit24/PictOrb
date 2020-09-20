package com.example.ld1.Components;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ld1.R;

public class AddLocation {
    Activity activity;
    AlertDialog dialog;
    String location="";
    public AddLocation(Activity activity){
        this.activity = activity;

    }


    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.custom_add_location,null);
        builder.setView(alertView);
        builder.setCancelable(false);
        dialog = builder.create();

        final TextView dismissBtn = alertView.findViewById(R.id.closeBtn);
        final TextView addBtn = alertView.findViewById(R.id.addBtn);
        final EditText postLocation = alertView.findViewById(R.id.post_location);
        postLocation.setText(location);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                location = postLocation.getText().toString();
//                Toast.makeText(activity, location, Toast.LENGTH_SHORT).show();
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

    public String getLocation() {
        return location;
    }
}
