package com.example.ld1.Components;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ld1.Adapter.TagAdapter;
import com.example.ld1.Model.Tag;
import com.example.ld1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class AddTag {
    Activity activity;
    AlertDialog dialog;
    private List<Tag> mTags;
    private TagAdapter tagAdapter;
    private List<String> tagsAdded;

    public AddTag(Activity activity){
        this.activity = activity;
        mTags = new ArrayList<>();
        tagAdapter = new TagAdapter(activity.getApplicationContext(),mTags);
        tagsAdded = new ArrayList<>();
    }



    public void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertView = inflater.inflate(R.layout.custom_add_tags,null);
        builder.setView(alertView);
        builder.setCancelable(false);
        dialog = builder.create();

        final TextView dismissBtn = alertView.findViewById(R.id.closeBtn);
        final TextView doneBtn = alertView.findViewById(R.id.doneBtn);
        final TextView createTagBtn = alertView.findViewById(R.id.createTagBtn);
        final TextView createBtn = alertView.findViewById(R.id.createBtn);
        final TextView title = alertView.findViewById(R.id.tag_title);
        final EditText searchTag = alertView.findViewById(R.id.search_tags);
        final RelativeLayout layout = alertView.findViewById(R.id.add_tag_layout);
        final RecyclerView tagsView = alertView.findViewById(R.id.tag_recycler_view);
        final TextView viewList = alertView.findViewById(R.id.view_list_btn);
        tagsView.setHasFixedSize(true);
        tagsView.setLayoutManager(new LinearLayoutManager(activity));
        tagsView.setAdapter(tagAdapter);

        searchTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(title.getText().equals("Add Tags")) {
                    layout.setVisibility(GONE);
                    tagsView.setVisibility(View.VISIBLE);
                    searchTag(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:get tags added here
//                Toast.makeText(activity, tagAdapter.getTagsAdded().toString(), Toast.LENGTH_SHORT).show();
                tagsAdded = tagAdapter.getTagsAdded();
                dismiss();
            }
        });

        viewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.VISIBLE);
                layout.findViewById(R.id.tag_img).setVisibility(GONE);
                tagsView.setVisibility(GONE);
                TextView msgtv = layout.findViewById(R.id.text_msg);
                String mssg = "";
                for (String ta:tagAdapter.getTagsAdded() ){
                    mssg=mssg+" "+ta;
                }
                msgtv.setText(mssg);
            }
        });

        createTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTag.setText("");
                doneBtn.setVisibility(GONE);
                createTagBtn.setVisibility(GONE);
                createBtn.setVisibility(View.VISIBLE);
                searchTag.setHint("Enter tag name");
                tagsView.setVisibility(GONE);
                searchTag.setCompoundDrawables(null,null,null,null);
                title.setText("New Tag");
                layout.setVisibility(View.VISIBLE);
                layout.findViewById(R.id.tag_img).setVisibility(View.VISIBLE);
                viewList.setVisibility(GONE);
                TextView msgtv = layout.findViewById(R.id.text_msg);
                msgtv.setText("\nA valid tag Eg  :Qwerty.\n1. First character is :\n2. Contains no number.\n3. All letters in small case except starting letter.");
            }
        });

        searchTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(title.getText().equals("Add Tags")) {
                    layout.setVisibility(GONE);
                    tagsView.setVisibility(View.VISIBLE);
                    viewList.setVisibility(View.VISIBLE);
                }
            }
        });
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tagName = searchTag.getText().toString();
                if(tagName.equals(""))
                {
                    Toast.makeText(activity, "Tag name left empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isValid(tagName)){
                    final LoadingDialog ld= new LoadingDialog(activity);
                    ld.show();
                    FirebaseDatabase.getInstance().getReference().child("Tags").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(tagName)){
                                ld.dismiss();
                                ErrorDialog ed = new ErrorDialog(activity);ed.setErrorMessage("Tag already exists.");ed.show();
                            }
                            else{
                                HashMap<String,Object> map = new HashMap<>();
                                map.put("name",tagName);
                                map.put("by", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                    LocalDateTime now = LocalDateTime.now();
                                    map.put("when",dtf.format(now));
                                }else{
                                    map.put("when","");
                                }
//                                map.put("time", (new Date()).);
                                FirebaseDatabase.getInstance().getReference().child("Tags").child(tagName).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            ld.dismiss();
                                            SuccessMessageDialog smd = new SuccessMessageDialog(activity);smd.setMessage("Tag -> "+ tagName +" has been created.");smd.show();
                                            try {
                                                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
                                            searchTag.setText("");
                                            searchTag.clearFocus();
                                            doneBtn.setVisibility(View.VISIBLE);
                                            createTagBtn.setVisibility(View.VISIBLE);
                                            createBtn.setVisibility(GONE);
                                            viewList.setVisibility(GONE);
                                            searchTag.setHint("Search tag");
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                searchTag.setCompoundDrawables(activity.getDrawable(R.drawable.ic_search), null, null, null);
                                            }
                                            title.setText("Add Tags");
                                            layout.setVisibility(View.VISIBLE);
                                            TextView msgtv = layout.findViewById(R.id.text_msg);
                                            msgtv.setText("Search tag to get started.");
                                        }else{
                                            ld.dismiss();
                                            ErrorDialog ed = new ErrorDialog(activity);ed.setErrorMessage(task.getException().getMessage());ed.show();
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    ErrorDialog ed = new ErrorDialog(activity);ed.setErrorMessage("Tag name not valid.");ed.show();
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void searchTag(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Tags").orderByChild("name").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTags.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Tag tag = snapshot.getValue(Tag.class);
                    mTags.add(tag);
                }
                tagAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isValid(String tagName) {
        if(tagName.length()<3)
            return false;
        if(tagName.contains("0")||tagName.contains("1")||tagName.contains("2")||tagName.contains("3")||tagName.contains("4")||tagName.contains("5")||
                tagName.contains("6")||tagName.contains("7")||tagName.contains("8")||tagName.contains("9")||tagName.charAt(0)!=':'){
            return false;
        }
        if(!(tagName.charAt(1)>=65&&tagName.charAt(1)<=90)){
            return false;
        }

        for (char c:tagName.substring(2).toCharArray()) {
            int cASCII = (int)c;
            if(cASCII>=65&&cASCII<=90){
                return false;
            }
        }
        return true;
    }

    public void dismiss(){
        this.dialog.dismiss();
    }


    public List<String> getTagsAdded() {
        return tagsAdded;
    }
}
