package com.example.android.Model;

public class CommentModel {

    String comment, commentId, postId, uid, name, profileImage;

    public CommentModel() {
    }

    public CommentModel(String comment, String commentId, String postId, String uid, String name, String profileImage) {
        this.comment = comment;
        this.commentId = commentId;
        this.postId = postId;
        this.uid = uid;
        this.name = name;
        this.profileImage = profileImage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
