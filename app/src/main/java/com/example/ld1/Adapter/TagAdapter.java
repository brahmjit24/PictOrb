package com.example.ld1.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ld1.Fragment.ProfileFragment;
import com.example.ld1.Model.Tag;
import com.example.ld1.Model.User;
import com.example.ld1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import static android.content.Context.MODE_PRIVATE;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

        private Context mContext;
        private List<Tag> mTags;

        private FirebaseUser firebaseUser;

        private ArrayList<String> myTags=new ArrayList<>();

        public TagAdapter(Context mContext, List<Tag> mTags) {
            this.mContext = mContext;
            this.mTags = mTags;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item,parent,false);
            return (new TagAdapter.ViewHolder(view));
        }


    @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final Tag tag = mTags.get(position);
            holder.btn_add.setVisibility(View.VISIBLE);
            holder.tagname.setText(tag.getName());
            isAdded(tag.getName(),holder.btn_add);
            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.btn_add.getText().toString().equals("Add")){
                        myTags.add(holder.tagname.getText().toString());
                        isAdded(holder.tagname.getText().toString(),holder.btn_add);
                    }
                    else{
                        myTags.remove(holder.tagname.getText().toString());
                        isAdded(holder.tagname.getText().toString(),holder.btn_add);
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return mTags.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tagname;
            public Button btn_add;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tagname = itemView.findViewById(R.id.tagname);
                btn_add = itemView.findViewById(R.id.btn_add);
            }
        }


        private void isAdded(final String tagName, final Button button){

            if(myTags.contains(tagName))
            {
                button.setText("Added");
                button.setBackgroundResource(R.drawable.added_tag_button);
                button.setTextColor(Color.parseColor("#FFAB00"));
            }
            else{
                button.setText("Add");
                button.setBackgroundResource(R.drawable.add_tag_button);
                button.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }
        public List<String> getTagsAdded(){
            return myTags;
        }

 }


