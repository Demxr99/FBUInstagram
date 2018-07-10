package com.example.dedwards.fbu_instagram;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    // list of movies currently playing
    ArrayList<Post> posts;
    // the recycler view for movies
    RecyclerView rvTimeline;
    // the adapter connected to the recycler view
    TimelineAdpater adapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // initialize the list of movies
        posts = new ArrayList<>();
        // initialize the adapter
        adapter = new TimelineAdpater(posts);

        // resolve the recycler view and connect a layout manager and movie adapter
        rvTimeline = findViewById(R.id.rvTimeline);
        rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        rvTimeline.setAdapter(adapter);

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
                    Toast.makeText(TimelineActivity.this, "Added posts to timeline", Toast.LENGTH_SHORT).show();
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
        adapter.clear();
        populateTimeline();
        swipeContainer.setRefreshing(false);
    }
}
