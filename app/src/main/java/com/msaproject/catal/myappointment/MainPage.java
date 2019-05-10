package com.msaproject.catal.myappointment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.msaproject.catal.myappointment.models.Reservation;
import com.msaproject.catal.myappointment.models.User;

import java.util.ArrayList;


public class MainPage extends AppCompatActivity {

    FirebaseAuth auth;

    private RecyclerView upcomingView;
    private View parentLayout;
    private ArrayList<Reservation> upcomingResList = new ArrayList<>();
    private ReservationRecycleViewAdapter upcomingResAdapter;
    private DocumentSnapshot lastReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        User loggedUser = new User(auth);

        String userName = loggedUser.getUserName();
        String userEmail = loggedUser.getUserEmail();

        //recycleview
        upcomingView = findViewById(R.id.recycle_view);
        initRecyclerView();
        getUserReservations();

        TextView textView = findViewById(R.id.textUserName);
        if(userName != null) textView.setText("Welcome, " + userName +"!");
        else textView.setText("Welcome!");


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


    @Override    protected void onResume() {
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainPage.this, LoginActivity.class));
            finish();
        }
        super.onResume();
    }

    private void initRecyclerView(){
        if(upcomingResAdapter == null){
            upcomingResAdapter = new ReservationRecycleViewAdapter(this, upcomingResList);
        }
        upcomingView.setLayoutManager(new LinearLayoutManager(this));
        upcomingView.setAdapter(upcomingResAdapter);
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
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis()/1000,0);
                        if(timestamp.compareTo(reservation.getReservationBegin()) < 1 )
                            upcomingResList.add(reservation);
                    }

                    if(task.getResult().size() != 0){
                        lastReservation = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    }
                    upcomingResAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(
                            MainPage.this,
                            "Getting your reservations FAILED!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
