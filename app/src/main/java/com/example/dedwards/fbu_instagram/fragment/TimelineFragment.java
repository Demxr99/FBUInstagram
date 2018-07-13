package com.example.dedwards.fbu_instagram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.activity.NewHomeActivity;
import com.example.dedwards.fbu_instagram.adapter.TimelineAdpater;
import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment {

    // list of movies currently playing
    ArrayList<Post> posts;
    // the recycler view for movies
    RecyclerView rvTimeline;
    // the adapter connected to the recycler view
    TimelineAdpater adapter;
    private SwipeRefreshLayout swipeContainer;
    Context context;
    int skip;

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        skip = 20;
        // initialize the list of movies
        posts = new ArrayList<>();
        // initialize the adapter
        adapter = new TimelineAdpater(posts);


        // resolve the recycler view and connect a layout manager and movie adapter
        rvTimeline = view.findViewById(R.id.rvTimeline);
        rvTimeline.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTimeline.setAdapter(adapter);

        rvTimeline.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    ((NewHomeActivity)getActivity()).showProgressBar();
                    addTimeline();
                }
            }
        });

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
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
        postsQuery.orderByDescending("createdAt");
        postsQuery.getTop().withUser();
        // Define our query conditions
//        postsQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        // Execute the find asynchronously
        postsQuery.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> itemList, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    adapter.addAll(itemList);
                    ((NewHomeActivity)getActivity()).hideProgressBar();
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
//        postsQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        postsQuery.setSkip(skip);
        // Execute the find asynchronously
        postsQuery.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> itemList, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    adapter.addAll(itemList);
                    ((NewHomeActivity)getActivity()).hideProgressBar();
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
