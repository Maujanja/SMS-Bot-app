package com.brimedge.smsbot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("Brim Edge SMS Bot Inafanya Kazi\n\nBot inasikiliza SMS...");
        textView.setTextSize(20);
        textView.setPadding(40, 80, 40, 40);
        setContentView(textView);
    }
}
