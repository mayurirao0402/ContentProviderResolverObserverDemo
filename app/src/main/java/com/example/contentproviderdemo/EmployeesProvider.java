package com.example.contentproviderdemo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import java.util.HashMap;



public class EmployeesProvider extends ContentProvider {

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "EmpDB";
    static final String TABLE_NAME = "Employees";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME
            + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + " name TEXT NOT NULL);";

    static final String PROVIDER_NAME = "com.example.contentproviderdemo.EmployeesProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/employees";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String id = "id";
    static final String name = "name";
    static final int uriCode = 1;
    static final UriMatcher uriMatcher;
    private static HashMap<String, String> values;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "employees", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "employees/*", uriCode);
    }

    @Override
    public String getType (Uri uri){
        if (uriMatcher.match(uri) == uriCode) {
            return "vnd.android.cursor.dir/employees";
        }
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Override
    public boolean onCreate () {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query (Uri uri, String[]projection, String selection, String[]selectionArgs, String sortOrder){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        if (uriMatcher.match(uri) == uriCode) {
            qb.setProjectionMap(values);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder.equals("")) {
            sortOrder = id;
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert (Uri uri, ContentValues values){
        long rowID = db.insert(TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLiteException("Failed to add a record into " + uri);
    }

    @Override
    public int update (Uri uri, ContentValues values, String selection,
            String[]selectionArgs){
        int count = 0;
        if (uriMatcher.match(uri) == uriCode) {
            count = db.update(TABLE_NAME, values, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete (Uri uri, String selection, String[]selectionArgs){
        int count = 0;
        if (uriMatcher.match(uri) == uriCode) {
            count = db.delete(TABLE_NAME, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}

