package com.example.android.booksellerwarehousephase2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.android.booksellerwarehousephase2.data.BookWarehouseContract.WarehouseEntry;

/**
 * {@link ContentProvider} for BookSeller Warehouse Second Phase app.
 */

public class WarehouseProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the bookswarehouse table
     */

    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the bookseller table
     */
    private static final int BOOKS_ID = 101;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.

    static {

        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI will map to the
        // integer code {@link #BOOKS}. This URI is used to provide access to MULTIPLE rows
        // of the books table.


        sUriMatcher.addURI(BookWarehouseContract.CONTENT_AUTHORITY, BookWarehouseContract.PATH_BOOKS, BOOKS);

        // The content URI will map to the
        // integer code {@link #BOOK_ID}. This URI is used to provide access to ONE single row
        // of the bookseller table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        //
        sUriMatcher.addURI(BookWarehouseContract.CONTENT_AUTHORITY, BookWarehouseContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    /**
     * Database helper object
     */

    private WarehouseDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new WarehouseDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[]
            projection, @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:

                // For the BOOKS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.

                cursor = database.query(WarehouseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS_ID:
                // For the BOOKS_ID code, extract out the ID from the URI.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = WarehouseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the books table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(WarehouseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertions is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */

    private Uri insertBook(Uri uri, ContentValues values) {


        // Check that the title is valid
        String title = values.getAsString(WarehouseEntry.COLUMN_BOOK_TITLE);
        if (title == null)
            throw new IllegalArgumentException("Book requires a name");

        // Check that the price is valid
        Integer price = values.getAsInteger(WarehouseEntry.COLUMN_PRICE);
        if (price == null || price < 0)
            throw new IllegalArgumentException("No Negative Prices");

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(WarehouseEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 0)
            throw new IllegalArgumentException("No negative books");


        // Get writeable database

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(WarehouseEntry.TABLE_NAME, null, values);
        if (id == -1) {
            // If the ID is -1, then the insertion failed. Log an error and return null.
            Toast.makeText(getContext(), "Failed to insert row for ", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Notify all listeners that the data has changed for the books content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateInv(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = WarehouseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInv(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported for" + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */

    private int updateInv(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(WarehouseEntry.COLUMN_BOOK_TITLE)) {
            String name = values.getAsString(WarehouseEntry.COLUMN_BOOK_TITLE);
            if (name == null)
                throw new IllegalArgumentException("Book requires a name");
        }
        if (values.containsKey(WarehouseEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(WarehouseEntry.COLUMN_PRICE);

            /*
            As a developer, I've found that I think better in terms of confirmed negatives.
            This is a perfect example. If the price is NOT null and is greater or equal to 0,
            do nothing. Otherwise, the answer MUST be negative, so throw an exception.
            */
            if (price != null && price >= 0) {
            } else {
                throw new IllegalArgumentException("No Negative Prices");
            }
        }
        if (values.containsKey(WarehouseEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(WarehouseEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity >= 0) {
            } else {
                throw new IllegalArgumentException("No negative books");
            }
        }

        /*
        I follow the same logic here. If the values are not equal to 0, meaning they were updated
        then we definitely need to obtain the updated data, otherwise we don't need to do anything.
        */
        if (values.size() != 0) {
            // So if that's the case, we should get writeable a database to update the data
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            // Perform the update on the database and get the number of rows affected
            int rowsUpdated = database.update(WarehouseEntry.TABLE_NAME, values, selection, selectionArgs);

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows updated
            return rowsUpdated;
        } else {
            //As stated earlier, otherwise, do nothing, and retrieve no updated data.
            return 0;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(WarehouseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                // Delete a single row given by the ID in the URI
                selection = WarehouseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(WarehouseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for" + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return WarehouseEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return WarehouseEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
