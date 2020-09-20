package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ld1.Components.AddLocation;
import com.example.ld1.Components.AddTag;
import com.example.ld1.Components.AddToCollection;
import com.example.ld1.Components.ErrorDialog;
import com.example.ld1.Components.ImagePreview;
import com.example.ld1.Components.LoadingDialog;
import com.example.ld1.Components.MentionPeople;
import com.example.ld1.Utilities.SquareImageView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    TextView postBtn;
    ImageView imageToBePosted,pic_tag;
    SquareImageView pic_location,pic_people;
    SwitchCompat allowComments,shareGlobally,allowDownload;
    EditText editTextDescription;
    StorageReference storageReference;

    Uri uri=null;
    String myUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);



        imageToBePosted = findViewById(R.id.image_to_be_posted);
        pic_location = findViewById(R.id.pic_location);
        pic_tag = findViewById(R.id.pic_tags);
        pic_people = findViewById(R.id.pic_people);
        allowComments = findViewById(R.id.allow_comments_switch);
        shareGlobally = findViewById(R.id.share_global_switch);
        allowDownload = findViewById(R.id.allow_download_switch);
        postBtn = findViewById(R.id.postBtn);
        editTextDescription = findViewById(R.id.edit_text_description);
        storageReference = FirebaseStorage.getInstance().getReference();


        final AddLocation al = new AddLocation(PostDetailsActivity.this);
        final MentionPeople mp = new MentionPeople(PostDetailsActivity.this);
        final AddTag at = new AddTag(PostDetailsActivity.this);

        ImageButton goBack = findViewById(R.id.back_post);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent i =getIntent();
        if(i.hasExtra("imageUri")){
             uri = (Uri)i.getExtras().get("imageUri");
            Picasso.get().load(uri).into(imageToBePosted);
        }


        imageToBePosted.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImagePreview ip = new ImagePreview(PostDetailsActivity.this);
                if(uri!=null)
                ip.setImageUri(uri);
                ip.show();
                return true;
            }
        });

        pic_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al.show();
            }
        });

        pic_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.show();
            }
        });

        pic_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                at.show();
            }
        });


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LoadingDialog ld = new LoadingDialog(PostDetailsActivity.this);
                ld.setMessage("Posting...");
                ld.show();

                final ErrorDialog errorDialog = new ErrorDialog(PostDetailsActivity.this);

                final String description = editTextDescription.getText().toString();
                final String location = al.getLocation();
                final List<String> usersTagged= mp.getMyTaggedUser();
                final List<String> tags = at.getTagsAdded();

                final boolean canComment = allowComments.isChecked();
                final boolean canDownload = allowDownload.isChecked();
                final boolean canShareGlobally = shareGlobally.isChecked();

                String fileExtention = getExtention(uri);



                if(uri!=null){
                    final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+fileExtention);
                    UploadTask uploadTask = fileReference.putFile(uri);
                    uploadTask.continueWithTask(new Continuation(){

                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if(!task.isSuccessful())
                                return  task.getException();

                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Uri downloadUri = (Uri)task.getResult();
                                myUrl = downloadUri.toString();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                                String postid = reference.push().getKey();
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("id",postid);
                                hashMap.put("imageurl",myUrl);
                                hashMap.put("description",description);
                                hashMap.put("location",location);
                                hashMap.put("tags",tags);
                                hashMap.put("usermentioned",usersTagged);
                                hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("isglobal",canShareGlobally);
                                hashMap.put("iscommentable",canComment);
                                hashMap.put("isdownloadable",canDownload);
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                    LocalDateTime now = LocalDateTime.now();
                                    hashMap.put("when",dtf.format(now));
                                }else{
                                    hashMap.put("when","");
                                }


                                reference.child(postid).setValue(hashMap);

                                HashMap<String, Object> map = new HashMap<>();
                                map.put(postid,true);
                                if(canShareGlobally) {
                                    FirebaseDatabase.getInstance().getReference().child("Global").updateChildren(map);
                                    for(int i=0;i<tags.size();i++){
                                        FirebaseDatabase.getInstance().getReference().child("TagsPost").child(tags.get(i)).updateChildren(map);
                                    }
                                    for(int i=0;i<usersTagged.size();i++){
                                        FirebaseDatabase.getInstance().getReference().child("UsersMentionedPost").child(usersTagged.get(i)).updateChildren(map);
                                    }
                                }


                                ld.dismiss();
                                gotoMainActivity();
                                ///goto main

                            }else{
                                ld.dismiss();
                                errorDialog.setErrorMessage(task.getException().getMessage());
                                errorDialog.show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ld.dismiss();
                            errorDialog.setErrorMessage(e.getMessage());
                            errorDialog.show();
                        }
                    });


                }else{
                    ld.dismiss();
                    errorDialog.setErrorMessage("Internal error! Try again");
                    errorDialog.show();
                }






            }
        });





    }


    private String getExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void gotoMainActivity(){
        Intent i = new Intent(PostDetailsActivity.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

}
