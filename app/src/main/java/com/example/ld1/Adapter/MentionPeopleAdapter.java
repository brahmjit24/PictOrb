package com.example.ld1.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class MentionPeopleAdapter extends RecyclerView.Adapter<MentionPeopleAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUsers;
    private List<String> myMarkedUsers=new ArrayList<>();
    private FirebaseUser firebaseUser;

    public MentionPeopleAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mention_people_item,parent,false);
        return new MentionPeopleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(position);
        holder.btn_mark.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.name.setText(user.getName());
        Glide.with(mContext).load(user.getImage()).into(holder.image_profile);
        isMarked(user,holder.btn_mark);


        if(user.getId().equals(firebaseUser.getUid())){
            holder.btn_mark.setVisibility(View.GONE);
        }


        holder.btn_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.btn_mark.getText().toString().equals("Mark")){
                    myMarkedUsers.add(user.getId());
                    isMarked(user,holder.btn_mark);
                }
                else{
                    myMarkedUsers.remove(user.getId());
                    isMarked(user,holder.btn_mark);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView name;
        public CircleImageView image_profile;
        public Button btn_mark;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_mark = itemView.findViewById(R.id.btn_mark);
        }
    }


    private void isMarked(final User user, final Button button){

                if(myMarkedUsers.contains(user.getId())){
                    button.setText("Marked");
                    button.setBackgroundResource(R.drawable.marked_button);
                    button.setTextColor(Color.parseColor("#FF008C"));
                }
                else{
                    button.setText("Mark");
                    button.setBackgroundResource(R.drawable.mark_button);
                    button.setTextColor(Color.WHITE);
                }

    }


    public List<String> getMentionedPeople(){
        return myMarkedUsers;
    }

}
