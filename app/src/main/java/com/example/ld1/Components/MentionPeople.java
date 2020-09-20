package com.example.ld1.Components;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ld1.Adapter.MentionPeopleAdapter;
import com.example.ld1.Model.User;
import com.example.ld1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MentionPeople {
    Activity activity;
    AlertDialog dialog;
    private MentionPeopleAdapter mentionPeopleAdapter;
    private List<User> mUsers;
    private List<String> myTaggedUser;

    public MentionPeople(Activity activity){
        this.activity = activity;
        mUsers = new ArrayList<>();
        mentionPeopleAdapter = new MentionPeopleAdapter(activity.getApplicationContext(),mUsers);
        myTaggedUser = new ArrayList<>();
    }


    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.custom_mention_people,null);
        builder.setView(alertView);
        builder.setCancelable(false);
        dialog = builder.create();

        final TextView dismissBtn = alertView.findViewById(R.id.closeBtn);
        final TextView doneBtn = alertView.findViewById(R.id.doneBtn);
        final RecyclerView peopleView = alertView.findViewById(R.id.mention_people_recycler_view);
        final RelativeLayout layout = alertView.findViewById(R.id.mention_people_img_layout);
        final EditText searchUser = alertView.findViewById(R.id.search_users);
        final TextView viewListBtn = alertView.findViewById(R.id.view_list_btn);
        final TextView displayList = alertView.findViewById(R.id.list_display_textview);


        peopleView.setHasFixedSize(true);
        peopleView.setLayoutManager(new LinearLayoutManager(activity));
        peopleView.setAdapter(mentionPeopleAdapter);

        viewListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayList.setVisibility(View.VISIBLE);
                peopleView.setVisibility(View.GONE);
                String usersList="";
                for(String username:mentionPeopleAdapter.getMentionedPeople()){
                    usersList=usersList+"  "+username;
                }
                displayList.setText(usersList);
            }
        });


        searchUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    layout.setVisibility(View.GONE);
                    peopleView.setVisibility(View.VISIBLE);
//                    viewListBtn.setVisibility(View.VISIBLE);
                    displayList.setVisibility(View.GONE);
                }
            }
        });

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchAllUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:LOGIC

                myTaggedUser = mentionPeopleAdapter.getMentionedPeople();


                dismiss();
            }
        });
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void searchAllUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                mentionPeopleAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void dismiss(){
        this.dialog.dismiss();
    }


    public List<String> getMyTaggedUser() {
        return myTaggedUser;
    }
}
