package com.example.android.booksellerwarehousephase2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.booksellerwarehousephase2.data.BookWarehouseContract.WarehouseEntry;

import java.util.Objects;


import static android.content.DialogInterface.*;
import static android.text.TextUtils.isEmpty;
import static java.lang.Integer.*;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Sets initial price and quantity defaults to 0
     */

    int price = 0;
    int quantity = 0;


    /**
     * Displays EditText fields for book title, price, quantity, and supplier info. There had to
     * be some big changes here that took me a while to figure it out. I decided to remove the
     * spinner, since it's likely that there are more than 4 publishers in the world, and
     * I didn't want to be limited.
     */
    private EditText mTitleField;
    private EditText mPriceField;
    private EditText mQuantityField;
    private EditText mSupplierField;
    private EditText mSupplierPhone;
    private Button mOrderButton;
    private Uri mCurrentBookUri;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */

    private boolean mInvHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInvHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Tie the order button to the actual xml resource

        mOrderButton = findViewById(R.id.order);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book list item.

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.add_inventory));
            invalidateOptionsMenu();
            mOrderButton.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.edit_inventory));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTitleField = findViewById(R.id.title);
        mPriceField = findViewById(R.id.price);
        mQuantityField = findViewById(R.id.quantity);
        mSupplierField = findViewById(R.id.supplier);
        mSupplierPhone = findViewById(R.id.supplier_phone);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.

        mTitleField.setOnTouchListener(mTouchListener);
        mSupplierField.setOnTouchListener(mTouchListener);
        mQuantityField.setOnTouchListener(mTouchListener);
        mSupplierField.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);
    }

    /**
     * Get user input from editor and save new book into the warehouse database.
     */

    private int addBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String titleString = mTitleField.getText().toString().trim();
        if (!isEmpty(titleString)) {
            String priceString;
            priceString = mPriceField.getText().toString().trim();
            if (priceString.equals("")) {
                return 0;
            }
            price = parseInt(priceString);
            String quantityString;
            quantityString = mQuantityField.getText().toString().trim();
            if (quantityString.equals("")) {
                return 0;
            }
            quantity = parseInt(quantityString);
            String supplierString;
            supplierString = mSupplierField.getText().toString().trim();
            if (isEmpty(supplierString)) {
                return 0;
            }
            String phoneString;
            phoneString = mSupplierPhone.getText().toString().trim();
            if (isEmpty(phoneString)) {
                return 0;
            }
            if (price < 1) {
                return 2;
            }

            // Create a ContentValues object where column names are the keys,
            // and books attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(WarehouseEntry.COLUMN_BOOK_TITLE, titleString);
            values.put(WarehouseEntry.COLUMN_PRICE, price);
            values.put(WarehouseEntry.COLUMN_QUANTITY, quantity);
            values.put(WarehouseEntry.COLUMN_SUPPLIER, supplierString);
            values.put(WarehouseEntry.COLUMN_SUPPLIER_PHONE, phoneString);

            // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
            if (mCurrentBookUri == null) {
                // This is a NEW book, so insert a new book into the provider,
                // returning the content URI for the new book.
                Uri newUri = getContentResolver().insert(WarehouseEntry.CONTENT_URI, values);
                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null)
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, R.string.unabletosave, Toast.LENGTH_SHORT).show();
                else
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, R.string.itemsaved, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise this is an EXISTING book, so update the pet with content URI: mCurrentBookUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentBookUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0)
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, R.string.failed_update, Toast.LENGTH_SHORT).show();
                    // Otherwise, the update was successful and we can display a toast.
                else Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
            }
            return 1;
        } else {
            return 0;
        }
    }

    //Set up else/if statements to handle notifying the user if they accidentally forget
    //to enter details, such as price or other details.

    public void saveBook() {
        int condition;
        condition = addBook();
        if (condition == 0)
            Toast.makeText(this, R.string.checkparameters, Toast.LENGTH_SHORT).show();
        else {
            if (condition == 2)
                Toast.makeText(this, R.string.flaginvalidprice, Toast.LENGTH_SHORT).show();
            else {
                if (condition != 1) {
                    return;
                }
            }
        }
        finish();
        startActivity(new Intent(this, WarehouseActivity.class));
    }

    //Plus button designed to increment quantity by 1 when pressed.

    public void addButton(View view) {
        String quantityString;
        quantityString = mQuantityField.getText().toString().trim();
        quantity = parseInt(quantityString);
        mQuantityField.setText(String.valueOf(++quantity));
    }

    //Plus button designed to decrement quantity by 1 when pressed.

    public void subtractButton(View view) {
        String quantityString;
        quantityString = mQuantityField.getText().toString().trim();
        quantity = parseInt(quantityString);
        if (quantity == 0)
            Toast.makeText(this, R.string.belowzerotrigger, Toast.LENGTH_SHORT).show();
        else {
            mQuantityField.setText(String.valueOf(--quantity));
        }
    }


    //Establishes the Order button that opens the intent for the user to call the supplier
    //phone number as entered.

    public void order(View view) {
        String phoneField = mSupplierPhone.getText().toString().trim();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!Objects.equals(phoneField, "")) {
                Intent intent;
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.phoneaction) + phoneField));
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_entry);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book to database
                saveBook();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete_entry:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mInvHasChanged && !hasEntry()) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                showUnsavedChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     */
    private void showUnsavedChangesDialog() {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_dialog);
        builder.setPositiveButton(R.string.discard, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
            }
        });
        builder.setNegativeButton(R.string.Editing, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                } else {
                    return;
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method is called when the back button is pressed.
     */

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mInvHasChanged && !hasEntry()) {
            super.onBackPressed();
            return;
        }
        showUnsavedChangesDialog();
    }

    /*
    This block is intended to check if there are any blank spaces.
    The idea is to alert the user that they have missed
    some details, should check the item and begin again. This is quality control
    for the business.
    */
    public boolean hasEntry() {
        boolean hasInput = false;
        if (mCurrentBookUri != null) {
            return hasInput;
        }
        String product = mTitleField.getText().toString().trim();
        String price = mPriceField.getText().toString().trim();
        String quantity = mQuantityField.getText().toString().trim();
        String supplier = mSupplierField.getText().toString().trim();
        String phone = mSupplierPhone.getText().toString().trim();

        // Check to see if the business' variables meet the criterion to trigger the actions
        //described above and execute either the 'true' or 'false' aspects of this boolean that
        // inform the user to check their books and re-enter all fields.

        if (isEmpty(product) && (isEmpty(price) || (valueOf(price) <= 0)) &&
                (isEmpty(quantity) || (valueOf(quantity) <= 0)) &&
                isEmpty(supplier) && isEmpty(phone)) {
            return hasInput;
        } else {
            hasInput = true;
            if (hasInput) return true;
            else return false;
        }
    }

    //    Implementing CursorLoader
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book addition attributes, define a projection that contains
        // all columns from the bookswarehouse table
        String[] projection = {
                WarehouseEntry._ID,
                WarehouseEntry.COLUMN_BOOK_TITLE,
                WarehouseEntry.COLUMN_PRICE,
                WarehouseEntry.COLUMN_QUANTITY,
                WarehouseEntry.COLUMN_SUPPLIER,
                WarehouseEntry.COLUMN_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor.moveToFirst()) {
            // Find the columns of individual book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(WarehouseEntry.COLUMN_BOOK_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(WarehouseEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(WarehouseEntry.COLUMN_QUANTITY);
            int suppNameColumnIndex = cursor.getColumnIndex(WarehouseEntry.COLUMN_SUPPLIER);
            int suppPhoneColumnIndex = cursor.getColumnIndex(WarehouseEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String suppName = cursor.getString(suppNameColumnIndex);
            int suppPhone = cursor.getInt(suppPhoneColumnIndex);

            // Update the views on the screen with the values from the database

            mTitleField.setText(name);
            mPriceField.setText(Integer.toString(price));
            mQuantityField.setText(Integer.toString(quantity));
            mSupplierField.setText(suppName);
            mSupplierPhone.setText(Integer.toString(suppPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    //Delete dialog method for handling deletion of a book.

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_book1);
        builder.setPositiveButton(R.string.delete, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database with toast messages showing if deletion
     * was successful or not
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.

            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0)
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, R.string.deletion_failed, Toast.LENGTH_SHORT).show();
            else
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, R.string.remove_inventory, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
