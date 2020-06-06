package com.Intrahubproject.intrahub;

public class MessageHolder {

    private String message, time, date, from, type;

    public MessageHolder(){

    }

    public MessageHolder(String message, String time, String date, String from, String type) {
        this.message = message;
        this.time = time;
        this.date = date;
        this.from = from;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
