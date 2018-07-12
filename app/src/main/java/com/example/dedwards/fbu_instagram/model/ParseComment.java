package com.example.dedwards.fbu_instagram.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class ParseComment extends ParseObject {
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_USER = "user";
    private static final String KEY_POST = "post";

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){ put(KEY_USER, user); }

    public Post getPost() { return (Post) getParseObject(KEY_POST); }

    public void setPost(Post post) { put(KEY_POST, post); }

    public static class Query extends ParseQuery<ParseComment> {
        public Query() {
            super(ParseComment.class);
        }

        public ParseComment.Query getTop(){
            setLimit(20);
            return this;
        }

        public ParseComment.Query withUser(){
            include("user");
            return this;
        }
    }

}
