package com.msaproject.catal.myappointment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.msaproject.catal.myappointment.models.Reservation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {
    private SimpleDateFormat mSimpleDateFormat;
    private Calendar mCalendar;
    private Activity mActivity;
    private TextView mDate;

    /* Set up view, variables, and OnClickListener */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        mActivity = this;
        mSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault());
        mDate = (TextView) findViewById(R.id.contentMain);
        mDate.setOnClickListener(textListener);
    }

    private final View.OnClickListener textListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCalendar = Calendar.getInstance();
            new DatePickerDialog(mActivity, mDateDataSet, mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(mActivity, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };

    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            mDate.setText(mSimpleDateFormat.format(mCalendar.getTime()));

            Appointments ap = new Appointments();
            Reservation newReservation = new Reservation();
            newReservation.setReservationStart(mSimpleDateFormat.format(mCalendar.getTime()));
            int response = ap.makeAppointment("Beauty Shop",newReservation);

            if(response == 0){
            Toast.makeText(
                    ReservationActivity.this,
                    "APPOINTEMENT HAS BEEN MADE",
                    Toast.LENGTH_LONG).show();}
            else {Toast.makeText(
                    ReservationActivity.this,
                    "DATE/TIME TAKEN",
                    Toast.LENGTH_LONG).show();}
        }
    };

}