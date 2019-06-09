package com.msaproject.catal.myappointment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msaproject.catal.myappointment.models.Business;
import com.msaproject.catal.myappointment.models.HitsList;
import com.msaproject.catal.myappointment.models.HitsObject;
import com.msaproject.catal.myappointment.util.BusinessListAdapter;
import com.msaproject.catal.myappointment.util.ElasticSearchAPI;
import com.msaproject.catal.myappointment.util.RecyclerViewMargin;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private static final String BASE_URL = "http://104.198.186.72//elasticsearch/bus/_doc/";
    private static final int NUM_GRID_COLUMNS = 3;
    private static final int GRID_ITEM_MARGIN = 5;

    //vars
    private boolean twice;
    private String mElasticSearchPassword;
    private String mPrefCity;
    private String mPrefStateProv;
    private String mPrefCountry;
    private ArrayList<Business> mBusinessList;
    private RecyclerView mRecyclerView;
    private BusinessListAdapter mAdapter;

    //widgets
    private ImageView mFilters;
    private EditText mSearchText;
    private FrameLayout mFrameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mFilters = findViewById(R.id.ic_search);
        mSearchText = findViewById(R.id.input_search);
        mRecyclerView = findViewById(R.id.recyclerView);
        mFrameLayout = findViewById(R.id.container);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(SearchActivity.this));

        getElasticSearchPassword();

        init();
        Log.d(TAG, "init done" );
    }

    private void setupBusinessesList(){
        RecyclerViewMargin itemDecorator = new RecyclerViewMargin(GRID_ITEM_MARGIN, NUM_GRID_COLUMNS);
        mRecyclerView.addItemDecoration(itemDecorator);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, NUM_GRID_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new BusinessListAdapter(SearchActivity.this, mBusinessList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void init(){
        mFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to filters activity.");
                Intent intent = new Intent(SearchActivity.this, FiltersActivity.class);
                startActivity(intent);
            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    mBusinessList = new ArrayList<Business>();

                    Log.d(TAG, "mSearch Text pressed" );

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    ElasticSearchAPI searchAPI = retrofit.create(ElasticSearchAPI.class);

                    HashMap<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Authorization", Credentials.basic("user", mElasticSearchPassword));

                    String searchString = "";

                    if(!mSearchText.equals("")){
                        searchString = searchString + mSearchText.getText().toString() + "*";
                    }
                    if(!mPrefCity.equals("")){
                        searchString = searchString + " city:" + mPrefCity;
                    }
                    if(!mPrefStateProv.equals("")){
                        searchString = searchString + " state_province:" + mPrefStateProv;
                    }
                    if(!mPrefCountry.equals("")){
                        searchString = searchString + " country:" + mPrefCountry;
                    }

                    Call<HitsObject> call = searchAPI.search(headerMap, "AND", searchString);

                    call.enqueue(new Callback<HitsObject>() {
                        @Override
                        public void onResponse(Call<HitsObject> call, Response<HitsObject> response) {

                            HitsList hitsList = new HitsList();
                            String jsonResponse = "";

                            try{
                                Log.d(TAG, "onResponse: server response: " + response.toString());

                                if(response.isSuccessful()){
                                    hitsList = response.body().getHits();
                                }else{
                                    jsonResponse = response.errorBody().string();
                                }

                                Log.d(TAG, "onResponse: hits: " + hitsList);

                                for(int i = 0; i < hitsList.getBusinessIndex().size(); i++){
                                    Log.d(TAG, "onResponse: data: " + hitsList.getBusinessIndex().get(i).getBusiness().toString());
                                    mBusinessList.add(hitsList.getBusinessIndex().get(i).getBusiness());
                                }

                                Log.d(TAG, "onResponse: size: " + mBusinessList.size());
                                //setup the list of businesses

                                setupBusinessesList();

                            }catch (NullPointerException e){
                                Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage() );
                            }
                            catch (IndexOutOfBoundsException e){
                                Log.e(TAG, "onResponse: IndexOutOfBoundsException: " + e.getMessage() );
                            }
                            catch (IOException e){
                                Log.e(TAG, "onResponse: IOException: " + e.getMessage() );
                            }

                        }

                        @Override
                        public void onFailure(Call<HitsObject> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage() );
                            Toast.makeText(SearchActivity.this, "search failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return false;
            }
        });
    }

    public void viewBusiness(String businessId){
        ViewBusinessFragment fragment = new ViewBusinessFragment();
        FragmentTransaction transaction = SearchActivity.this.getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putString(getString(R.string.arg_business_id), businessId);
        fragment.setArguments(args);

        Log.d(TAG,"Fragment args bundle" + args.toString());

        transaction.replace(R.id.container, fragment, getString(R.string.activity_view_business));
        transaction.addToBackStack(getString(R.string.activity_view_business));
        transaction.commit();

        mFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getFilters();
    }

    private void getElasticSearchPassword(){
        Log.d(TAG, "getElasticSearchPassword: retrieving elasticsearch password.");

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.node_elasticsearch))
                .orderByValue();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                mElasticSearchPassword = singleSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFilters(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SearchActivity.this);
        mPrefCity = preferences.getString(getString(R.string.preferences_city), "");
        mPrefStateProv = preferences.getString(getString(R.string.preferences_state_province), "");
        mPrefCountry = preferences.getString(getString(R.string.preferences_country), "");

        Log.d(TAG, "getFilters: got filters: \ncity: " + mPrefCity + "\nState/Prov: " + mPrefStateProv
                + "\nCountry: " + mPrefCountry);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (twice){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }

        twice = true;
        Intent intent = new Intent(SearchActivity.this,MainPageActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        }, 3000);

        startActivity(intent);
    }
}
