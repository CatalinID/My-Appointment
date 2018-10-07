package com.msaproject.catal.myappointment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        TextView textView=findViewById(R.id.textUserName);
        textView.setText("Welcome User!");
    }
}
