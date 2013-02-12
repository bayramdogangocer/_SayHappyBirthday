package com.example.facebookapplication;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.util.Log;

public class WallPostRequestListener extends BaseRequestListener {

    public void onComplete(final String response) {
        Log.d("Facebook-Example", "Got response: " + response);
        String message = "<empty>";
        try {
            JSONObject json = Util.parseJson(response);
            message = json.getString("message");
        } catch (JSONException e) {
            Log.w("Facebook-Example", "JSON Error in response");
        } catch (FacebookError e) {
            Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
        }
        
        
    }

	@Override
	public void onComplete(String response, Object state) {
		// TODO Auto-generated method stub
		
	}
}

