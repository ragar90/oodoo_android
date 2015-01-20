package com.oodoo.models;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.oodoo.utils.JsonDateParser;


public class Device {
    @SerializedName("id")
    private int     id;

    @SerializedName("phone_number")
    private String  phoneNumber;

    @SerializedName("alias_name")
    private String  aliasName;

    @SerializedName("lock")
    private boolean lock;

    @SerializedName("moving")
    private boolean moving;

    @SerializedName("user_id")
    private int     userId;

    @SerializedName("created_at")
    private String  createdAt;

    @SerializedName("updated_at")
    private String  updatedAt;

    @Override
    public String toString() {
        return this.getAliasName();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAliasName() {
        return aliasName;
    }
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
    public boolean isLock() {
        return lock;
    }
    public void setLock(boolean lock) {
        this.lock = lock;
    }
    public boolean isMoving() {
        return moving;
    }
    public void setMoving(boolean moving) {
        this.moving = moving;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Date getParsedCreatedAt() {
        return JsonDateParser.parseStringToDate(this.createdAt);
    }
    public void setParsedCreatedAt(Date created_at) {

        this.createdAt = JsonDateParser.parseDateToString(created_at);;
    }
    public Date getParsedUpdatedAt() {
        return JsonDateParser.parseStringToDate(this.updatedAt);
    }
    public void setParsedUpdatedAt(Date updated_at) {
        this.updatedAt = JsonDateParser.parseDateToString(updated_at);
    }
}
