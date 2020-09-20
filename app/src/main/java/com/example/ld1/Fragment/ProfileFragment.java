package com.example.ld1.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ld1.Adapter.MyPhotoAdapter;
import com.example.ld1.Adapter.PostAdapter;
import com.example.ld1.EditProfileActivity;
import com.example.ld1.FollowersActivity;
import com.example.ld1.Model.Post;
import com.example.ld1.Model.User;
import com.example.ld1.OptionsActivity;
import com.example.ld1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    CircleImageView small_profile,image_profile;
    ImageButton options,my_photos_grid,my_photos_linear,my_bookmarks ,tagged_photos,menu_options;
    RecyclerView grid_recycler_view_photos,linear_recycler_view_photos,grid_recycler_view_bookmarked,grid_recycler_view_tagged;
    TextView username,fullname,bio,posts,followers,following;
    Button profile_button;

    MyPhotoAdapter photoAdapter;
    List<Post> postList;

    private List<String> mySavePost,myTaggedPost;
    private PostAdapter postAdapter;

    MyPhotoAdapter photoAdapter_saved;
    List<Post> postList_saved;

    MyPhotoAdapter photoAdapter_tagged;
    List<Post> postList_tagged;


    FirebaseUser firebaseUser;
    String profileid;

    public ProfileFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid","none");

        small_profile = view.findViewById(R.id.small_profile);
        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        my_photos_grid = view.findViewById(R.id.my_photos_grid);
        my_photos_linear = view.findViewById(R.id.my_photos_linear);
        my_bookmarks = view.findViewById(R.id.my_bookmarks);
        tagged_photos = view.findViewById(R.id.tagged_photos);
        grid_recycler_view_photos = view.findViewById(R.id.grid_recycler_view_photos);
        linear_recycler_view_photos = view.findViewById(R.id.linear_recycler_view_photos);
        grid_recycler_view_bookmarked = view.findViewById(R.id.grid_recycler_view_bookmarked);
        grid_recycler_view_tagged = view.findViewById(R.id.grid_recycler_view_tagged);
        username = view.findViewById(R.id.username);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        profile_button = view.findViewById(R.id.profile_button);
        menu_options = view.findViewById(R.id.menu_options);


        if(profileid.equals(firebaseUser.getUid())){
            options.setVisibility(View.GONE);
            menu_options.setVisibility(View.VISIBLE);
        }
        else{
            options.setVisibility(View.VISIBLE);
            menu_options.setVisibility(View.GONE);
        }


        menu_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);

            }
        });


        grid_recycler_view_photos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        grid_recycler_view_photos.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        photoAdapter = new MyPhotoAdapter(getContext(),postList);

        grid_recycler_view_photos.setAdapter(photoAdapter);


        grid_recycler_view_bookmarked.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new GridLayoutManager(getContext(),3);
        grid_recycler_view_bookmarked.setLayoutManager(linearLayoutManager2);
        postList_saved = new ArrayList<>();
        photoAdapter_saved = new MyPhotoAdapter(getContext(),postList_saved);
        grid_recycler_view_bookmarked.setAdapter(photoAdapter_saved);


        grid_recycler_view_photos.setVisibility(View.VISIBLE);
        grid_recycler_view_bookmarked.setVisibility(View.GONE);
        my_photos_grid.setImageResource(R.drawable.ic_grid_selected);




        grid_recycler_view_tagged.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager3 = new GridLayoutManager(getContext(),3);
        grid_recycler_view_tagged.setLayoutManager(linearLayoutManager3);
        postList_tagged = new ArrayList<>();
        photoAdapter_tagged = new MyPhotoAdapter(getContext(),postList_tagged);
        grid_recycler_view_tagged.setAdapter(photoAdapter_tagged);




        linear_recycler_view_photos.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        linear_recycler_view_photos.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(getContext(),postList);
        linear_recycler_view_photos.setAdapter(postAdapter);



        userinfo();
        getFollowers();
        getNrPosts();
        myPhotos();
        mySaves();
        myTagged();

        if(firebaseUser.getUid().equals(profileid)){
            profile_button.setText("Edit profile");
            profile_button.setBackgroundResource(R.drawable.background_light_white_box);
            profile_button.setTextColor(Color.BLACK);
        }else{
            checkFollow();
            my_bookmarks.setVisibility(View.GONE);
            grid_recycler_view_bookmarked.setVisibility(View.GONE);
        }



        my_photos_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_photos_grid.setImageResource(R.drawable.ic_grid_selected);
                my_bookmarks.setImageResource(R.drawable.ic_bookmark_outline);
                my_photos_linear.setImageResource(R.drawable.ic_linear_photos);
                tagged_photos.setImageResource(R.drawable.ic_tagged_people);

                grid_recycler_view_tagged.setVisibility(View.GONE);
                linear_recycler_view_photos.setVisibility(View.GONE);
                grid_recycler_view_photos.setVisibility(View.VISIBLE);
                grid_recycler_view_bookmarked.setVisibility(View.GONE);
            }
        });

        my_bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_photos_grid.setImageResource(R.drawable.ic_grid);
                my_bookmarks.setImageResource(R.drawable.ic_bookmarked);
                my_photos_linear.setImageResource(R.drawable.ic_linear_photos);
                tagged_photos.setImageResource(R.drawable.ic_tagged_people);

                grid_recycler_view_tagged.setVisibility(View.GONE);
                linear_recycler_view_photos.setVisibility(View.GONE);
                grid_recycler_view_photos.setVisibility(View.GONE);
                grid_recycler_view_bookmarked.setVisibility(View.VISIBLE);
            }
        });


        my_photos_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_photos_grid.setImageResource(R.drawable.ic_grid);
                my_bookmarks.setImageResource(R.drawable.ic_bookmark_outline);
                my_photos_linear.setImageResource(R.drawable.ic_linear_photos_selected);
                tagged_photos.setImageResource(R.drawable.ic_tagged_people);

                grid_recycler_view_tagged.setVisibility(View.GONE);
                grid_recycler_view_photos.setVisibility(View.GONE);
                grid_recycler_view_bookmarked.setVisibility(View.GONE);
                linear_recycler_view_photos.setVisibility(View.VISIBLE);

            }
        });

        tagged_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_photos_grid.setImageResource(R.drawable.ic_grid);
                my_bookmarks.setImageResource(R.drawable.ic_bookmark_outline);
                my_photos_linear.setImageResource(R.drawable.ic_linear_photos);
                tagged_photos.setImageResource(R.drawable.ic_tag_selected);

                grid_recycler_view_tagged.setVisibility(View.VISIBLE);
                linear_recycler_view_photos.setVisibility(View.GONE);
                grid_recycler_view_photos.setVisibility(View.GONE);
                grid_recycler_view_bookmarked.setVisibility(View.GONE);


            }
        });




        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = profile_button.getText().toString();

                if(btn.equals("Edit profile")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else if (btn.equals("Follow")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers").child(firebaseUser.getUid()).setValue(true);
                    addNotification();
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers").child(firebaseUser.getUid()).removeValue();

                }

            }
        });



        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","followers");
                startActivity(intent);

            }
        });


        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","following");
                startActivity(intent);

            }
        });

        return view;
    }



    private  void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);
        HashMap<String,Object> map = new HashMap<>();
        map.put("userid",firebaseUser.getUid());
        map.put("text","started following you");
        map.put("postid","");
        map.put("ispost",false);

        reference.push().setValue(map);
    }


    private void userinfo(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext() == null)
                    return;

                User user = dataSnapshot.getValue(User.class);
                String image= user.getImage();
                Picasso.get().load(image).into(small_profile);
                Picasso.get().load(image).into(image_profile);

                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profileid).exists()){
                    profile_button.setText("Following");
                    profile_button.setBackgroundResource(R.drawable.following_button);
                    profile_button.setTextColor(Color.parseColor("#2196F3"));
                }
                else{
                    profile_button.setText("Follow");
                    profile_button.setBackgroundResource(R.drawable.follow_button);
                    profile_button.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }





    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        i++;
                    }
                }

                posts.setText(i+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                photoAdapter.notifyDataSetChanged();
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void mySaves(){
        mySavePost=new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySavePost.add(snapshot.getKey());
                }
                readSaves();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    private void myTagged(){
        myTaggedPost=new ArrayList<>();


        FirebaseDatabase.getInstance().getReference("UsersMentionedPost").child(profileid).addValueEventListener(new ValueEventListener() {
                            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                  myTaggedPost.add(snapshot.getKey());
               }

               readTagged();

            }

           @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
              }
           });
    }


    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_saved.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);

                    for(String id : mySavePost)
                    if(post.getId().equals(id)){
                        postList_saved.add(post);
                    }
                }
                photoAdapter_saved.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    private void readTagged(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_tagged.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);

                    for(String id : myTaggedPost)
                        if(post.getId().equals(id)){
                            postList_tagged.add(post);
                        }
                }
                photoAdapter_tagged.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }




}
