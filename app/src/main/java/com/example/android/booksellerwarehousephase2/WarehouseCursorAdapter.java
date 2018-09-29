package com.example.android.booksellerwarehousephase2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.booksellerwarehousephase2.data.BookWarehouseContract;

/**
 * Constructs a new {@link WarehouseCursorAdapter}.
 */

public class WarehouseCursorAdapter extends CursorAdapter {

    public WarehouseCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView titleTextView = view.findViewById(R.id.title);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity_count);
        Button orderButtonView = view.findViewById(R.id.order_button1);

        // Find the columns of book attributes that we're interested in

        int nameColumnIndex = cursor.getColumnIndex(BookWarehouseContract.WarehouseEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BookWarehouseContract.WarehouseEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookWarehouseContract.WarehouseEntry.COLUMN_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(BookWarehouseContract.WarehouseEntry._ID);

        // Read the book attributes from the Cursor for the current book

        final String currentName = cursor.getString(nameColumnIndex);
        int Price = cursor.getInt(priceColumnIndex);
        final int currentQuantity = cursor.getInt(quantityColumnIndex);
        final int currentID = cursor.getInt(idColumnIndex);

        //Check to see if the quantity is not 0. If it's not 0, then display how much stock is left.


        if (currentQuantity != 0) {
            orderButtonView.setEnabled(true);
            quantityTextView.setText(context.getString(R.string.Stock) + String.valueOf(currentQuantity));

            // Otherwise, display a "Sold Out" message in place of that quantity.
        } else {
            orderButtonView.setEnabled(false);
            quantityTextView.setText(R.string.outofstock);
        }

        // Update the TextViews with the attributes for the current book.

        titleTextView.setText(currentName);
        priceTextView.setText(context.getString(R.string.currency_value) + String.valueOf(Price));

        // Set up an OnClickListener that reacts to shifts in quantity.

        orderButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int bookInv = currentQuantity - 1;
                if (bookInv >= 0) {
                    ContentValues values;
                    values = new ContentValues();
                    values.put(BookWarehouseContract.WarehouseEntry.COLUMN_QUANTITY, bookInv);
                    Uri invItem;
                    invItem = ContentUris.withAppendedId(BookWarehouseContract.WarehouseEntry.CONTENT_URI, currentID);
                    context.getContentResolver().update(invItem, values, null, null);
                } else {
                    Toast.makeText(context, context.getString(R.string.Stock), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
