package com.msaproject.catal.myappointment;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;


public class MainPage extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        String userEmail = user.getEmail();
        String userName = user.getDisplayName();

        TextView textView = findViewById(R.id.textUserName);
        if(userName != null) textView.setText("Welcome, " + userName +"!");
        else textView.setText("Welcome!");

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("business/Beauty Shop");
        db.addValueEventListener(new ValueEventListener() {
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
        });

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.color.md_dark_background)
                .addProfiles(
                        new ProfileDrawerItem().withName(userName).withEmail(userEmail).withIcon(R.drawable.avatar)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withCloseDrawerOnProfileListClick(true)
                .build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("Home")
                .withTextColor(Color.LTGRAY)
                .withSelectedColor(Color.LTGRAY)
                .withSelectedTextColor(Color.BLACK)
                .withIcon(R.drawable.home);
        final PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName("My Appointments")
                .withTextColor(Color.LTGRAY)
                .withSelectedColor(Color.LTGRAY)
                .withSelectedTextColor(Color.BLACK)
                .withIcon(R.drawable.calendar);
        final PrimaryDrawerItem item3 = new PrimaryDrawerItem().withName("Settings")
                .withTextColor(Color.LTGRAY)
                .withSelectedColor(Color.LTGRAY)
                .withSelectedTextColor(Color.BLACK)
                .withIcon(R.drawable.black_settings_button);

        final Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        new DividerDrawerItem(),
                        item3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem.equals(item3)){
                            Intent intent = new Intent(MainPage.this, MainActivity.class);
                            startActivity(intent);
                            finish();}
                        if (drawerItem.equals(item2)){
                            Intent intent = new Intent(MainPage.this, ReservationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return false;
                    }
                })
                .withSliderBackgroundColor(Color.DKGRAY)
                .withCloseOnClick(true)
                .build();


    }

    private void showBusinessData(DataSnapshot dataSnapshot) {
    }

    @Override    protected void onResume() {
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainPage.this, LoginActivity.class));
            finish();
        }
        super.onResume();
    }
}
