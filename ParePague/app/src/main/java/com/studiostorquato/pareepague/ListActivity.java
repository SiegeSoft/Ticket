package com.studiostorquato.pareepague;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.studiostorquato.pareepague.adapter.ListAdapter;
import com.studiostorquato.pareepague.model.Ticket;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Ticket> tickets = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        tickets = getTickets();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ListAdapter(tickets, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }


    private ArrayList<Ticket> getTickets(){
        ArrayList<Ticket> tickets = new ArrayList<>();

        // PEGAR DO SQLITE AO INVES DO EXEMPLO ABAIXO
        Ticket ticket = new Ticket("25/02/2017", "123456789", "Rua Galeao Coutinho", "0,00", "98888888", "ABCDE-SP");
        tickets.add(ticket);
        ticket = new Ticket("25/02/2017", "123456789", "Rua Galeao Coutinho", "0,00", "98888888", "ABCDE-SP");
        tickets.add(ticket);
        ticket = new Ticket("25/02/2017", "123456789", "Rua Galeao Coutinho", "0,00", "98888888", "ABCDE-SP");
        tickets.add(ticket);
        ticket = new Ticket("25/02/2017", "123456789", "Rua Galeao Coutinho", "0,00", "98888888", "ABCDE-SP");
        tickets.add(ticket);
        return tickets;
    }

    public void returnToHome(View v) {

        finish();
    }
}