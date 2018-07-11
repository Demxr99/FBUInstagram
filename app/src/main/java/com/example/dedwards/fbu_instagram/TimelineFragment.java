package com.example.dedwards.fbu_instagram;

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
import android.widget.Toast;

import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

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
                    addTimeline();
                }
            }
        });

//        // normally this data should be encapsulated in ViewModels, but shown here for simplicity
//        LiveData<PagedList<Post>> posts;
//
//        public void onCreate(Bundle savedInstanceState) {
//
//            PagedList.Config pagedListConfig =
//                    new PagedList.Config.Builder().setEnablePlaceholders(true)
//                            .setPrefetchDistance(10)
//                            .setInitialLoadSizeHint(10)
//                            .setPageSize(10).build();
//
//            // initial page size to fetch can also be configured here too
//            PagedList.Config config = new PagedList.Config.Builder().setPageSize(20).build();
//
//            ParseDataSourceFactory sourceFactory = new ParseDataSourceFactory();
//
//            posts = new LivePagedListBuilder(factory, config).build();
//
//            posts.observe(this, new Observer<PagedList<Post>>() {
//                @Override
//                public void onChanged(@Nullable PagedList<Post> tweets) {
//                    postAdapter.submitList(tweets);
//                }
//            });
//        }

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
        postsQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        // Execute the find asynchronously
        postsQuery.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> itemList, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    adapter.addAll(itemList);
                    Toast.makeText(getActivity(), "Added posts to timeline", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void addTimeline(){

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
