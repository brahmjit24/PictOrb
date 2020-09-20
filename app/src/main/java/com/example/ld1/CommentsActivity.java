package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ld1.Adapter.CommentAdapter;
import com.example.ld1.Model.Comment;
import com.example.ld1.Model.Post;
import com.example.ld1.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    EditText addComment;
    TextView postBtn;
    CircleImageView image_profile;
    ImageButton backBtn;
    String postId="";
    String postcreaterid = "";
    String publisherId;
    RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comments);


        backBtn = findViewById(R.id.backBtn);
        addComment = findViewById(R.id.add_comment);
        postBtn = findViewById(R.id.post);
        image_profile = findViewById(R.id.image_profile);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);
        commentsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentsRecyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList);
        commentsRecyclerView.setAdapter(commentAdapter);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Intent intent = getIntent();
        if(intent.getExtras().containsKey("postid")){
            postId = intent.getExtras().get("postid").toString();
        }
        if(getIntent().getExtras().containsKey("postcreaterid")){
            postcreaterid = getIntent().getExtras().get("postcreaterid").toString();
        }


        publisherId = firebaseUser.getUid();

        getImage();

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addComment.getText().toString().trim().equals("")){
                    return;
                }
                else{
                    postComment();
                }
            }
        });



        readComments();



    }



    private  void addNotification(final String msg){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Notifications").child(post.getPublisher());
                HashMap<String,Object> map = new HashMap<>();
                map.put("userid",firebaseUser.getUid());
                map.put("text","commented: "+msg);
                map.put("postid",postId);
                map.put("ispost",true);

                reference2.push().setValue(map);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    private void postComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        HashMap<String,Object> map  = new HashMap<>();

        map.put("comment",addComment.getText().toString());
        map.put("publisher",publisherId);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            map.put("when",dtf.format(now));
        }else{
            map.put("when","");
        }

        reference.push().setValue(map);

        addNotification(addComment.getText().toString());


        addComment.setText("");
    }

    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImage()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void readComments(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Comment comment = dataSnapshot1.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




}
