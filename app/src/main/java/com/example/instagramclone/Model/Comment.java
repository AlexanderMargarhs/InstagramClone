package com.example.instagramclone.Model;

// This class is created to handle all the Comments as an individual item in a recycle view.
public class Comment
{
    private String comment;
    private String publisher;
    private String commentid;

    // Constructors.
    public Comment()
    { }

    public Comment(String comment, String publisher , String commentid)
    {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid = commentid;
    }

    // Getters.
    public String getComment()
    {
        return comment;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public String getCommentid()
    {
        return commentid;
    }

    // Setters.
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    public void setCommentid(String commentid)
    {
        this.commentid = commentid;
    }
}
