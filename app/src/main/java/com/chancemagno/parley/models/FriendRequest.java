package com.chancemagno.parley.models;


public class FriendRequest {
    String friendRequestID;

    FriendRequest(){};

    public FriendRequest(String friendRequestID) {
        this.friendRequestID = friendRequestID;
    }


    public String getFriendRequestID() {
        return friendRequestID;
    }
}
