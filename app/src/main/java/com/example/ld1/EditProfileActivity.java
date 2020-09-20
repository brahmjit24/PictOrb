package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ld1.Components.ErrorDialog;
import com.example.ld1.Components.LoadingDialog;
import com.example.ld1.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView image_profile;
    ImageButton closeBtn;
    TextView saveProfileBtn,changePhoto;
    EditText name,username,bio;


    FirebaseUser firebaseUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        image_profile = findViewById(R.id.image_profile);
        closeBtn = findViewById(R.id.closeBtn);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        changePhoto = findViewById(R.id.changePhoto);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                name.setText(user.getName());
                bio.setText(user.getBio());
                Picasso.get().load(user.getImage()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(name.getText().toString(),username.getText().toString(),bio.getText().toString());
                finish();
            }
        });


    }






    private void updateProfile(String name, String username, String bio) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> map = new HashMap<>();

        map.put("name",name);
        map.put("username",username);
        map.put("bio",bio);

        reference.updateChildren(map);

    }
    private String getExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final LoadingDialog ld = new LoadingDialog(EditProfileActivity.this);
        ld.setMessage("Uploading...");
        ld.show();



        if(mImageUri!=null) {
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getExtention(mImageUri));
            UploadTask uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {

                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                        return task.getException();

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();
                        String myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String,Object> map = new HashMap<>();
                        map.put("image",myUrl);

                        reference.updateChildren(map);
                        ld.dismiss();
                    }
                    else{
                        ld.dismiss();
                        ErrorDialog dialog = new ErrorDialog(EditProfileActivity.this);
                        dialog.setErrorMessage("Failed");
                        dialog.show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ld.dismiss();
                    ErrorDialog dialog = new ErrorDialog(EditProfileActivity.this);
                    dialog.setErrorMessage(e.getMessage());
                    dialog.show();
                }
            });;
        }else{
            ErrorDialog dialog = new ErrorDialog(EditProfileActivity.this);
            dialog.setErrorMessage("No image selected");
            dialog.show();

        }




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            uploadImage();
        }else{

        }

    }

    private void gotoMainActivity(){
        Intent i = new Intent(EditProfileActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }



}
