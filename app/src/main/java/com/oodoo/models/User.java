package com.oodoo.models;

import android.util.Log;

import com.oodoo.utils.JsonDateParser;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by ragar90 on 1/11/15.
 */
public class User {
    String name;
    String lastname;
    String facebookToken;
    String email;
    String createdAt;
    String updatedAt;

    public User(JSONObject json){
        try{
            this.name = json.getString("name");
            this.lastname = json.getString("lastname");
            this.facebookToken = json.getString("facebook_token");
            this.email = json.getString("email");
            this.createdAt = json.getString("created_at");
            this.updatedAt = 	json.getString("updated_at");
        }
        catch (Exception ex){
            Log.e("UserAuthError", ex.getMessage());
        }
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getFacebookToken() {
        return facebookToken;
    }
    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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
