package com.example.dedwards.fbu_instagram.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject{
    private static final String KEY_USER = "user";
    private static final String KEY_POST = "post";


    public ParseUser getUser(){ return getParseUser(KEY_USER); }

    public void setUser(ParseUser user){ put(KEY_USER, user); }

    public Post getPost() { return (Post) getParseObject(KEY_POST); }

    public void setPost(Post post) { put(KEY_POST, post); }

    public static class Query extends ParseQuery<Like> {
        public Query() {
            super(Like.class);
        }

        public Like.Query getTop(){
            setLimit(20);
            return this;
        }

        public Like.Query withUser(){
            include("user");
            return this;
        }

        public Like.Query withPost(){
            include("post");
            return this;
        }
    }
}
