package com.msaproject.catal.myappointment;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.msaproject.catal.myappointment.models.Reservation;

class Appointments {
    private FirebaseFirestore businessRef = FirebaseFirestore.getInstance();

   public void makeAppointment(String businessName, Reservation reservation){

        DocumentReference newReservationRef = businessRef.collection("reservations" ).document(businessName).collection(reservation.getMonthYear()+ "-" + reservation.getDay()).document(reservation.getStartHour());

       reservation.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

       newReservationRef.set(reservation).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               /*if (task.isSuccessful()) {
                   System.out.println("Document created");
               } else {
                   System.out.println("Document already exists");
               }*/
           }
       });
    }

}
