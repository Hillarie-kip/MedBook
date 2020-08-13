package com.hillarie.medbook;

public class pojo {
    public String title,body;
    public  int userId,id;

    public pojo(String title, String body, int userId, int id) {
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.id = id;

    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }
}
