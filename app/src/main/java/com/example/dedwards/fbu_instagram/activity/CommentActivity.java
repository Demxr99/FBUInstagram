package com.example.dedwards.fbu_instagram.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.adapter.CommentAdapter;
import com.example.dedwards.fbu_instagram.model.ParseComment;
import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    EditText etComment;
    TextView tvPost;
    RecyclerView rvComments;
    Post post;
    ArrayList<ParseComment> comments;
    CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // initialize the list of movies
        comments = new ArrayList<>();
        // initialize the adapter
        adapter = new CommentAdapter(comments);

        // resolve the recycler view and connect a layout manager and movie adapter
        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        etComment = findViewById(R.id.etComment);
        tvPost = findViewById(R.id.tvPost);
        rvComments = findViewById(R.id.rvComments);

        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etComment.getText().toString();
                ParseUser user = ParseUser.getCurrentUser();
                createComments(comment, user, post);
                etComment.setText("");
            }
        });

        populateComments();
    }

    private void createComments(String description, ParseUser user, final Post post){
        final ParseComment newComment = new ParseComment();
        newComment.setDescription(description);
        newComment.setUser(user);
        newComment.setPost(post);

        newComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d("CommentActivity", "Create comment success!");
                    updateComments(newComment, post);
                } else{
                    Log.d("CommentActivity", "Failed to comment");
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateComments(ParseComment newComment, Post post){
        comments = post.getComments();
        if (comments == null){
            comments = new ArrayList<>();
        }
        comments.add(newComment);
        post.setComments(comments);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("CommentActivity", "Comment has been added to post");
                    adapter.clear();
                    populateComments();
                } else {
                    Log.d("CommentActivity", "Failed to add comment to post");
                    e.printStackTrace();
                }
            }
        });
    }

    public void populateComments(){
        // create new query to parse
        final ParseComment.Query postsQuery = new ParseComment.Query();
        postsQuery.orderByDescending("createdAt");
        postsQuery.getTop().withUser();
        // Define our query conditions
        postsQuery.whereEqualTo("post", post);
        // Execute the find asynchronously
        postsQuery.findInBackground(new FindCallback<ParseComment>() {
            public void done(List<ParseComment> itemList, ParseException e) {
                if (e == null) {
                    // Access the array of results here
                    adapter.addAll(itemList);
                    Toast.makeText(CommentActivity.this, "Added comments to timeline", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}
