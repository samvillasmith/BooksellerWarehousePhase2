package com.example.android.booksellerwarehousephase2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The "Content Authority" is a name for the entire content provider, similar to the
 * relationship between a domain name and its website.  A convenient string to use for the
 * content authority is the package name for the app, which is guaranteed to be unique on the
 * device.
 * <p>
 * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
 * the content provider.
 * <p>
 * Finally, declare the Possible path (appended to base content URI for possible URI's)
 */


public final class BookWarehouseContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.booksellerwarehousephase2";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */

    public static final String PATH_BOOKS = "warehouse";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.

    private BookWarehouseContract() {
    }

    /**
     * Inner class that defines constant values for the books warehouse database table.
     * Each entry in the table represents a book.
     */

    public static final class WarehouseEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The content URI to access the book data in the provider
         */

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);


        /**
         * The columns for identifiers for the table and all columns for the book. These are
         * largely identical to the first phase of the app, but I removed the COLUMN_AUTHOR.
         */


        public final static String TABLE_NAME = "bookstore";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_TITLE = "book_title";
        public final static String COLUMN_PRICE = "book_price";
        public final static String COLUMN_QUANTITY = "book_quantity";
        public final static String COLUMN_SUPPLIER = "supplier";
        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";
    }
}
