package com.oodoo.models;

import android.util.Log;

import com.oodoo.utils.JsonDateParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ragar90 on 12/28/14.
 */
public class TrackingRoute {
    public TrackingRoute(JSONObject jsonObject){
        try{
            id = jsonObject.getInt("id");
            deviceId = jsonObject.getInt("device_id");
            startedAt = jsonObject.getString("started_at");
            endAt = jsonObject.getString("end_at");
            createdAt = jsonObject.getString("created_at");
            updatedAt = jsonObject.getString("updated_at");
            JSONArray jsonPositions =  jsonObject.getJSONArray("positions");
            int total = jsonPositions.length();
            positions = new ArrayList<Position>();
            for(int i = 0; i < total; i++){
                positions.add(new Position(jsonPositions.getJSONObject(i)));
            }
        }
        catch(Exception ex){
            Log.e("TrackingRouteError", ex.getMessage());
        }
    }
    private int id;
    private int deviceId;
    private String startedAt;
    private String endAt;
    private String createdAt;
    private String updatedAt;
    private List<Position> positions;
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getEndAt() {
        return endAt;
    }
    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }
    public String getStartedAt() {
        return startedAt;
    }
    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }
    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
    public Date getParsedStartedAt(){ return JsonDateParser.parseStringToDate(this.startedAt); }
    public Date getParsedEndAt(){ return JsonDateParser.parseStringToDate(this.endAt); }
    public void setParsedStartedAt(Date started_at){
        this.startedAt = JsonDateParser.parseDateToString(started_at);
    }
    public void setParsedEndAt(Date end_at){
        this.endAt = JsonDateParser.parseDateToString(end_at);
    }
    public List<Position> getPositions() {
        return positions;
    }
}
