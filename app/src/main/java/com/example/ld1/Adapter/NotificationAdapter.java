package com.example.ld1.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ld1.Fragment.PostDetailFragment;
import com.example.ld1.Fragment.ProfileFragment;
import com.example.ld1.Model.Notification;
import com.example.ld1.Model.Post;
import com.example.ld1.Model.User;
import com.example.ld1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context mContext;
    List<Notification> mNotification;

    FirebaseUser firebaseUser;

    RequestOptions requestOptions = new RequestOptions();
    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        requestOptions.placeholder(R.drawable.ic_placeholder_image);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View  view = LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Notification notification = mNotification.get(position);

            holder.text.setText(notification.getText());
            getUserInfo(holder.image_profile,holder.username,notification.getUserid());
            if(notification.isIspost())
               { holder.post_image.setVisibility(View.VISIBLE);
                   getPostImage(holder.post_image,notification.getPostid());
               }
            else{
                holder.post_image.setVisibility(View.GONE);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(notification.isIspost()){
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                        editor.putString("postid",notification.getPostid());
                        editor.apply();

                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailFragment()).commit();
                    }
                    else{
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                        editor.putString("profileid",notification.getUserid());
                        editor.apply();
                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image_profile;
        ImageView post_image;
        TextView text,username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            text = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.username);

        }
    }

    private void getUserInfo(final CircleImageView imageView, final TextView username, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                Glide.with(mContext).load(user.getImage()).into(imageView);
//                Picasso.get().load(user.getImage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getPostImage(final ImageView imageView, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getImageurl()).apply(requestOptions).into(imageView);
//                Picasso.get().load(post.getImageurl()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
