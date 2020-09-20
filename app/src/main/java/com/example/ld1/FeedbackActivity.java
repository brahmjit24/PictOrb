package com.example.ld1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ld1.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smilerating.SmileRating;
import com.hsalf.smileyrating.SmileyRating;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedbackActivity extends AppCompatActivity {

    TextView userFullnameDisplayFeedback;
    EditText edit_text_feedback;
    SmileyRating smile_rating;
    Button cancelFeedbackbutton,submitFeedbackbutton;
    CircleImageView userProfileImageFeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        userFullnameDisplayFeedback = findViewById(R.id.userFullnameDisplayFeedback);
        edit_text_feedback = findViewById(R.id.edit_text_feedback);
        smile_rating = findViewById(R.id.smile_rating);
        cancelFeedbackbutton = findViewById(R.id.cancelFeedbackbutton);
        submitFeedbackbutton = findViewById(R.id.submitFeedbackbutton);
        userProfileImageFeedback = findViewById(R.id.userProfileImageFeedback);

        userinfo();
        cancelFeedbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final int[] rating = {0};

        smile_rating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                rating[0] = type.getRating();
            }
        });
        submitFeedbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String feedback = edit_text_feedback.getText().toString();
                feedback = feedback.trim();
                if(feedback!=null){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Feedback").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("rating", rating[0]);
                    map.put("feedback",feedback);
                    reference.setValue(map);
                    Toast.makeText(FeedbackActivity.this, "Feedback sent", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void userinfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userFullnameDisplayFeedback.setText(user.getUsername());
                Picasso.get().load(user.getImage()).into(userProfileImageFeedback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
