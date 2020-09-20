package com.example.ld1.Adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ld1.CommentsActivity;
import com.example.ld1.FollowersActivity;
import com.example.ld1.Fragment.ProfileFragment;
import com.example.ld1.Model.Post;
import com.example.ld1.Model.User;
import com.example.ld1.R;
import com.example.ld1.Utilities.SquareImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;
    RequestOptions requestOptions = new RequestOptions();



    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        requestOptions.placeholder(R.drawable.ic_placeholder_image);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Post post  = mPost.get(position);
        publisherInfo(holder.image_profile,holder.username,holder.username2,post.getPublisher());
        Glide.with(mContext).load(post.getImageurl()).apply(requestOptions).into(holder.image);
//        Picasso.get().load(post.getImageurl()).placeholder(R.drawable.ic_placeholder_image).into(holder.image);
        if(post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        if(post.getLocation().equals("")){
            holder.location.setVisibility(View.GONE);
        }
        else{
            holder.location.setVisibility(View.VISIBLE);
            holder.location.setText(post.getLocation());
        }

        if(post.getUsermentioned()!=null){
            holder.taggedPeople.setVisibility(View.VISIBLE);
        }
        else{
            holder.taggedPeople.setVisibility(View.GONE);
        }

        //Todo: REST OF CODE TO BE ADDED HERE

        isLiked(post.getId(),holder.like);
        nrLikes(holder.total_likes,post.getId());
        getComments(post.getId(),holder.total_comments);
        isSaved(post.getId(),holder.bookmark);


        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.bookmark.getTag()=="save"){
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getId()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getId()).removeValue();
                }
            }
        });


        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });

        holder.username2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });








        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag()=="like"){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getId())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(),post.getId());
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getId())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.total_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",post.getId());
                intent.putExtra("title","likes");
                mContext.startActivity(intent);
            }
        });

        holder.taggedPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id",post.getId());
                intent.putExtra("title","usertagged");
                mContext.startActivity(intent);
            }
        });



        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View alertView = View.inflate(mContext,R.layout.custom_post_options,null);
                builder.setView(alertView);
                builder.setCancelable(true);
                final AlertDialog dialog = builder.create();
                TextView reportPostBtn = alertView.findViewById(R.id.reportBtn);
                TextView downloadPostBtn = alertView.findViewById(R.id.downloadBtn);
                TextView editDescriptionBtn = alertView.findViewById(R.id.editDescriptionBtn);
                TextView deletePostBtn = alertView.findViewById(R.id.delteBtn);
                TextView goToUserBtn = alertView.findViewById(R.id.goToUserBtn);
                final TextView dismissBtn = alertView.findViewById(R.id.cancelBtn);




                reportPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Report").child(post.getId()).child(firebaseUser.getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("postid",post.getId());
                        map.put("userid",firebaseUser.getUid());
                        map.put("report","inappropriate");
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            map.put("when",dtf.format(now));
                        }else{
                            map.put("when","");
                        }

                        reference.push().setValue(map);
                        Toast.makeText(mContext, "Post has been reported inappropriate.", Toast.LENGTH_SHORT).show();
                    }
                });


                deletePostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
//
                    }
                });


                downloadPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(mContext, "Downloading...", Toast.LENGTH_SHORT).show();

                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        dialog.dismiss();
                        downloadFile(mContext, post.getId(), ".jpg",
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                                        , post.getImageurl());

                    }
                });


                goToUserBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                        editor.putString("profileid",post.getPublisher());
                        editor.apply();

                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                    }
                });







                alertView.findViewById(R.id.line0).setVisibility(View.VISIBLE);
                alertView.findViewById(R.id.line1).setVisibility(View.GONE);
                alertView.findViewById(R.id.line2).setVisibility(View.GONE);
                editDescriptionBtn.setVisibility(View.GONE);
                deletePostBtn.setVisibility(View.GONE);

                if(!post.isIsdownloadable()){
                    alertView.findViewById(R.id.line01).setVisibility(View.GONE);
                    downloadPostBtn.setVisibility(View.GONE);
                }

                if(post.getPublisher().equals(firebaseUser.getUid())){
//                    editDescriptionBtn.setVisibility(View.VISIBLE);
//                    deletePostBtn.setVisibility(View.VISIBLE);
                    reportPostBtn.setVisibility(View.GONE);


                    alertView.findViewById(R.id.line0).setVisibility(View.GONE);
//                    alertView.findViewById(R.id.line1).setVisibility(View.VISIBLE);
//                    alertView.findViewById(R.id.line2).setVisibility(View.VISIBLE);

                }


                dismissBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });



        if(!post.isIscommentable()){
            holder.comment.setVisibility(View.GONE);
            holder.total_comments.setVisibility(View.GONE);
        }


        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid",post.getId());
                intent.putExtra("postcreaterid",post.getPublisher());
                mContext.startActivity(intent);
            }
        });



        holder.total_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid",post.getId());
                mContext.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image_profile;
        TextView username,location,total_likes,username2,description,total_comments;
        ImageView bookmark,like,comment,share,taggedPeople,options;
        SquareImageView image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            location = itemView.findViewById(R.id.location);
            total_likes = itemView.findViewById(R.id.total_likes);
            username2 = itemView.findViewById(R.id.username2);
            description = itemView.findViewById(R.id.description);
            total_comments = itemView.findViewById(R.id.total_comments);
            bookmark = itemView.findViewById(R.id.bookmark);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            share = itemView.findViewById(R.id.share);
            taggedPeople = itemView.findViewById(R.id.taggedPeople);
            options = itemView.findViewById(R.id.options);
            image = itemView.findViewById(R.id.image);
        }
    }



    private  void addNotification(String userid,String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String,Object> map = new HashMap<>();
        map.put("userid",firebaseUser.getUid());
        map.put("text","liked your post");
        map.put("postid",postid);
        map.put("ispost",true);

        reference.push().setValue(map);
    }




    public void isSaved(final String postid, final ImageView imageView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(postid)){
                    imageView.setImageResource(R.drawable.ic_bookmarked);
                    imageView.setTag("saved");
                }
                else{
                    imageView.setImageResource(R.drawable.ic_bookmark_outline);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    public void isLiked(String postid, final ImageView imageView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(firebaseUser.getUid())){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }
                else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }






    private void getComments(String postid,final TextView total_comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total_comments.setText("View all "+dataSnapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }






    private void nrLikes(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }






    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView username2, String userId){
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImage()).apply(requestOptions).into(image_profile);
//                Picasso.get().load(user.getImage()).placeholder(R.drawable.ic_placeholder_image).into(image_profile);
                username.setText(user.getUsername());
                username2.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadFile(Context context,String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(destinationDirectory,fileName+fileExtension);
        dm.enqueue(request);

    }

}
