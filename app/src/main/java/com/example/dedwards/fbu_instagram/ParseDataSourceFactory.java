package com.example.dedwards.fbu_instagram;

import android.arch.paging.DataSource;

import com.example.dedwards.fbu_instagram.model.Post;

public class ParseDataSourceFactory extends DataSource.Factory<Integer, Post> {

    @Override
    public DataSource<Integer, Post> create() {
        ParsePositionalDataSource source = new ParsePositionalDataSource();
        return source;
    }
}
