package com.example.android.booksellerwarehousephase2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.booksellerwarehousephase2.data.BookWarehouseContract.WarehouseEntry;

/**
 * Displays list of books that were entered and stored in the app.
 */

public class WarehouseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int WAREHOUSE_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    WarehouseCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backroom);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WarehouseActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the books data
        ListView WarehouseListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        WarehouseListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.

        mCursorAdapter = new WarehouseCursorAdapter(this, null);
        WarehouseListView.setAdapter(mCursorAdapter);


        // Setup the item click listener

        WarehouseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(WarehouseActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific book list item that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link WarehouseEntry#CONTENT_URI}.

                Uri currentInvURi = ContentUris.withAppendedId(WarehouseEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentInvURi);
                // Launch the {@link EditorActivity} to display the data for the current book item.
                startActivity(intent);
            }
        });

        // Kick off the loader

        getLoaderManager().initLoader(WAREHOUSE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_warehouse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllInventory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllInventory() {
        getContentResolver().delete(WarehouseEntry.CONTENT_URI, null, null);
    }

    //    Implementing CursorLoader
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                WarehouseEntry._ID,
                WarehouseEntry.COLUMN_BOOK_TITLE,
                WarehouseEntry.COLUMN_PRICE,
                WarehouseEntry.COLUMN_QUANTITY
        };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                WarehouseEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link BookCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
