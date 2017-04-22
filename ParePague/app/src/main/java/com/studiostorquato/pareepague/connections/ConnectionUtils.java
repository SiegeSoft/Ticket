package com.studiostorquato.pareepague.connections;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rafaelmagalhaes on 21/04/17.
 */

public class ConnectionUtils extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "com.troyapark.db";
    public static final String TABLE_NAME = "tickets";
    public static final String ID = "_id";
    public static final String TICKET = "cupom";
    public static final String SIGN = "placa";
    public static final String ADDRESS = "endereco";
    public static final String VALUE = "valor";
    public static final String CREDITS = "creditos";
    public static final String TOTAL = "total";
    public static final String DATE = "data";
    public static final int DATABASE_VERSION = 1;

    private final String DATABASE_CREATE_TABLE =
                    "CREATE TABLE "+TABLE_NAME+" ( "
                    + ID + " integer primary key autoincrement, "
                    + TICKET + " text, "
                    + SIGN + " text, "
                    + ADDRESS + " text, "
                    + VALUE + " text, "
                    + CREDITS + " text, "
                    + TOTAL + " text, "
                    + DATE + " text "
                    +")";

    private final String DATABASE_DROP_IF_EXISTS =
                    "DROP TABLE IF EXISTS" + TABLE_NAME;



    public ConnectionUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DROP_IF_EXISTS);
        onCreate(db);
    }


}
