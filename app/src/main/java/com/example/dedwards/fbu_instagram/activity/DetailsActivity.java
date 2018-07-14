package com.example.dedwards.fbu_instagram.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dedwards.fbu_instagram.GlideApp;
import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.fragment.EditNameDialogFragment;
import com.example.dedwards.fbu_instagram.fragment.ProfileFragment;
import com.example.dedwards.fbu_instagram.model.Like;
import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    // declare view objects
    ImageView ivProfileImage;
    ImageView ivImage;
    ImageView ivLike;
    ImageView ivComment;
    ImageView ivDirect;
    ImageView ivSave;
    ImageView ivOptions;
    TextView tvUsername;
    TextView tvDescription;
    TextView tvCreated;
    TextView tvLikeCount;

    private ArrayList<Like> likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // lookup view objects by their id
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivImage = findViewById(R.id.ivImage);
        ivLike = findViewById(R.id.ivLike);
        ivComment = findViewById(R.id.ivComment);
        ivDirect = findViewById(R.id.ivDirect);
        ivSave = findViewById(R.id.ivSave);
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvCreated = findViewById(R.id.tvCreatedAt);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        ivOptions = findViewById(R.id.ivOptions);

        final Post post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        ParseUser user = post.getUser();

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvCreated.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));

        ParseFile image = post.getImage();
        String url = image.getUrl().toString();

        Glide.with(DetailsActivity.this)
                .load(url)
                .into(ivImage);

        ParseFile profileImage = (ParseFile) user.get("profileImage");

        if (user.get("profileImage") != null){
            String profileUrl = profileImage.getUrl().toString();

            GlideApp.with(this)
                    .load(profileUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }

        if (post.getLikes() == null){
            tvLikeCount.setText("0");
        } else{
            tvLikeCount.setText(Integer.toString(post.getLikes().size()));
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    ProfileFragment nextFrag = new ProfileFragment();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.your_placeholder, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                } else {
                    ParseUser user = post.getUser();
                    Intent intent = new Intent(DetailsActivity.this, ProfileActivity.class);
                    intent.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
                    startActivity(intent);
                }
            }
        });

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    ProfileFragment nextFrag = new ProfileFragment();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.your_placeholder, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit();
                } else {
                    ParseUser user = post.getUser();
                    Intent intent = new Intent(DetailsActivity.this, ProfileActivity.class);
                    intent.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
                    startActivity(intent);
                }
            }
        });

        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, CommentActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                startActivity(intent);
            }
        });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean makeLike = true;
                // create new query to parse
                final Like.Query postsQuery = new Like.Query();
                postsQuery.orderByDescending("createdAt");
                postsQuery.getTop().withUser();
                // Define our query conditions
                postsQuery.whereEqualTo("post", post);
                postsQuery.whereEqualTo("user", ParseUser.getCurrentUser());
                // Execute the find asynchronously
                postsQuery.findInBackground(new FindCallback<Like>() {
                    public void done(List<Like> itemList, com.parse.ParseException e) {
                        if (e == null) {
                            // Access the array of results here
                            if (itemList.size() == 0){
                                createLikes(ParseUser.getCurrentUser(), post);
                            }
                        } else {
                            Log.d("item", "Error: " + e.getMessage());

                        }
                    }
                });
            }
        });

        ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });

        // create new query to parse
        final Like.Query postsQuery = new Like.Query();
        postsQuery.orderByDescending("createdAt");
        postsQuery.getTop().withUser();
        // Define our query conditions
        postsQuery.whereEqualTo("post", post);
        postsQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        // Execute the find asynchronously
        postsQuery.findInBackground(new FindCallback<Like>() {
            public void done(List<Like> itemList, com.parse.ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    if (itemList.size() != 0){
                        ivLike.setImageResource(R.mipmap.ufi_heart_active);
                        int color = Color.parseColor("#f05656");
                        ivLike.setColorFilter(color);
                    } else{
                        ivLike.setImageResource(R.mipmap.ufi_heart);
                        int color = Color.parseColor("#000000"); //The color u want
                        ivLike.setColorFilter(color);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    private void createLikes(ParseUser user, final Post post){
        final Like newLike = new Like();
        newLike.setUser(user);
        newLike.setPost(post);

        newLike.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null){
                    Log.d("TimelineActivity", "Like post success!");
                    updatePost(newLike, post);
                } else{
                    Log.d("TimelineActivity", "Failed to like");
                    e.printStackTrace();
                }
            }
        });
    }

    private void updatePost(Like newLike, Post post){
        likes = post.getLikes();
        if (likes == null){
            likes = new ArrayList<>();
        }
        likes.add(newLike);
        post.setLikes(likes);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.d("TimelineActivity", "Like has been added to post");
                } else {
                    Log.d("TimelineActivity", "Failed to add like to post");
                    e.printStackTrace();
                }
            }
        });
    }

    public void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EditNameDialogFragment editNameDialogFragment = EditNameDialogFragment.newInstance("Some Title");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }
}
