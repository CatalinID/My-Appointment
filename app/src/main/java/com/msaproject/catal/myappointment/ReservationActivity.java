package com.msaproject.catal.myappointment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.msaproject.catal.myappointment.models.Reservation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {
    private SimpleDateFormat mSimpleDateFormat;
    private Calendar mCalendar;
    private Activity mActivity;
    private TextView mDate;
    private boolean twice;

    /* Set up view, variables, and OnClickListener */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        mActivity = this;
        mSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        mDate = findViewById(R.id.dateMain);
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

            Reservation newReservation = new Reservation();

            SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
            SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM-yyyy");
            SimpleDateFormat startHourFormat = new SimpleDateFormat("h:mm");
            Timestamp reservationBegin = new Timestamp(mCalendar.getTimeInMillis()/1000,0);

            newReservation.setDay(dayFormat.format(mCalendar.getTime()));
            newReservation.setStartHour(startHourFormat.format(mCalendar.getTime()));
            newReservation.setMonthYear(monthYearFormat.format(mCalendar.getTime()));
            newReservation.setReservationBegin(reservationBegin);


            FirebaseFirestore businessRef = FirebaseFirestore.getInstance();

            DocumentReference newReservationRef = businessRef.collection("reservations" ).document();//.document(businessName).collection(reservation.getMonthYear()+ "-" + reservation.getDay()).document(reservation.getStartHour());

            newReservation.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
            newReservation.setBusiness_id(newReservationRef.getId());

            newReservationRef.set(newReservation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()) {
                   Toast.makeText(
                           ReservationActivity.this,
                           "APPOINTEMENT HAS BEEN MADE",
                           Toast.LENGTH_LONG).show();
               } else {
                   Toast.makeText(
                           ReservationActivity.this,
                           "APPOINTEMENT FAILED",
                           Toast.LENGTH_LONG).show();
               }
                }
            });
        }

    };

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
        Intent intent = new Intent(ReservationActivity.this,MainPageActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        }, 3000);

        startActivity(intent);
    }

}