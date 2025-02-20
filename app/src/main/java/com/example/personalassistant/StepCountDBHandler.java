package com.example.personalassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StepCountDBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "PERSONAL_ASSISTANT";
    private static final String TABLE_NAME = "STEP_COUNT";
    private static final int DB_VERSION = 1;

    // Column names
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_DATE = "STEP_DATE";
    private static final String COLUMN_COUNT = "STEP_COUNT";

    public StepCountDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table query
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT UNIQUE, " + // Using TEXT for date storage
                COLUMN_COUNT + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Recreate the table
        onCreate(db);
    }
    // Method to insert step count data
    public void insertStepCount(String date, int stepCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT OR REPLACE INTO " + TABLE_NAME + " ("
                + COLUMN_DATE + ", "
                + COLUMN_COUNT + ") VALUES ('"
                + date + "', "
                + stepCount + ");";
        db.execSQL(query);
        db.close();
    }
    public void updateStepCount(String date, int stepCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET "
                + COLUMN_COUNT + " = " + stepCount
                + " WHERE " + COLUMN_DATE + " = '" + date + "';";
        db.execSQL(query);
        db.close();
    }

    // Insert or Update Step Count for a Date
    public void insertOrUpdateStepCount(String date, int stepCount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("STEP_DATE", date);
        values.put("STEP_COUNT", stepCount);

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
    }


    // Get Step Count for a Date
    public int getStepCount(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT STEP_COUNT FROM " + TABLE_NAME + " WHERE STEP_DATE = '" + date + "';";
        Cursor cursor = db.rawQuery(query, null);
        int stepCount = 0;
        if (cursor.moveToFirst()) {
            stepCount = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return stepCount;
    }

    public List<StepCount> getAllSteps() {
        List<StepCount> stepList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY "+COLUMN_DATE, null);

        if (cursor.moveToFirst()) {
            do {
                StepCount step = new StepCount(
                        cursor.getString(0),  // Date
                        cursor.getInt(1)      // Steps
                );
                Log.d("DB_DATA", "Date: " + step.getDate() + ", Steps: " + step.getSteps());
                stepList.add(step);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return stepList;
    }

}
