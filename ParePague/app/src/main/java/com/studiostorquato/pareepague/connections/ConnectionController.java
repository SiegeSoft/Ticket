package com.studiostorquato.pareepague.connections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.studiostorquato.pareepague.model.Ticket;
import java.util.ArrayList;

/**
 * Created by rafaelmagalhaes on 21/04/17.
 */

public class ConnectionController {

    private SQLiteDatabase db;
    private ConnectionUtils connectionUtils;
    private static final String LOGCAT_TAG = "Connection_TroyaPark";

    private final String[] allColumns = {
            connectionUtils.ID,
            connectionUtils.TICKET,
            connectionUtils.SIGN,
            connectionUtils.ADDRESS,
            connectionUtils.VALUE,
            connectionUtils.CREDITS,
            connectionUtils.DATE,
            connectionUtils.TOTAL
    };


    public ConnectionController(Context context) {
        connectionUtils = new ConnectionUtils(context);
    }

    private void openConnection(){
        Log.i(LOGCAT_TAG,"Database Opened");
        db = connectionUtils.getWritableDatabase();

    }

    private void openReadeable(){
        Log.i(LOGCAT_TAG,"Database Opened Readeable");
        db = connectionUtils.getReadableDatabase();

    }
    private void closeConnection(){
        Log.i(LOGCAT_TAG, "Database Closed");
        db.close();
    }


    public boolean createTicket(Ticket ticket) {
        ContentValues values = new ContentValues();
        long response;

        openConnection();

        values.put(connectionUtils.TICKET, ticket.getTicket());
        values.put(connectionUtils.SIGN, ticket.getSign());
        values.put(connectionUtils.ADDRESS, ticket.getAddress());
        values.put(connectionUtils.VALUE, ticket.getValue());
        values.put(connectionUtils.CREDITS, ticket.getCredits());
        values.put(connectionUtils.DATE, ticket.getDate());
        values.put(connectionUtils.TOTAL, ticket.getTotal());

        response = db.insert(connectionUtils.TABLE_NAME, null, values);

        closeConnection();

        if (response == -1){
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Ticket> readAllTickets(){
        ArrayList<Ticket> tickets = new ArrayList<>();
        Ticket ticket = null;
        Cursor cursor;
        openReadeable();

        cursor = db.query(connectionUtils.TABLE_NAME,allColumns,null,null,null, null, null);

        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                ticket = new Ticket(
                        cursor.getString(cursor.getColumnIndex(connectionUtils.DATE)),
                        cursor.getString(cursor.getColumnIndex(connectionUtils.TICKET)),
                        cursor.getString(cursor.getColumnIndex(connectionUtils.ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(connectionUtils.VALUE)),
                        cursor.getString(cursor.getColumnIndex(connectionUtils.CREDITS)),
                        cursor.getString(cursor.getColumnIndex(connectionUtils.SIGN)),
                        cursor.getString(cursor.getColumnIndex(connectionUtils.TOTAL))
                );
                tickets.add(ticket);
            }
        }

        closeConnection();

        return tickets;
    }

}
