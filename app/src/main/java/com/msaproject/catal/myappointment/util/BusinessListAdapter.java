package com.msaproject.catal.myappointment.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.msaproject.catal.myappointment.R;
import com.msaproject.catal.myappointment.SearchActivity;
import com.msaproject.catal.myappointment.models.Business;

import java.util.ArrayList;

public class BusinessListAdapter extends RecyclerView.Adapter<BusinessListAdapter.ViewHolder>{

    private static final String TAG = "BusinessListAdapter";
    private static final int NUM_GRID_COLUMNS = 3;

    private ArrayList<Business> mBusiness;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{

        SquareImageView mBusinessImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mBusinessImage = itemView.findViewById(R.id.post_image);

            int gridWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth/NUM_GRID_COLUMNS;
            mBusinessImage.setMaxHeight(imageWidth);
            mBusinessImage.setMaxWidth(imageWidth);
        }
    }

    public BusinessListAdapter(Context context, ArrayList<Business> businesses) {
        mBusiness = businesses;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UniversalImageLoader.setImage(mBusiness.get(position).getImage(), holder.mBusinessImage);

        final int pos = position;
        holder.mBusinessImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: selected a business with id:" + mBusiness.get(pos).getId());
                //TODO
                ((SearchActivity)mContext).viewBusiness(mBusiness.get(pos).getId());

            }

        });
    }

    @Override
    public int getItemCount() {
        return mBusiness.size();
    }


}
