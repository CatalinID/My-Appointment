package com.msaproject.catal.myappointment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.msaproject.catal.myappointment.models.Reservation;
import com.msaproject.catal.myappointment.util.ReservationRecycleViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {

    private static final String TAG = "ReservationActivity";

    private SimpleDateFormat mSimpleDateFormat, mSimpleDayMonthYear;
    private Calendar mCalendar;
    private Activity mActivity;
    private TextView mDate,mTime,mPickedDate;
    private String mBusinessId,mBusinessName;
    private boolean twice;

    private RecyclerView takenView;

    private ArrayList<Reservation> takenResList = new ArrayList<>();
    private ReservationRecycleViewAdapter takenResAdapter;
    private DocumentSnapshot lastReservation;

    /* Set up view, variables, and OnClickListener */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Bundle b = getIntent().getExtras();

        if(b != null){
            mBusinessId = b.getString(getString(R.string.arg_business_id));
            mBusinessName = b.getString(getString(R.string.arg_business_name));
        }

        Log.d(TAG, "onCreate: got the post id: " + mBusinessId);

        mPickedDate = findViewById(R.id.datePicked);

        takenView = findViewById(R.id.recycle_view);

        initRecyclerView();

        mActivity = this;
        mSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        mSimpleDayMonthYear = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        mDate = findViewById(R.id.dateMain);
        mDate.setOnClickListener(textListener);

        mTime = findViewById(R.id.timeMain);
        mTime.setOnClickListener(timeListener);


    }

    private final View.OnClickListener textListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCalendar = Calendar.getInstance();
            new DatePickerDialog(mActivity, mDateDataSet, mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    private final View.OnClickListener timeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new TimePickerDialog(mActivity, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };

    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            getBusinessReservations(mBusinessId);
            //new TimePickerDialog(mActivity, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
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
            newReservation.setBusiness_id(mBusinessId);
            newReservation.setBusinessName(mBusinessName);
            newReservation.setState(getString(R.string.arg_reservation_state));

            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ReservationActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString(getString(R.string.msg_token_fmt), token);
                    editor.commit();

                    // Log and toast
                    Log.d(TAG, token);

                }
            });

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ReservationActivity.this);

            newReservation.setMessaging_token(preferences.getString(getString(R.string.msg_token_fmt), ""));



            newReservationRef.set(newReservation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()) {
                   Toast.makeText(
                           ReservationActivity.this,
                           "APPOINTMENT HAS BEEN MADE",
                           Toast.LENGTH_LONG).show();
               } else {
                   Toast.makeText(
                           ReservationActivity.this,
                           "APPOINTMENT FAILED, DATE TAKEN",
                           Toast.LENGTH_LONG).show();
               }
                }
            });
        }

    };

    private void initRecyclerView(){
        if(takenResAdapter == null){
            takenResAdapter = new ReservationRecycleViewAdapter(this, takenResList);
        }
        takenView.setLayoutManager(new LinearLayoutManager(this));
        takenView.setAdapter(takenResAdapter);
    }

    private void getBusinessReservations(String businessId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference reservationsCollectionRef = db.collection("reservations");

        Query reservationsQuery = null;

        if(lastReservation != null){
            reservationsQuery = reservationsCollectionRef
                    .whereEqualTo("business_id", businessId)
                    .orderBy("reservationBegin", Query.Direction.ASCENDING)
                    .startAfter(lastReservation);
        }
        else {
            reservationsQuery = reservationsCollectionRef
                    .whereEqualTo("business_id", businessId)
                    .orderBy("reservationBegin", Query.Direction.ASCENDING);
        }
        reservationsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Reservation reservation = document.toObject(Reservation.class);
                        int month =  mCalendar.get(mCalendar.MONTH) + 1;
                        String date = mCalendar.get(mCalendar.DAY_OF_MONTH) + " " + month + " " + mCalendar.get(mCalendar.YEAR);

                        Log.d(TAG, "date vs getMonthYear " + date + " : " + reservation.getDayMonthYear());

                        if(date.equals(reservation.getDayMonthYear())) {
                            reservation.setBusinessName("");
                            reservation.setState("TAKEN");
                            takenResList.add(reservation);
                        }
                    }

                    if(task.getResult().size() != 0){
                        lastReservation = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    }
                    takenResAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(
                            ReservationActivity.this,
                            "Getting TAKEN appointments FAILED!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mPickedDate.setText(mSimpleDayMonthYear.format(mCalendar.getTime()));
        mPickedDate.setVisibility(View.VISIBLE);
        mDate.setVisibility(View.INVISIBLE);
        mTime.setVisibility(View.VISIBLE);
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