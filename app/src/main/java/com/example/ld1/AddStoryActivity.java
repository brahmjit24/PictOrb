package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import com.example.ld1.Components.ErrorDialog;
import com.example.ld1.Components.LoadingDialog;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.HashSet;

public class AddStoryActivity extends AppCompatActivity {


    private Uri mImageUri;
    String myUrl = "";
    private StorageTask storageTask;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);


        storageReference = FirebaseStorage.getInstance().getReference("story");

        CropImage.activity().setAspectRatio(9,16).start(AddStoryActivity.this);

    }


    private void publishStory(){
        final LoadingDialog ld  = new LoadingDialog(this);
        ld.setMessage("Posting...");
        ld.show();

        if(mImageUri!=null){
            final StorageReference imageReference = storageReference.child(System.currentTimeMillis()+"."+getExtention(mImageUri));
            storageTask = imageReference.putFile(mImageUri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                        return  task.getException();

                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(myId);
                        String storyid = reference.push().getKey();
                        long storyend = System.currentTimeMillis()+86400000;


                        HashMap<String,Object> map = new HashMap<String, Object>();
                        map.put("imageurl",myUrl);
                        map.put("storystart", ServerValue.TIMESTAMP);
                        map.put("storyend",storyend);
                        map.put("userid",myId);
                        map.put("storyid",storyid);


                        reference.child(storyid).setValue(map);
                        ld.dismiss();
//                        finish();
                        gotoMainActivity();
                    }
                    else{
                        ld.dismiss();
                        ErrorDialog ed= new ErrorDialog(AddStoryActivity.this);
                        ed.setErrorMessage(task.getException().getMessage());
                        ed.show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ld.dismiss();
                    ErrorDialog ed= new ErrorDialog(AddStoryActivity.this);
                    ed.setErrorMessage(e.getMessage());
                    ed.show();
                }
            });
        }else{
            ld.dismiss();
            ErrorDialog ed= new ErrorDialog(AddStoryActivity.this);
            ed.setErrorMessage("No image selected! ");
            ed.show();
        }


    }
    private String getExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            publishStory();
        }else{

//            finish();

            gotoMainActivity();
        }

    }



    private void gotoMainActivity() {
        Intent i = new Intent(AddStoryActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }
}
