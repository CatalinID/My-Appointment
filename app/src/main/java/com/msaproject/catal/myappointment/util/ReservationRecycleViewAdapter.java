package com.msaproject.catal.myappointment.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.msaproject.catal.myappointment.R;
import com.msaproject.catal.myappointment.models.Reservation;

import java.util.ArrayList;

public class ReservationRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Reservation> mReservation;
    private Context mContext;


    // data is passed into the constructor
    public ReservationRecycleViewAdapter(Context context, ArrayList<Reservation> reservations) {
        mContext = context;
        mReservation = reservations;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_reservation_list_item, parent, false);

        holder = new ViewHolder(view);

        return holder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            ((ViewHolder)holder).title.setText(mReservation.get(position).getBusinessName());

            ((ViewHolder)holder).state.setText(mReservation.get(position).getState());

            ((ViewHolder)holder).timestart.setText(mReservation.get(position).getReservationTime());
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mReservation.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, timestart, state;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            timestart = itemView.findViewById(R.id.timestart);
            state = itemView.findViewById(R.id.state);

        }

    }
}