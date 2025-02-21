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

    // ✅ Insert or Update Step Count for a Date
    public void insertOrUpdateStepCount(String date, int stepCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, date.trim()); // Ensure correct format
        values.put(COLUMN_COUNT, stepCount);

        long result = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        if (result == -1) {
            Log.e("DB_ERROR", "Failed to insert/update step count");
        } else {
            Log.d("DB_SUCCESS", "Inserted: " + date + " - " + stepCount);
        }

        db.close();
    }

    // ✅ Get Step Count for a Specific Date
    public int getStepCount(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_COUNT + " FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " = ?", new String[]{date});

        int stepCount = 0;
        if (cursor.moveToFirst()) {
            stepCount = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return stepCount;
    }

    // ✅ Get All Step Data (Corrected Indexing)
    public List<StepCount> getAllSteps() {
        List<StepCount> stepList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE, null);

        if (cursor.moveToFirst()) {
            do {
                // ✅ Correct column indexes using getColumnIndex()
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                int steps = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT));

                StepCount step = new StepCount(date, steps);
                Log.d("DB_DATA", "Date: " + date + ", Steps: " + steps);
                stepList.add(step);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return stepList;
    }
}
