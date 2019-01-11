package com.msaproject.catal.myappointment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.*;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;


public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userEmail = user.getEmail();
        String userName = user.getDisplayName();

        TextView textView = findViewById(R.id.textUserName);
        if(userName != null) textView.setText("Welcome " + userName +"!");
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
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName("My Appointments")
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
                        return false;
                    }
                })
                .withSliderBackgroundColor(Color.DKGRAY)
                .withCloseOnClick(true)
                .build();

    }

    @Override    protected void onResume() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainPage.this, MainActivity.class));
            finish();
        }
        super.onResume();
    }
}
