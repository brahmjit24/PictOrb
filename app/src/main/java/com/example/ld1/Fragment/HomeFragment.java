package com.example.ld1.Fragment;

import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ld1.Adapter.PostAdapter;
import com.example.ld1.Adapter.StoryAdapter;
import com.example.ld1.Model.Post;
import com.example.ld1.Model.Story;
import com.example.ld1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postsList;
    private List<String> followingList;

    private RecyclerView storyRecyclerView;
    private List<Story> storyList;
    private StoryAdapter storyAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.home_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postsList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postsList);
        recyclerView.setAdapter(postAdapter);




        storyRecyclerView = view.findViewById(R.id.stories_recycler_view);
        storyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        storyRecyclerView.setLayoutManager(llm);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(),storyList);
        storyRecyclerView.setAdapter(storyAdapter);





        checkFollowingAndReadPostsAndReadStory();


        return view;
    }



    private void checkFollowingAndReadPostsAndReadStory(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followingList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    followingList.add(dataSnapshot1.getKey());
                }
                readPost();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postsList.clear();
                for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                    Post post = dataSnapshot1.getValue(Post.class);
                    for(String id : followingList){
                        if(post.getPublisher().equals(id)){
                            postsList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timeCurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,0,"",FirebaseAuth.getInstance().getCurrentUser().getUid()));

                for(String id : followingList){
                    int countStory = 0 ;
                    Story story = null;
                    for(DataSnapshot snapshot : dataSnapshot.child(id).getChildren()){
                        story = snapshot.getValue(Story.class);
                        if(timeCurrent > story.getStorystart() && timeCurrent < story.getStoryend()){
                            countStory++;
                        }
                    }
                    if(countStory>0){
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
