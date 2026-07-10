package com.brimedge.smsbot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SMSReceiver extends BroadcastReceiver {
    
    private static final String API_URL = "https://plndmqvlkchbtcfeoson.supabase.co/functions/v1/sms-reply";
    private static final String API_KEY = "YOUR_API_KEY_HERE";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                    String from = sms.getOriginatingAddress();
                    String message = sms.getMessageBody();
                    
                    Log.d("SMSBot", "SMS kutoka " + from + ": " + message);
                    
                    String reply = getAIReply(from, message);
                    if (reply != null) {
                        SMSHelper.sendSMS(context, from, reply);
                    }
                }
            }
        }
    }
    
    private String getAIReply(String from, String message) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-api-key", API_KEY);
            conn.setDoOutput(true);
            
            String jsonBody = "{\"from\":\"" + from + "\",\"message\":\"" + message + "\"}";
            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes());
            os.flush();
            os.close();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            
            String responseText = response.toString();
            try {
                org.json.JSONObject json = new org.json.JSONObject(responseText);
                return json.getString("reply");
            } catch (Exception e) {
                return responseText;
            }
            
        } catch (Exception e) {
            Log.e("SMSBot", "API Error: " + e.getMessage());
            return null;
        }
    }
}
