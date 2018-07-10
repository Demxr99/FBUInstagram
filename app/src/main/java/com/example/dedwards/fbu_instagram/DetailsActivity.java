package com.example.dedwards.fbu_instagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

    // declare view objects
    ImageView ivProfileImage;
    ImageView ivImage;
    ImageView ivLike;
    ImageView ivComment;
    ImageView ivDirect;
    ImageView ivSave;
    TextView tvUsername;
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // lookup view objects by their id
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivImage = findViewById(R.id.ivImage);
        ivLike = findViewById(R.id.ivLike);
        ivComment = findViewById(R.id.ivComment);
        ivDirect = findViewById(R.id.ivDirect);
        ivSave = findViewById(R.id.ivSave);
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);

        Post post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());

        ParseFile image = post.getImage();
        String url = image.getUrl().toString();

        Glide.with(DetailsActivity.this)
                .load(url)
                .into(ivImage);
    }
}
