package com.example.ld1.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ld1.AddStoryActivity;
import com.example.ld1.Model.Story;
import com.example.ld1.Model.User;
import com.example.ld1.R;
import com.example.ld1.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private Context mContext;
    private List<Story> mStory;

    FirebaseUser firebaseUser;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 0){
            view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item,parent,false);
        }
        else
            view = LayoutInflater.from(mContext).inflate(R.layout.story_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Story story = mStory.get(position);

        userInfo(holder,story.getUserid(),position);
        if(holder.getAdapterPosition()!=0){
            seenStory(holder,story.getUserid());
        }else{
            myStory(holder.addstory_text,holder.story_plus,false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.getAdapterPosition()==0){
                        myStory(holder.addstory_text,holder.story_plus,true);
                }else{
                    Intent intent  = new Intent(mContext, StoryActivity.class);
                    intent.putExtra("userid",story.getUserid());
                    mContext.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView story_photo,story_plus,story_photo_seen;
        public TextView story_username,addstory_text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.addstory_text);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
        {
            return 0;
        }
        return 1;
    }



    private void userInfo(final ViewHolder viewHolder, String userid, final int position){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImage()).into(viewHolder.story_photo);
                if(position!=0){
                    Glide.with(mContext).load(user.getImage()).into(viewHolder.story_photo_seen);
                    String str = user.getUsername();
                    if(str.length()>7)
                        str=str.substring(0,7)+"...";
                    viewHolder.story_username.setText(str);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private  void myStory(final TextView textView, final ImageView imageView, final boolean click){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0 ;
                long timecurrent = System.currentTimeMillis();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Story story = snapshot.getValue(Story.class);
                    if(timecurrent > story.getStorystart()&& timecurrent<story.getStoryend()){
                        count++;
                    }
                }
                if(click){
                    if(count>0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        View alertView = View.inflate(mContext,R.layout.custom_options_for_story,null);
                        builder.setView(alertView);
                        builder.setCancelable(true);
                        final AlertDialog dialog = builder.create();
                        TextView viewStoryBtn = alertView.findViewById(R.id.viewStoryBtn);
                        TextView addStoryBtn = alertView.findViewById(R.id.addStoryBtn);

                        viewStoryBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent intent  = new Intent(mContext, StoryActivity.class);
                                intent.putExtra("userid",firebaseUser.getUid());
                                mContext.startActivity(intent);
                            }
                        });

                        addStoryBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                Intent intent = new Intent(mContext, AddStoryActivity.class);
                                mContext.startActivity(intent);
                            }
                        });
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }else{
                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                    }
                }else{
                    if(count == 0){
                        textView.setText("Add story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                    else{
                        textView.setText("My story");
                        imageView.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void seenStory(final ViewHolder viewHolder, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(!snapshot.child("views").child(firebaseUser.getUid()).exists() && System.currentTimeMillis() < snapshot.getValue(Story.class).getStoryend()){
                            i++;
                    }
                }

                if(i > 0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }
                else{
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
