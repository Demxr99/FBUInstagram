package com.example.dedwards.fbu_instagram.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.activity.MainActivity;
import com.example.dedwards.fbu_instagram.activity.NewHomeActivity;
import com.example.dedwards.fbu_instagram.activity.NewPhotoActivity;
import com.example.dedwards.fbu_instagram.adapter.ImageAdapter;
import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProfileFragment extends Fragment {

    Button logout;
    ImageView ivProfileImage;
    RecyclerView rvPosts;
    ArrayList<Post> posts;
    TextView tvPostCount;
    TextView tvFollowerCount;
    TextView tvFollwingCount;
    TextView tvName;
    ImageAdapter adapter;
    int postCount;

    ParseUser user;
    private SwipeRefreshLayout swipeContainer;
    // Instance of the progress action-view
    MenuItem miActionProgressItem;
    int skip;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Random rand = new Random();
        int value_1 = rand.nextInt(1000);
        int value_2 = rand.nextInt(5000);
        ((NewHomeActivity)getActivity()).showProgressBar();

        posts = new ArrayList<>();
        adapter = new ImageAdapter(posts);

        rvPosts = view.findViewById(R.id.rvImages);
        tvPostCount = view.findViewById(R.id.tvPostCount);
        tvFollowerCount = view.findViewById(R.id.tvFollowerCount);
        tvFollwingCount = view.findViewById(R.id.tvFollowingCount);
        tvName = view.findViewById(R.id.tvName);

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvPosts.setNestedScrollingEnabled(false);

        ParseUser user = ParseUser.getCurrentUser();
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        logout = view.findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOutInBackground();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewPhotoActivity.class);
                intent.putExtra("first", true);
                startActivity(intent);
            }
        });

        ParseFile image = (ParseFile) user.get("profileImage");

        if (user.get("profileImage") != null){
            String url = image.getUrl().toString();

            Glide.with(getActivity())
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
        tvFollowerCount.setText(Integer.toString(value_1));
        tvFollwingCount.setText(Integer.toString(value_2));
        tvName.setText(user.getUsername());

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

        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    ((NewHomeActivity)getActivity()).showProgressBar();
                    addTimeline();
                }
            }
        });

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
                    postCount = itemList.size();
                    tvPostCount.setText(Integer.toString(postCount));
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
        postsQuery.whereEqualTo("user", user);
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
