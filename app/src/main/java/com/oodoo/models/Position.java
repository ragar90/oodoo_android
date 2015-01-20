package com.oodoo.models;

import com.oodoo.utils.JsonDateParser;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by ragar90 on 1/5/15.
 */
public class Position {
    private int     id;
    private int     trackingRouteID;
    private String  message;
    private double  latitude;
    private double  longitude;
    private String  createdAt;
    private String  updatedAt;
    public Position(JSONObject jsonObject){
        try{
            id = jsonObject.getInt("id");
            trackingRouteID = jsonObject.getInt("tracking_route_id");
            message = jsonObject.getString("message");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            createdAt = jsonObject.getString("createdAt");
            updatedAt = jsonObject.getString("updatedAt");

        }
        catch(Exception ex){

        }

    }




    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTrackingRouteID() {
        return trackingRouteID;
    }
    public void setTrackingRouteID(int trackingRouteID) {
        this.trackingRouteID = trackingRouteID;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
