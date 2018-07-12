package com.example.dedwards.fbu_instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dedwards.fbu_instagram.activity.DetailsActivity;
import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    // list of all posts
    private List<Post> mPosts;
    Context context;

    // pass in Tweets array in the constructor
    public ImageAdapter(List<Post> posts){
        mPosts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate layout
        View postView = inflater.inflate(R.layout.item_image, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ParseUser user = ParseUser.getCurrentUser();
        Post post = mPosts.get(i);
        ParseFile image = post.getImage();
        String url = image.getUrl().toString();

        Glide.with(context)
                .load(url)
                .into(viewHolder.ivImage);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // create viewholder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // declare view objects
        ImageView ivImage;

        public ViewHolder(View itemView) {
            super(itemView);
            // lookup view objects by their id
            ivImage = itemView.findViewById(R.id.ivImage);

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
}

