package com.example.instagramclone.Model;

// This class is created to handle all the hashtags as an individual item in a recycle view.
public class HashTag
{
    private String tag;
    private String postid;

    // Constructors.
    public HashTag()
    { }

    public HashTag(String tag, String postid)
    {
        this.tag = tag;
        this.postid = postid;
    }

    // Getters.
    public String getTag()
    {
        return tag;
    }

    public String getPostid()
    {
        return postid;
    }

    // Setters.
    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public void setPostid(String postid)
    {
        this.postid = postid;
    }
}
