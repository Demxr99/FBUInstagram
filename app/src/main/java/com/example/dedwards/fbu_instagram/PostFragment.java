package com.example.dedwards.fbu_instagram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.dedwards.fbu_instagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

public class PostFragment extends Fragment {

    final private static String FILE = "file";
    EditText etDescription;
    ImageView ivImage;
    Button btnPost;
    Bitmap takenImage;
    File photoFile;
    ParseUser user;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment create(File photoFile){
        final Bundle arguments = new Bundle();
        arguments.putSerializable(FILE, photoFile);

        final PostFragment fragment = new PostFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ParseUser.getCurrentUser();
        photoFile = (File) getArguments().getSerializable(FILE);
        takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        takenImage = rotateBitmapOrientation(photoFile.getPath());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etDescription = view.findViewById(R.id.etDescription);
        ivImage = view.findViewById(R.id.ivImage);
        ivImage.setImageBitmap(takenImage);
        btnPost = view.findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String description = etDescription.getText().toString();
                final ParseFile parseFile = new ParseFile(photoFile);
                parseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Log.d("HomeActivity", "ParseFile has been saved");
                            createPosts(description, parseFile, user);
//                            Intent intent = new Intent(getActivity(), NewHomeActivity.class);
//                            startActivity(intent);
                        } else{
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void createPosts(String description, ParseFile imageFile, ParseUser user){
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d("HomeActivity", "Create post success!");
                } else{
                    Log.d("HomeActivity", "Failed to post");
                    e.printStackTrace();
                }
            }
        });
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }
}
