package com.Intrahubproject.intrahub;

public class AdminMessagePogo  {

    String AdminMessage,current_time,current_date;

    public AdminMessagePogo(){

    }

    public AdminMessagePogo(String adminMessage, String current_time, String current_date) {
        AdminMessage = adminMessage;
        this.current_time = current_time;
        this.current_date = current_date;
    }

    public String getAdminMessage() {
        return AdminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        AdminMessage = adminMessage;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }
}
