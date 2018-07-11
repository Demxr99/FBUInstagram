package com.example.dedwards.fbu_instagram;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;

import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class ParsePositionalDataSource extends PositionalDataSource<Post> {

    // define basic query here
    public ParseQuery<Post> getQuery() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.orderByDescending("createdAt");
        return postsQuery;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Post> callback) {
        // get basic query
        ParseQuery<Post> query = getQuery();

        // Use values passed when PagedList was created.
        query.setLimit(params.requestedLoadSize);
        query.setSkip(params.requestedStartPosition);

        try {
            // loadInitial() should run queries synchronously so the initial list will not be empty
            // subsequent fetches can be async
            int count = query.count();
            List<Post> posts = query.find();

            // return info back to PagedList
            callback.onResult(posts, params.requestedStartPosition, count);
        } catch (ParseException e) {
            // retry logic here
        }
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Post> callback) {
        // get basic query
        ParseQuery<Post> query = getQuery();

        query.setLimit(params.loadSize);

        // fetch the next set from a different offset
        query.setSkip(params.startPosition);

        try {
            // run queries synchronously since function is called on a background thread
            List<Post> posts = query.find();

            // return info back to PagedList
            callback.onResult(posts);
        } catch (ParseException e) {
            // retry logic here
        }
    }
}
