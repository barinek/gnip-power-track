package com.barinek.gnip;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Keyword {
    private final int id;
    private final String keyword;
    private final int count;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;
    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy h:mm a");

    public Keyword(int id, String keyword, int count, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.keyword = keyword;
        this.count = count;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getCount() {
        return count;
    }

    public String getCreatedAt() {
        return format.format(createdAt);
    }

    public String getUpdatedAt() {
        return format.format(updatedAt);
    }
}
