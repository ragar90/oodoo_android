package com.oodoo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ragar90 on 12/21/14.
 */
public class JsonDateParser {
    public static Date parseStringToDate(String date){
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
        //this is zero time so we need to add that TZ indicator for
        if ( date.endsWith( "Z" ) ) {
            date = date.substring( 0, date.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = date.substring( 0, date.length() - inset );
            String s1 = date.substring( date.length() - inset, date.length() );

            date = s0 + "GMT" + s1;
        }
        try{
            return df.parse( date );
        }
        catch(ParseException ex){
            return null;
        }

    }
    public static String parseDateToString(Date date){
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssz" );
        TimeZone tz = TimeZone.getTimeZone( "UTC" );
        df.setTimeZone( tz );
        String output = df.format( date );
        int inset0 = 9;
        int inset1 = 6;
        String s0 = output.substring( 0, output.length() - inset0 );
        String s1 = output.substring( output.length() - inset1, output.length() );
        String result = s0 + s1;
        result = result.replaceAll( "UTC", "+00:00" );
        return result;
    }
}
