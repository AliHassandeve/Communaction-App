package com.Intrahubproject.intrahub;

public class NotifacrionHelper {

   private String FriendsID, friendname, friendprofileimage, messag, messagedate, messagetime;


   public NotifacrionHelper(){

   }

    public NotifacrionHelper(String friendsID, String friendname, String friendprofileimage, String messag, String messagedate, String messagetime) {
        FriendsID = friendsID;
        this.friendname = friendname;
        this.friendprofileimage = friendprofileimage;
        this.messag = messag;
        this.messagedate = messagedate;
        this.messagetime = messagetime;
    }

    public String getFriendsID() {
        return FriendsID;
    }

    public void setFriendsID(String friendsID) {
        FriendsID = friendsID;
    }

    public String getFriendname() {
        return friendname;
    }

    public void setFriendname(String friendname) {
        this.friendname = friendname;
    }

    public String getFriendprofileimage() {
        return friendprofileimage;
    }

    public void setFriendprofileimage(String friendprofileimage) {
        this.friendprofileimage = friendprofileimage;
    }

    public String getMessag() {
        return messag;
    }

    public void setMessag(String messag) {
        this.messag = messag;
    }

    public String getMessagedate() {
        return messagedate;
    }

    public void setMessagedate(String messagedate) {
        this.messagedate = messagedate;
    }

    public String getMessagetime() {
        return messagetime;
    }

    public void setMessagetime(String messagetime) {
        this.messagetime = messagetime;
    }
}
