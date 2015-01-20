package com.oodoo.trackerapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class TrackerReceiver extends BroadcastReceiver {
    public static final String POSITION_HASHTAG = "#position";
    public TrackerReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String recMsgString = "";
        String fromAddress = "";
        SmsMessage recMsg = null;
        byte[] data = null;
        if (bundle != null){
            //---retrieve the SMS message received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdus.length; i++)
            {
                recMsg = SmsMessage.createFromPdu((byte[]) pdus[i]);
                try{
                    data = recMsg.getUserData();
                    recMsgString = recMsg.getDisplayMessageBody();
                }
                catch (Exception e){
                    Log.d("SMS ERROR", e.getMessage());
                }
                fromAddress = recMsg.getOriginatingAddress();
            }
        }
        //Expected message = #position 47.686743,-122.320098
        int tagIndex = recMsgString.lastIndexOf(POSITION_HASHTAG);
        int positionIndex = tagIndex + POSITION_HASHTAG.length()+1;
        if(recMsgString.contains(POSITION_HASHTAG) && positionIndex == 10){
            String msg = fromAddress + ": " + recMsgString;
            Intent result = new Intent(context, TrackActivity.class);
            result.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            String[] posStrings = recMsgString.substring(positionIndex).split(",");
            double[] position = {Double.parseDouble(posStrings[0]), Double.parseDouble(posStrings[1])};
            result.putExtra(TrackActivity.POSITION,position);
            result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(result);
        }
    }
}
