package com.example.dedwards.fbu_instagram.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.adapter.ImageAdapter;
import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    Context context;

    ImageView ivProfileImage;
    ImageAdapter adapter;
    RecyclerView rvPosts;
    ArrayList<Post> posts;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = ParseUser.getCurrentUser();

        posts = new ArrayList<>();
        adapter = new ImageAdapter(posts);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        rvPosts = findViewById(R.id.rvImages);

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new GridLayoutManager(this, 3));

        if (user.get("profileImage") != null){
            ParseFile profileImage = (ParseFile) user.get("profileImage");
            String profileUrl = profileImage.getUrl().toString();

            Glide.with(this)
                    .load(profileUrl)
                    .into(ivProfileImage);
        }

        populateTimeline();
    }

    public void populateTimeline(){
        // create new query to parse
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();
        // Define our query conditions
        postsQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        // Execute the find asynchronously
        postsQuery.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> itemList, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    adapter.addAll(itemList);
                    Toast.makeText(ProfileActivity.this, "Added posts to timeline", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}
