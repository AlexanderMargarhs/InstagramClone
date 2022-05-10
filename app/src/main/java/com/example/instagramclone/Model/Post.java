package com.example.instagramclone.Model;

// This class is created to handle all the Posts as an individual item in a recycle view.
public class Post
{
    private String postid;
    private String postimage;
    private String description;
    private String publisher;

    // Constructors.
    public Post()
    { }

    public Post(String postid, String postimage, String description, String publisher)
    {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
    }

    // Getters.
    public String getPostid()
    {
        return postid;
    }

    public String getPostimage()
    {
        return postimage;
    }

    public String getDescription()
    {
        return description;
    }

    public String getPublisher()
    {
        return publisher;
    }

    // Setters.
    public void setPostid(String postid)
    {
        this.postid = postid;
    }

    public void setPostimage(String postimage)
    {
        this.postimage = postimage;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }
}
