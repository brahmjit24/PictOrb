package com.example.ld1.Fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.ld1.Adapter.MyPhotoAdapter;
import com.example.ld1.Adapter.UserAdapter;
import com.example.ld1.Model.Post;
import com.example.ld1.Model.User;
import com.example.ld1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    public ExploreFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView,recycler_view_global;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private EditText searchBar;
    private Button  cancelSearch;

    MyPhotoAdapter photoAdapter;
    List<Post> postList;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_explore, container, false);

        recycler_view_global = view.findViewById(R.id.recycler_view_global);
        recycler_view_global.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recycler_view_global.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        photoAdapter = new MyPhotoAdapter(getContext(),postList);
        recycler_view_global.setAdapter(photoAdapter);


        recyclerView = view.findViewById(R.id.recycler_view);
         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         mUsers = new ArrayList<>();
         userAdapter = new UserAdapter(getContext(),mUsers,true);
         recyclerView.setAdapter(userAdapter);

         searchBar = view.findViewById(R.id.search_bar);
         cancelSearch = view.findViewById(R.id.cancel_search);
         readUsers();
         searchBar.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });

         cancelSearch.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                     InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                 } catch (Exception e) {
                     // TODO: handle exception
                 }
                 searchBar.setText("");
                 searchBar.clearFocus();
                 recyclerView.setVisibility(View.GONE);

             }
         });

         searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    cancelSearch.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recycler_view_global.setVisibility(View.GONE);
                }
                else{
                    cancelSearch.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    recycler_view_global.setVisibility(View.VISIBLE);
                }
            }
        });


        globalPhotos();






        return view;

    }

    private void globalPhotos() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if(post.isIsglobal()&& Math.random() < 0.5){
                        postList.add(post);
                    }
                }
                Collections.shuffle(postList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void searchUsers(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }


                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(searchBar.getText().toString().equals("")){
                    mUsers.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
