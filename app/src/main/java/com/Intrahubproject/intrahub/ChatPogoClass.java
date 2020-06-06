package com.Intrahubproject.intrahub;

public class ChatPogoClass  {

    String username, imageuri, usermessage, usermessagetime, typestatas;

    public ChatPogoClass(){

    }

    public ChatPogoClass(String username, String imageuri, String usermessage, String usermessagetime, String typestatas) {
        this.username = username;
        this.imageuri = imageuri;
        this.usermessage = usermessage;
        this.usermessagetime = usermessagetime;
        this.typestatas = typestatas;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public String getUsermessage() {
        return usermessage;
    }

    public void setUsermessage(String usermessage) {
        this.usermessage = usermessage;
    }

    public String getUsermessagetime() {
        return usermessagetime;
    }

    public void setUsermessagetime(String usermessagetime) {
        this.usermessagetime = usermessagetime;
    }

    public String getTypestatas() {
        return typestatas;
    }

    public void setTypestatas(String typestatas) {
        this.typestatas = typestatas;
    }
}
