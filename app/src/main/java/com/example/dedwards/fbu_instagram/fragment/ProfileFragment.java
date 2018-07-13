package com.example.dedwards.fbu_instagram.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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

public class ProfileFragment extends Fragment {

    Button logout;
    ImageView ivProfileImage;
    RecyclerView rvPosts;
    ArrayList<Post> posts;
    ImageAdapter adapter;

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

        ((NewHomeActivity)getActivity()).showProgressBar();

        posts = new ArrayList<>();
        adapter = new ImageAdapter(posts);

        rvPosts = view.findViewById(R.id.rvImages);

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
                    ((NewHomeActivity)getActivity()).hideProgressBar();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}
