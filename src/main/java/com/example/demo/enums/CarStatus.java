package com.example.demo.enums;

public enum CarStatus {
    LISTED("已上架"),
    PENDING_REVIEW("待審核"),
    SOLD("已售出"),
    DELISTED("已下架");

    private final String description;

    CarStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
