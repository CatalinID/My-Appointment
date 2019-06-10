package com.msaproject.catal.myappointment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.msaproject.catal.myappointment.models.Business;
import com.msaproject.catal.myappointment.util.RotateBitmap;
import com.msaproject.catal.myappointment.util.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BusinessRegisterActivity extends AppCompatActivity implements SelectPhotoDialog.OnPhotoSelectedListener{

    private static final String TAG = "BusinessRegisterActivity";

    @Override
    public void getImagePath(Uri imagePath) {
        Log.d(TAG, "getImagePath: setting the image to imageview");
        UniversalImageLoader.setImage(imagePath.toString(), mPostImage);
        //assign to global variable
        mSelectedBitmap = null;
        mSelectedUri = imagePath;
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        Log.d(TAG, "getImageBitmap: setting the image to imageview");
        mPostImage.setImageBitmap(bitmap);
        //assign to a global variable
        mSelectedUri = null;
        mSelectedBitmap = bitmap;
    }

    //widgets
    private ImageView mPostImage;
    private EditText mTitle, mDescription, mPrice, mCountry, mStateProvince, mCity, mContactEmail, mPhoneNo;
    private Button mPost;
    private ProgressBar mProgressBar;

    //vars
    private Bitmap mSelectedBitmap;
    private Uri mSelectedUri;
    private byte[] mUploadBytes;
    private double mProgress = 0;
    private boolean twice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_register);

        mPostImage = findViewById(R.id.post_image);
        mTitle = findViewById(R.id.input_bname);
        mDescription = findViewById(R.id.input_description);
        mPrice = findViewById(R.id.input_price);
        mCountry = findViewById(R.id.input_country);
        mStateProvince = findViewById(R.id.input_state_province);
        mCity = findViewById(R.id.input_city);
        mContactEmail = findViewById(R.id.input_email);
        mPhoneNo = findViewById(R.id.input_phoneNo);
        mPost = findViewById(R.id.btn_post);
        mProgressBar = findViewById(R.id.progressBar);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(BusinessRegisterActivity.this));

        init();

    }

    private void init(){

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening dialog to choose new photo");
                SelectPhotoDialog dialog = new SelectPhotoDialog();
                ImageLoader imageLoader = ImageLoader.getInstance();
                dialog.show(getFragmentManager(), getString(R.string.dialog_select_photo));
            }
        });

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to post...");
                if(!isEmpty(mTitle.getText().toString())
                        && !isEmpty(mDescription.getText().toString())
                        && !isEmpty(mPrice.getText().toString())
                        && !isEmpty(mCountry.getText().toString())
                        && !isEmpty(mStateProvince.getText().toString())
                        && !isEmpty(mCity.getText().toString())
                        && !isEmpty(mContactEmail.getText().toString())
                        && !isEmpty(mPhoneNo.getText().toString())){

                    //we have a bitmap and no Uri
                    if(mSelectedBitmap != null && mSelectedUri == null){
                        uploadNewPhoto(mSelectedBitmap);
                    }
                    //we have no bitmap and a uri
                    else if(mSelectedBitmap == null && mSelectedUri != null){
                        uploadNewPhoto(mSelectedUri);
                    }
                }else{
                    Toast.makeText(BusinessRegisterActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadNewPhoto(Bitmap bitmap){
        Log.d(TAG, "uploadNewPhoto: uploading a new image bitmap to storage");
        BackgroundImageResize resize = new BackgroundImageResize(bitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    private void uploadNewPhoto(Uri imagePath){
        Log.d(TAG, "uploadNewPhoto: uploading a new image uri to storage.");
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imagePath);
    }

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]>{

        Bitmap mBitmap;

        public BackgroundImageResize(Bitmap bitmap) {
            if(bitmap != null){
                this.mBitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(BusinessRegisterActivity.this, "compressing image", Toast.LENGTH_SHORT).show();
            showProgressBar();
        }

        @Override
        protected byte[] doInBackground(Uri... params) {
            Log.d(TAG, "doInBackground: started.");

            if(mBitmap == null){
                try{
                    RotateBitmap rotateBitmap = new RotateBitmap();
                    mBitmap =rotateBitmap.HandleSamplingAndRotationBitmap(BusinessRegisterActivity.this, params[0]);
                }catch (IOException e){
                    Log.e(TAG, "doInBackground: IOException: " + e.getMessage());
                }
            }
            byte[] bytes = null;
            Log.d(TAG, "doInBackground: megabytes before compression: " + mBitmap.getByteCount() / 1000000 );
            bytes = getBytesFromBitmap(mBitmap, 100);
            Log.d(TAG, "doInBackground: megabytes before compression: " + bytes.length / 1000000 );
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mUploadBytes = bytes;
            hideProgressBar();
            //execute the upload task
            executeUploadTask();
        }
    }

    private void executeUploadTask(){
        Toast.makeText(BusinessRegisterActivity.this, "uploading image", Toast.LENGTH_SHORT).show();

        final DocumentReference businnessDoc = FirebaseFirestore.getInstance().collection("business").document();

        final String businessId = businnessDoc.getId();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("business/photo/" + businessId + "/business_image");

        UploadTask uploadTask = storageReference.putBytes(mUploadBytes);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(BusinessRegisterActivity.this, "Could not upload photo", Toast.LENGTH_SHORT).show();
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d(TAG, "onSuccess: firebase download path: " + downloadUri.toString());

                    //DocumentReference businnessDoc = FirebaseFirestore.getInstance().collection("business").document();

                    Business newBusiness = new Business();

                    newBusiness.setImage(downloadUri.toString());
                    newBusiness.setCity(mCity.getText().toString());
                    newBusiness.setCountry(mCountry.getText().toString());
                    newBusiness.setState_province(mStateProvince.getText().toString());
                    newBusiness.setDescription(mDescription.getText().toString());
                    newBusiness.setEmail(mContactEmail.getText().toString());
                    newBusiness.setPhoneNo(mPhoneNo.getText().toString());
                    newBusiness.setName(mTitle.getText().toString());
                    newBusiness.setPrice(mPrice.getText().toString());
                    newBusiness.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    newBusiness.setId(businessId);

                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BusinessRegisterActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putString(getString(R.string.msg_token_fmt), token);
                            editor.commit();

                            // Log and toast
                            Log.d(TAG, token);

                        }
                    });

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BusinessRegisterActivity.this);

                    newBusiness.setMessaging_token(preferences.getString(getString(R.string.msg_token_fmt), ""));

                    businnessDoc.set(newBusiness).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(
                                        BusinessRegisterActivity.this,
                                        "BUSINESS HAS BEEN REGISTERED",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(
                                        BusinessRegisterActivity.this,
                                        "BUSINESS REGISTRATION FAILED",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                    });

                    resetFields();

                } else {
                    Toast.makeText(BusinessRegisterActivity.this, "Could not obtain download Url", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream);
        return stream.toByteArray();
    }

    private void resetFields(){
        UniversalImageLoader.setImage("", mPostImage);
        mTitle.setText("");
        mDescription.setText("");
        mPrice.setText("");
        mCountry.setText("");
        mStateProvince.setText("");
        mCity.setText("");
        mContactEmail.setText("");
        mPhoneNo.setText("");
    }

    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgressBar(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Return true if the @param is null
     * @param string
     * @return
     */
    private boolean isEmpty(String string){
        return string.equals("");
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
        Intent intent = new Intent(BusinessRegisterActivity.this,MainPageActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        }, 3000);

        startActivity(intent);
    }

}
