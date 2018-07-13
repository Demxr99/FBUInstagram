package com.example.dedwards.fbu_instagram.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.dedwards.fbu_instagram.GlideApp;
import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.adapter.ImageAdapter;
import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    Context context;

    ImageView ivProfileImage;
    ImageAdapter adapter;
    RecyclerView rvPosts;
    TextView tvPostCount;
    TextView tvFollowerCount;
    TextView tvFollwingCount;

    ArrayList<Post> posts;
    ParseUser user;
    // Instance of the progress action-view
    MenuItem miActionProgressItem;
    private SwipeRefreshLayout swipeContainer;
    int skip;
    int postCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        skip = 20;
        postCount = 0;
        Random rand = new Random();
        int value_1 = rand.nextInt(1000);
        int value_2 = rand.nextInt(5000);

        user = Parcels.unwrap(getIntent().getParcelableExtra(ParseUser.class.getSimpleName()));

        posts = new ArrayList<>();
        adapter = new ImageAdapter(posts);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        rvPosts = findViewById(R.id.rvImages);
        tvPostCount = findViewById(R.id.tvPostCount);
        tvFollowerCount = findViewById(R.id.tvFollowerCount);
        tvFollwingCount = findViewById(R.id.tvFollowingCount);

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new GridLayoutManager(this, 3));

        if (user.get("profileImage") != null){
            ParseFile profileImage = (ParseFile) user.get("profileImage");
            String profileUrl = profileImage.getUrl().toString();

            GlideApp.with(this)
                    .load(profileUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }

        tvPostCount = findViewById(R.id.tvPostCount);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    showProgressBar();
                    addTimeline();
                }
            }
        });

        tvFollowerCount.setText(Integer.toString(value_1));
        tvFollwingCount.setText(Integer.toString(value_2));
        populateTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    public void populateTimeline(){
        // create new query to parse
        final Post.Query postsQuery = new Post.Query();
        postsQuery.orderByDescending("createdAt");
        postsQuery.getTop().withUser();
        // Define our query conditions
        postsQuery.whereEqualTo("user", user);
        // Execute the find asynchronously
        postsQuery.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> itemList, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    adapter.addAll(itemList);
                    postCount = itemList.size();
                    tvPostCount.setText(Integer.toString(postCount));
                    Toast.makeText(ProfileActivity.this, "Added posts to timeline", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void addTimeline(){
        // create new query to parse
        final Post.Query postsQuery = new Post.Query();
        postsQuery.orderByDescending("createdAt");
        postsQuery.getTop().withUser();
        // Define our query conditions
        postsQuery.whereEqualTo("user", user);
        postsQuery.setSkip(skip);
        // Execute the find asynchronously
        postsQuery.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> itemList, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    adapter.addAll(itemList);
                    hideProgressBar();
                    if (itemList.size() != 0) {
                        skip += 20;
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        if (skip > 20) {
            skip -= 20;
        }
        adapter.clear();
        populateTimeline();
        swipeContainer.setRefreshing(false);
    }
}
