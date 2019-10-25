package au.edu.federation.capstoneprototype;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.capstoneprototype.Base.ClassOffline;


public class OffineDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "studentClassesOffline";
    private static final String TABLE_CLASSES = "classes_offline";
    private static final String KEY_ID = "id";
    private static final String KEY_CLASS = "class";
    private static final String KEY_STUDENT = "class";
    private static final String KEY_MANUAL = "finish";
    private static final String KEY_PRESENT = "present";

    public OffineDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_OFFLINE_TABLE = "CREATE TABLE "
                + TABLE_CLASSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CLASS + " TEXT,"
                + KEY_STUDENT + " TEXT,"
                + KEY_MANUAL + " TEXT,"
                + KEY_PRESENT + " TEXT"
                + ")";
        db.execSQL(CREATE_OFFLINE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);

        // Create tables again
        onCreate(db);
    }

    // code to add the new class
    void addClass(ClassOffline class_event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // TODO add to DB
        values.put(KEY_ID, class_event.getId());
        values.put(KEY_CLASS, class_event.getClass_id());
        values.put(KEY_STUDENT, class_event.getStudent_id());
        values.put(KEY_PRESENT, class_event.getPresent());
        values.put(KEY_MANUAL, class_event.getManual());

        // Inserting Row
        db.insert(TABLE_CLASSES, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single
    ClassOffline getClassOffline(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, new String[]{KEY_ID, KEY_CLASS, KEY_STUDENT , KEY_PRESENT, KEY_MANUAL}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        // return contact
        return new ClassOffline(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
    }

    // code to get all contacts in a list view
    public List<ClassOffline> getAll() {
        List<ClassOffline> classList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CLASSES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ClassOffline class_event = new ClassOffline();
                class_event.setId(cursor.getInt(0));
                class_event.setClass_id(cursor.getString(1));
                class_event.setStudent_id(cursor.getString(2));
                class_event.setPresent(cursor.getString(3));
                class_event.setManual(cursor.getString(4));
                // Adding contact to list
                classList.add(class_event);
            } while (cursor.moveToNext());
        }

        // return contact list
        return classList;
    }

    // Deleting single contact
    public void deleteClass(ClassOffline class_event) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASSES, KEY_ID + " = ?", new String[]{String.valueOf(class_event.getId())});
        db.close();
    }
}