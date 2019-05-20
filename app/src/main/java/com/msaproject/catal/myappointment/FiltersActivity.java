package com.msaproject.catal.myappointment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FiltersActivity extends AppCompatActivity {

    private static final String TAG = "com.msaproject.catal.myappointment.FiltersActivity";

    //vars
    private boolean twice;

    //widgets
    private Button mSave;
    private EditText mCity, mStateProvince, mCountry;
    private ImageView mBackArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mSave = (Button) findViewById(R.id.btnSave);
        mCity = (EditText) findViewById(R.id.input_city);
        mStateProvince = (EditText) findViewById(R.id.input_state_province);
        mCountry = (EditText) findViewById(R.id.input_country);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);

        init();

    }

    private void init(){

        getFilterPreferences();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: saving...");

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FiltersActivity.this);
                SharedPreferences.Editor editor = preferences.edit();

                Log.d(TAG, "onClick: city: " + mCity.getText().toString());
                editor.putString(getString(R.string.preferences_city), mCity.getText().toString());
                editor.commit();

                Log.d(TAG, "onClick: state/province: " + mStateProvince.getText().toString());
                editor.putString(getString(R.string.preferences_state_province), mStateProvince.getText().toString());
                editor.commit();

                Log.d(TAG, "onClick: country: " + mCountry.getText().toString());
                editor.putString(getString(R.string.preferences_country), mCountry.getText().toString());
                editor.commit();
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back.");
                finish();
            }
        });
    }

    private void getFilterPreferences(){
        Log.d(TAG, "getFilterPreferences: retrieving saved preferences.");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String country = preferences.getString(getString(R.string.preferences_country), "");
        String state_province = preferences.getString(getString(R.string.preferences_state_province), "");
        String city = preferences.getString(getString(R.string.preferences_city), "");

        mCountry.setText(country);
        mStateProvince.setText(state_province);
        mCity.setText(city);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (twice){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }

        twice = true;
        Intent intent = new Intent(FiltersActivity.this,SearchActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        }, 3000);

        startActivity(intent);
    }
}