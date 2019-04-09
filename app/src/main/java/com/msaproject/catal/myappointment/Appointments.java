package com.msaproject.catal.myappointment;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.msaproject.catal.myappointment.models.Reservation;

class Appointments {
    private int bussy = 0; //appointment can be made

//    private DatabaseReference businessRef = FirebaseDatabase.getInstance().getReference().child("business"); //old way

    private FirebaseFirestore businessRef = FirebaseFirestore.getInstance();


    public int makeAppointment(String businessName, Reservation reservation){
        //DocumentReference newReservationRef = businessRef.collection("reservations").document(businessName);

        DocumentReference newReservationRef = businessRef.collection("reservations" ).document(businessName).collection(reservation.getMonthYear()+ "-" + reservation.getDay()).document(reservation.getStartHour());

        reservation.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //checkAvailability(businessName,timestamp);

        newReservationRef.set(reservation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                   bussy = 0;
                }
                else
                {  bussy = 1;}
            }
        });

        return bussy;
    }

    /*private void checkAvailability(String businessName, String timestamp){
        DatabaseReference myConditionRef = businessRef.child(businessName)
                .child(timestamp)
                .child("bussy");

        myConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bussy = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}
