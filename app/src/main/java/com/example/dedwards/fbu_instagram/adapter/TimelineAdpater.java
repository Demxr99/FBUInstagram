package com.example.dedwards.fbu_instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dedwards.fbu_instagram.GlideApp;
import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.activity.CommentActivity;
import com.example.dedwards.fbu_instagram.activity.DetailsActivity;
import com.example.dedwards.fbu_instagram.activity.ProfileActivity;
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

public class TimelineAdpater extends RecyclerView.Adapter<TimelineAdpater.ViewHolder> {

    // list of all posts
    private List<Post> mPosts;
    Context context;
    private ArrayList<Like> likes;

    // pass in Tweets array in the constructor
    public TimelineAdpater(List<Post> posts){
        mPosts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate layout
        View postView = inflater.inflate(R.layout.item_post, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Post post = mPosts.get(i);
        ParseUser user = post.getUser();
        ParseFile image = post.getImage();
        String url = image.getUrl().toString();

        Glide.with(context)
                .load(url)
                .into(viewHolder.ivImage);

        viewHolder.tvUsername.setText(post.getUser().getUsername());

        if (post.getLikes() == null){
            viewHolder.tvLikeCount.setText("0");
        } else{
            viewHolder.tvLikeCount.setText(Integer.toString(post.getLikes().size()));
        }

        if (user.get("profileImage") != null){
            ParseFile profileImage = (ParseFile) user.get("profileImage");
            String profileUrl = profileImage.getUrl().toString();

            GlideApp.with(context)
                    .load(profileUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.ivProfileImage);
        }

        viewHolder.tvCreatedAt.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));

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
                        viewHolder.ivLike.setImageResource(R.mipmap.ufi_heart_active);
                        int color = Color.parseColor("#f05656");
                        viewHolder.ivLike.setColorFilter(color);
                    } else{
                        viewHolder.ivLike.setImageResource(R.mipmap.ufi_heart);
                        int color = Color.parseColor("#000000"); //The color u want
                        viewHolder.ivLike.setColorFilter(color);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // create viewholder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // declare view objects
        ImageView ivProfileImage;
        ImageView ivImage;
        ImageView ivLike;
        ImageView ivComment;
        ImageView ivDirect;
        ImageView ivSave;
        TextView tvUsername;
        TextView tvCreatedAt;
        TextView tvLikeCount;

        public ViewHolder(View itemView) {
            super(itemView);
            // lookup view objects by their id
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivDirect = itemView.findViewById(R.id.ivDirect);
            ivSave = itemView.findViewById(R.id.ivSave);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    // TODO send the user in the intent
                    context.startActivity(intent);
                }
            });

            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    // TODO send the user in the intent
                    context.startActivity(intent);
                }
            });

            ivComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // gets position of item in ArrayList
                    int position = getAdapterPosition();
                    // checks if position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        // get the post at position
                        Post post = mPosts.get(position);

                        Intent intent = new Intent(context, CommentActivity.class);
                        intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                        context.startActivity(intent);
                    }
                }
            });

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Boolean makeLike = true;
                    // gets position of item in ArrayList
                    int position = getAdapterPosition();
                    // checks if position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        // get the post at position
                        final Post post = mPosts.get(position);

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
                }
            });

            // adds onClick listener to view holder
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            // gets position of item in ArrayList
            int position = getAdapterPosition();
            // checks if position is valid
            if (position != RecyclerView.NO_POSITION) {
                // get the post at position
                Post post = mPosts.get(position);

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(intent);
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
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
                    notifyDataSetChanged();
                } else {
                    Log.d("TimelineActivity", "Failed to add like to post");
                    e.printStackTrace();
                }
            }
        });
    }
}
