package com.msaproject.catal.myappointment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BusinessDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_business);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("business/Beauty Shop");
       /* db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String description, prices, contact, address, hours;

                description = dataSnapshot.child("description").getValue(String.class);
                address = dataSnapshot.child("address").getValue(String.class);
                contact = dataSnapshot.child("contact").getValue(String.class);
                prices = dataSnapshot.child("prices").getValue(String.class);
                hours = dataSnapshot.child("hours").getValue(String.class);

                TextView businessDescription = findViewById(R.id.descripiton_business);
                TextView businessAddress = findViewById(R.id.address_business);
                TextView businessContact = findViewById(R.id.contact_business);
                TextView businessHours = findViewById(R.id.hours_business);
                TextView businessPrices = findViewById(R.id.prices_business);

                businessDescription.setText(description);
                businessAddress.setText(address);
                businessContact.setText(contact);
                businessHours.setText(hours);
                businessPrices.setText(prices);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        findViewById(R.id.reservation_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReservationActivity.class));
            }
        });*/
    }
}
