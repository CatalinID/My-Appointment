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

public class BusinessManageActivity extends AppCompatActivity {

    private static final String TAG = "BusinessManageActivity";

    private RecyclerView manageView;
    private String businessId;

    private ArrayList<Reservation> upcomingResList = new ArrayList<>();
    private ReservationRecycleViewAdapter upcomingResAdapter;
    private DocumentSnapshot lastReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_manage);

        manageView = findViewById(R.id.recycle_view);
        initRecyclerView();
        getBusinessId();
        getBusinessReservations();

    }

    private void getBusinessId(){ //getuserid from /business and look in /reservations for reservations with that businessid
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void initRecyclerView(){
        if(upcomingResAdapter == null){
            upcomingResAdapter = new ReservationRecycleViewAdapter(this, upcomingResList);
        }
        manageView.setLayoutManager(new LinearLayoutManager(this));
        manageView.setAdapter(upcomingResAdapter);
    }

    private void getBusinessReservations(){
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
                        upcomingResList.add(reservation);
                    }

                    if(task.getResult().size() != 0){
                        lastReservation = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    }
                    upcomingResAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(
                            BusinessManageActivity.this,
                            "Getting business reservations FAILED!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
