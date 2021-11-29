package com.android.odev.service;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.odev.model.CustomContact;

import java.util.ArrayList;

public class ContactDatabaseService extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "contacts_db";

    public static final String TABLE_NAME = "contacts";

    public static final String CREATE_TABLE = "CREATE TABLE contacts(phone TEXT,name TEXT, groupName TEXT)";

    public ContactDatabaseService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void deleteAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public void addContact(String phone,String name, String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("phone", phone.trim());
        cv.put("name", name.trim());
        cv.put("groupName", groupName.trim());

        long result = db.insert(TABLE_NAME, null, cv);

        if (result > -1)
            Log.i("DatabaseHelper", "Contact başarıyla kaydedildi");
        else
            Log.i("DatabaseHelper", "Contact kaydedilemedi");

        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<CustomContact> getContactList() {
        ArrayList<CustomContact> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                "phone",
                "name", "groupName"};

        Cursor c = db.query(TABLE_NAME, projection, null, null, null, null, null);
        while (c.moveToNext()) {
            data.add(new CustomContact(c.getString(c.getColumnIndex("phone")), c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("groupName"))));
        }

        c.close();
        db.close();

        return data;
    }
}
