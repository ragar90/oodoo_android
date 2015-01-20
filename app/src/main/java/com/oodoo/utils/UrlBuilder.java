package com.oodoo.utils;
import android.content.Context;
import com.oodoo.trackerapp.R;

/**
 * Created by ragar90 on 12/27/14.
 */
public class UrlBuilder {
    public static String buildCollectionUrl(Class resource, Context context){
        String resourceRoot = resource.getCanonicalName().toLowerCase();
        String serverUrl = context.getString(R.string.server_url);
        String url = serverUrl.concat(resourceRoot);
        return url;
    }

    public static String buildMemberUrl(Class resource, String id, Context context){
        String resourceRoot = resource.getSimpleName().toLowerCase();
        String serverUrl = context.getString(R.string.server_url);
        String memberPath = resourceRoot.concat("/").concat(id);
        String url = serverUrl.concat(memberPath);
        return url;
    }
}
