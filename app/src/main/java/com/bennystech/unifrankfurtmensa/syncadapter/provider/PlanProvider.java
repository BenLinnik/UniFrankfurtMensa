/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bennystech.unifrankfurtmensa.syncadapter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.bennystech.unifrankfurtmensa.db.SelectionBuilder;

public class PlanProvider extends ContentProvider {
    MensaPlanDatabase mDatabaseHelper;

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = MensaPlanContract.CONTENT_AUTHORITY;

    // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
    // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these
    // IDs.
    //
    // When a incoming URI is run through sUriMatcher, it will be tested against the defined
    // URI patterns, and the corresponding route ID will be returned.
    /**
     * URI ID for route: /essen
     */
    public static final int ROUTE_ESSEN = 1;

    /**
     * URI ID for route: /essen/{ID}
     */
    public static final int ROUTE_ESSEN_ID = 2;

    /**
     * URI ID for route: /mensen
     */
    public static final int ROUTE_MENSA= 3;

    /**
     * URI ID for route: /mensen/{ID}
     */
    public static final int ROUTE_MENSA_ID = 4;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "essen", ROUTE_ESSEN);
        sUriMatcher.addURI(AUTHORITY, "essen/*", ROUTE_ESSEN_ID);
        sUriMatcher.addURI(AUTHORITY, "mensen", ROUTE_MENSA);
        sUriMatcher.addURI(AUTHORITY, "mensen/*", ROUTE_MENSA_ID);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MensaPlanDatabase(getContext());
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ESSEN:
                return MensaPlanContract.EssenTable.CONTENT_TYPE;
            case ROUTE_ESSEN_ID:
                return MensaPlanContract.EssenTable.CONTENT_ITEM_TYPE;
            case ROUTE_MENSA:
                return MensaPlanContract.EssenTable.CONTENT_TYPE;
            case ROUTE_MENSA_ID:
                return MensaPlanContract.EssenTable.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     *
     * <p>Currently supports returning all entries (/entries) and individual entries by ID
     * (/entries/{ID}).
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        Cursor c;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_ESSEN_ID:
                // Return a single entry, by ID.
                String id = uri.getLastPathSegment();
                builder.where(MensaPlanContract.EssenTable._ID + "=?", id);
            case ROUTE_ESSEN:
                // Return all known entries.
                builder.table(MensaPlanContract.EssenTable.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            case ROUTE_MENSA_ID:
                // Return a single entry, by ID.
                String id2 = uri.getLastPathSegment();
                builder.where(MensaPlanContract.MensaTable._ID + "=?", id2);
            case ROUTE_MENSA:
                // Return all known entries.
                builder.table(MensaPlanContract.MensaTable.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx2 = getContext();
                assert ctx2 != null;
                c.setNotificationUri(ctx2.getContentResolver(), uri);
                return c;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case ROUTE_ESSEN:
                long id = db.insertOrThrow(MensaPlanContract.EssenTable.TABLE_NAME, null, values);
                result = Uri.parse(MensaPlanContract.EssenTable.CONTENT_URI + "/" + id);
                break;
            case ROUTE_ESSEN_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            case ROUTE_MENSA:
                long id2 = db.insertOrThrow(MensaPlanContract.MensaTable.TABLE_NAME, null, values);
                result = Uri.parse(MensaPlanContract.MensaTable.CONTENT_URI + "/" + id2);
                break;
            case ROUTE_MENSA_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ESSEN:
                count = builder.table(MensaPlanContract.EssenTable.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ESSEN_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(MensaPlanContract.EssenTable.TABLE_NAME)
                        .where(MensaPlanContract.EssenTable._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_MENSA:
                count = builder.table(MensaPlanContract.MensaTable.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_MENSA_ID:
                String id2 = uri.getLastPathSegment();
                count = builder.table(MensaPlanContract.MensaTable.TABLE_NAME)
                        .where(MensaPlanContract.MensaTable._ID + "=?", id2)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * Update an etry in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ESSEN:
                count = builder.table(MensaPlanContract.EssenTable.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ESSEN_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(MensaPlanContract.EssenTable.TABLE_NAME)
                        .where(MensaPlanContract.EssenTable._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_MENSA:
                count = builder.table(MensaPlanContract.MensaTable.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_MENSA_ID:
                String id2 = uri.getLastPathSegment();
                count = builder.table(MensaPlanContract.MensaTable.TABLE_NAME)
                        .where(MensaPlanContract.MensaTable._ID + "=?", id2)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * SQLite backend
     *
     * Provides access to an disk-backed, SQLite datastore which is utilized by FeedProvider. This
     * database should never be accessed by other parts of the application directly.
     */
    static class MensaPlanDatabase extends SQLiteOpenHelper {
        /** Schema version. */
        public static final int DATABASE_VERSION = 3;
        /** Filename for SQLite file. */
        public static final String DATABASE_NAME = "mensaplan.db";

        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String FLOAT_TYPE = " REAL";

        private static final String SQL_CREATE_ENTRIES_TABLE_ESSEN =
                "CREATE TABLE " + MensaPlanContract.EssenTable.TABLE_NAME + " (" +
                        MensaPlanContract.EssenTable._ID + INTEGER_TYPE + " PRIMARY KEY, " +
                        MensaPlanContract.EssenTable.COLUMN_NAME_MENSA_ID + INTEGER_TYPE + ", " +
                        MensaPlanContract.EssenTable.COLUMN_NAME_DAY + INTEGER_TYPE + ", " +
                        MensaPlanContract.EssenTable.COLUMN_NAME_NAME + TEXT_TYPE + ", " +
                        MensaPlanContract.EssenTable.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + ", " +
                        MensaPlanContract.EssenTable.COLUMN_NAME_PRICE + FLOAT_TYPE + ", " +
                        MensaPlanContract.EssenTable.COLUMN_NAME_BEILAGEN + INTEGER_TYPE +
                        " )";

        private static final String SQL_CREATE_ENTRIES_TABLE_MENSA =
                "CREATE TABLE " + MensaPlanContract.MensaTable.TABLE_NAME + " (" +
                        MensaPlanContract.MensaTable._ID + INTEGER_TYPE + " PRIMARY KEY, " +
                        MensaPlanContract.MensaTable.COLUMN_NAME_NAME + TEXT_TYPE + "," +
                        MensaPlanContract.MensaTable.COLUMN_NAME_URL + TEXT_TYPE + "," +
                        MensaPlanContract.MensaTable.COLUMN_NAME_PLACE + TEXT_TYPE +
                        " )";

        /** SQL statement to drop "entry" table. */
        private static final String SQL_DELETE_TABLE_ESSEN =
                "DROP TABLE IF EXISTS " + MensaPlanContract.EssenTable.TABLE_NAME;

        private static final String SQL_DELETE_TABLE_MENSA =
                "DROP TABLE IF EXISTS " + MensaPlanContract.MensaTable.TABLE_NAME;

        public MensaPlanDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES_TABLE_ESSEN);
            db.execSQL(SQL_CREATE_ENTRIES_TABLE_MENSA);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_TABLE_ESSEN);
            db.execSQL(SQL_DELETE_TABLE_MENSA);
            onCreate(db);
        }
        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }
}
