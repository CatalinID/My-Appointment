package com.msaproject.catal.myappointment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.msaproject.catal.myappointment.models.Reservation;
import com.msaproject.catal.myappointment.util.ReservationRecycleViewAdapter;

import java.util.ArrayList;

public class AppointmentsActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentsActivity ";

    private RecyclerView historyView;

    private ArrayList<Reservation> upcomingResList = new ArrayList<>();
    private ReservationRecycleViewAdapter upcomingResAdapter;
    private DocumentSnapshot lastReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        historyView = findViewById(R.id.recycle_view);
        initRecyclerView();
        getUserReservations();

    }

    private void initRecyclerView(){
        if(upcomingResAdapter == null){
            upcomingResAdapter = new ReservationRecycleViewAdapter(this, upcomingResList);
        }
        historyView.setLayoutManager(new LinearLayoutManager(this));
        historyView.setAdapter(upcomingResAdapter);
    }

    private void getUserReservations(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference reservationsCollectionRef = db.collection("reservations");

        Query reservationsQuery = null;

        if(lastReservation != null){
            reservationsQuery = reservationsCollectionRef
                    .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderBy("reservationBegin", Query.Direction.ASCENDING)
                    .startAfter(lastReservation);
        }
        else {
            reservationsQuery = reservationsCollectionRef
                    .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderBy("reservationBegin", Query.Direction.ASCENDING);
        }
        reservationsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        Reservation reservation = document.toObject(Reservation.class);
                        /*Timestamp timestamp = new Timestamp(System.currentTimeMillis()/1000,0);
                        if(timestamp.compareTo(reservation.getReservationBegin()) < 1 )*/
                            upcomingResList.add(reservation);
                    }

                    if(task.getResult().size() != 0){
                        lastReservation = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    }
                    upcomingResAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(
                            AppointmentsActivity.this,
                            "Getting your reservations FAILED!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
