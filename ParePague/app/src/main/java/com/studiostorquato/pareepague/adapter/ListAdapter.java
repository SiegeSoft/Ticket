package com.studiostorquato.pareepague.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studiostorquato.pareepague.R;
import com.studiostorquato.pareepague.model.Ticket;

import java.util.ArrayList;

/**
 * Created by rafaelmagalhaes on 21/04/17.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    ArrayList<Ticket> tickets;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView ticket, date, value, sign, address, phone;

        public ViewHolder(View v) {
            super(v);
            ticket = (TextView) v.findViewById(R.id.ticket);
            date = (TextView) v.findViewById(R.id.date);
            value = (TextView) v.findViewById(R.id.value);
            sign = (TextView) v.findViewById(R.id.sign);
            address = (TextView) v.findViewById(R.id.address);
            phone = (TextView) v.findViewById(R.id.phone);
        }
    }


    public ListAdapter(ArrayList<Ticket> tickets, Activity context) {
        this.tickets = tickets;
        this.context = context;

    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.print_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.date.setText(ticket.getDate());
        holder.ticket.setText(ticket.getTicket());
        holder.sign.setText(ticket.getSign());
        holder.address.setText(ticket.getAddress());
        holder.phone.setText(ticket.getPhone());
        //holder.value.setText(ticket.getValue());
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }


}
