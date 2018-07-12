package com.example.dedwards.fbu_instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.activity.ProfileActivity;
import com.example.dedwards.fbu_instagram.model.ParseComment;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    // list of all posts
    private List<ParseComment> mComments;
    Context context;

    // pass in Tweets array in the constructor
    public CommentAdapter(List<ParseComment> comments){
        mComments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate layout
        View postView = inflater.inflate(R.layout.item_comment, viewGroup, false);
        CommentAdapter.ViewHolder viewHolder = new CommentAdapter.ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ParseComment comment = mComments.get(i);
        ParseUser user = comment.getUser();
        ParseFile image = (ParseFile) user.get("profileImage");
        String url = image.getUrl().toString();

        viewHolder.tvComment.setText(comment.getDescription());
        viewHolder.tvCreatedAt.setText(getRelativeTimeAgo(comment.getCreatedAt().toString()));

        Glide.with(context)
                .load(url)
                .into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    // create viewholder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        // declare view objects
        ImageView ivProfileImage;
        TextView tvComment;
        TextView tvCreatedAt;

        public ViewHolder(View itemView) {
            super(itemView);
            // lookup view objects by their id
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    // TODO send the user in the intent
                    context.startActivity(intent);
                }
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mComments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<ParseComment> list) {
        mComments.addAll(list);
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
}
