package com.example.dedwards.fbu_instagram.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dedwards.fbu_instagram.GlideApp;
import com.example.dedwards.fbu_instagram.R;
import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
    TextView tvCreated;

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
        tvCreated = findViewById(R.id.tvCreatedAt);

        Post post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        ParseUser user = post.getUser();

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvCreated.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));

        ParseFile image = post.getImage();
        String url = image.getUrl().toString();

        Glide.with(DetailsActivity.this)
                .load(url)
                .into(ivImage);

        ParseFile profileImage = (ParseFile) user.get("profileImage");

        if (user.get("profileImage") != null){
            String profileUrl = profileImage.getUrl().toString();

            GlideApp.with(this)
                    .load(profileUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
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
