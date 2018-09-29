package com.example.android.booksellerwarehousephase2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.booksellerwarehousephase2.data.BookWarehouseContract.WarehouseEntry;


/**
 * Database helper for Books app. Manages database creation and version management.
 */

public class WarehouseDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file. I made this different from the first version because I made
     * some structural changes to the columns. The new increment remains at 1 of the database
     * vesion since this is a new database and table.
     */

    private static final String DATABASE_NAME = "bookstore.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link WarehouseDbHelper}.
     *
     * @param context of the app
     */

    public WarehouseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the books table
        // I actually decided to remove the author's name because I felt it was irrelevant
        // to the business based scenario that envisions an app being created
        // for a supplier mainly concerned with item names, quantity, and price alone.

        String SQL_CREATE_BOOKSTORE = "CREATE TABLE " + WarehouseEntry.TABLE_NAME + " ( "
                + WarehouseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WarehouseEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL, "
                + WarehouseEntry.COLUMN_PRICE + " INTEGER, "
                + WarehouseEntry.COLUMN_QUANTITY + " INTEGER DEFAULT 0, "
                + WarehouseEntry.COLUMN_SUPPLIER + " TEXT, "
                + WarehouseEntry.COLUMN_SUPPLIER_PHONE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKSTORE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The NEW database is still at version 1, so there's nothing to do be done here.
    }
}
