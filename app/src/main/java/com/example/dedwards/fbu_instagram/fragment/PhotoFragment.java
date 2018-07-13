package com.example.dedwards.fbu_instagram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dedwards.fbu_instagram.R;

public class PhotoFragment extends Fragment {

    // Define the listener of the interface type
    // listener will the activity instance containing fragment
    private OnItemSelectedListener listener;
    private ImageView ivTakePhoto;
    private ImageView ivGallery;

    public PhotoFragment() {
        // Required empty public constructor
    }

    // Define the events that the fragment will use to communicate
    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void onTakePhoto();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivTakePhoto = view.findViewById(R.id.ivTakePhoto);
        ivGallery = view.findViewById(R.id.ivGallery);
        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFragment nextFrag= new GalleryFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.your_placeholder, nextFrag,"findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        ivTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onTakePhoto();
            }
        });

    }
}
