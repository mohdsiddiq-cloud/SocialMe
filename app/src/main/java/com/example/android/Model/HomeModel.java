package com.example.android.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class HomeModel {
    private String name, profileImage, imageUrl, uid,comments,description,id;
    @ServerTimestamp
    private Date timeStamp;
    private int likeCount;
    public HomeModel(){

    }

    public HomeModel(String name, String profileImage, String imageUrl, String uid, String comments, String description, String id, Date timeStamp, int likeCount) {
        this.name = name;
        this.profileImage = profileImage;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.comments = comments;
        this.description = description;
        this.id = id;
        this.timeStamp = timeStamp;
        this.likeCount = likeCount;
    }

    public String getUserName() {
        return name;
    }

    public void setUserName(String userName) {
        this.name = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
