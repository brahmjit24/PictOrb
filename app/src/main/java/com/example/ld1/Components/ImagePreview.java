package com.example.ld1.Components;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.ld1.R;
import com.example.ld1.Utilities.SquareImageView;
import com.squareup.picasso.Picasso;

public class ImagePreview {
    Activity activity;
    AlertDialog dialog;
    Uri message;
    public ImagePreview(Activity activity){
        this.activity = activity;
        message=Uri.parse("Check2.jpg");
    }

    public void setImageUri(Uri imageUri) {
        this.message = imageUri;
    }

    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.custom_image_preview,null);
        builder.setView(alertView);
        builder.setCancelable(true);
        dialog = builder.create();
        SquareImageView imageToBeDisplayed = alertView.findViewById(R.id.image_preview);
        final TextView dismissBtn = alertView.findViewById(R.id.dismissBtn);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Picasso.get().load(message).into(imageToBeDisplayed);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void dismiss(){
        this.dialog.dismiss();
    }


}
