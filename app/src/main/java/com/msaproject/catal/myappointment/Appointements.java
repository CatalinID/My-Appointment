package com.msaproject.catal.myappointment;

import android.support.annotation.NonNull;

import java.time.Instant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class Appointements {
    private String bussy;

    DatabaseReference businessRef = FirebaseDatabase.getInstance().getReference().child("business");

    public void makeAppointment(String businessName, String timestamp){
        checkAvailability(businessName,timestamp);
        if(bussy.equals("false")){
            businessRef = businessRef.child(businessName)
                    .child("timestamp");
            businessRef.child("bussy").setValue("true");
            businessRef.child("user").setValue(FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .getUid());
            Instant instant = Instant.now();
            long timeStampSeconds = instant.getEpochSecond();
            businessRef.child("value").setValue(timeStampSeconds);
        }

    }

    public String checkAvailability(String businessName, String timestamp){
        DatabaseReference myConditionRef = businessRef.child(businessName)
                .child("timestamp")
                .child("bussy");

        myConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bussy = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return bussy;
    }
}
