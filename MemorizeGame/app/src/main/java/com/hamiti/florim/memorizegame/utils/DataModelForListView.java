package com.hamiti.florim.memorizegame.utils;

/**
 * Created by Florim on 5/27/2018.
 */

public class DataModelForListView {
    String date;
    String score;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public DataModelForListView(String date, String score) {

        this.date = date;
        this.score = score;
    }
}
