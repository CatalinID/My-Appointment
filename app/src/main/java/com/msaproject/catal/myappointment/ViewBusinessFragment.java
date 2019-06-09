package com.msaproject.catal.myappointment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.msaproject.catal.myappointment.models.Business;
import com.msaproject.catal.myappointment.util.UniversalImageLoader;


public class ViewBusinessFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";

    //widgets
    private TextView mEmailBusiness, mCallBusiness, mTitle, mDescription, mPrice, mLocation, mSaveBusiness, mMakeAppointment;
    private ImageView mClose, mWatchList, mBusinessImage;

    //vars
    private String mBusinessId;
    private Business mBusiness;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBusinessId = (String) getArguments().get(getString(R.string.arg_business_id));
        Log.d(TAG, "onCreate: got the post id: " + mBusinessId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_view_business, container, false);

        mEmailBusiness = view.findViewById(R.id.business_contact);
        mCallBusiness = view.findViewById(R.id.business_phone);
        mTitle = view.findViewById(R.id.business_title);
        mDescription = view.findViewById(R.id.business_description);
        mPrice = view.findViewById(R.id.business_price);
        mLocation = view.findViewById(R.id.business_location);
        mClose = view.findViewById(R.id.business_close);
        mWatchList = view.findViewById(R.id.add_watch_list);
        mBusinessImage = view.findViewById(R.id.business_image);
        mSaveBusiness = view.findViewById(R.id.save_business);
        mMakeAppointment = view.findViewById(R.id.appointment_button);

        init();

        hideSoftKeyboard();

        return view;
    }

    private void init(){
        getBusinessInfo();

        mEmailBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {mBusiness.getEmail()});
                getActivity().startActivity(emailIntent);
            }
        });

        mCallBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mBusiness.getPhoneNo()));
                getActivity().startActivity(callIntent);
            }
        });

        mMakeAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent apptIntent= new Intent(getActivity(), ReservationActivity.class);
                Bundle args = new Bundle();
                args.putString(getString(R.string.arg_business_id), mBusinessId);
                args.putString(getString(R.string.arg_business_name), mTitle.getText().toString());
                apptIntent.putExtras(args);
                getActivity().startActivity(apptIntent);
            }
        });



        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing business.");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        //mSavePost.setShadowLayer(5, 0 , 0, Color.BLUE);
        //mWatchList.setImageBitmap(createOutline(BitmapFactory.decodeResource(getResources(), R.drawable.ic_save_white)));
        //mWatchList.setColorFilter(Color.BLUE);

        mClose.setImageResource(R.drawable.ic_x_white);
        mClose.setColorFilter(Color.WHITE);
    }

    private void getBusinessInfo(){
        Log.d(TAG, "getPostInfo: getting the business information.");

        DocumentReference reference = FirebaseFirestore.getInstance().collection("business").document(mBusinessId);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        mBusiness = document.toObject(Business.class);

                        mTitle.setText(mBusiness.getName());
                        mDescription.setText(mBusiness.getDescription());

                        String price = "FREE";
                        if(mBusiness.getPrice() != null){
                            price = "$" + mBusiness.getPrice();
                        }

                        String location = mBusiness.getCountry() +" , "+ mBusiness.getState_province() +" , "+ mBusiness.getCity();

                        mPrice.setText(price);
                        mLocation.setText(location);
                        UniversalImageLoader.setImage(mBusiness.getImage(), mBusinessImage);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void hideSoftKeyboard(){
        final Activity activity = getActivity();
        final InputMethodManager inputManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}