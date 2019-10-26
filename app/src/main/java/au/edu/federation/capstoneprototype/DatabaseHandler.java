package au.edu.federation.capstoneprototype;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import au.edu.federation.capstoneprototype.Base.Class;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "studentClasses";
    private static final String TABLE_CLASSES = "classes";
    private static final String KEY_ID = "id";
    private static final String KEY_CODE = "code";
    private static final String KEY_NAME = "name";
    private static final String KEY_TEACHER_ID = "teacher_id";
    private static final String KEY_TEACHER_NAME = "teacher_name";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_MAC = "mac";
    private static final String KEY_DATE = "date";
    private static final String KEY_START = "start";
    private static final String KEY_FINISH = "finish";
    private static final String KEY_PRESENT = "present";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CLASSES_TABLE = "CREATE TABLE "
                + TABLE_CLASSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CODE + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_TEACHER_ID + " INTEGER,"
                + KEY_TEACHER_NAME + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_MAC + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_START + " TEXT,"
                + KEY_FINISH + " TEXT,"
                + KEY_PRESENT + " TEXT"
                + ")";
        db.execSQL(CREATE_CLASSES_TABLE);
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
    void addClass(Class class_event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // TODO add to DB
        values.put(KEY_ID, class_event.getId());
        values.put(KEY_CODE, class_event.getCode());
        values.put(KEY_NAME, class_event.getName());
        values.put(KEY_TEACHER_ID, class_event.getTeacherID());
        values.put(KEY_TEACHER_NAME, class_event.getTeacherName());
        values.put(KEY_LOCATION, class_event.getLocation());
        values.put(KEY_MAC, class_event.getMac());
        values.put(KEY_DATE, class_event.getDate());
        values.put(KEY_START, class_event.getStart());
        values.put(KEY_FINISH, class_event.getFinish());
        values.put(KEY_PRESENT, class_event.isPresent());

        // Inserting Row
        db.insert(TABLE_CLASSES, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single
    Class getClass(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, new String[]{KEY_ID, KEY_CODE, KEY_NAME, KEY_TEACHER_ID, KEY_TEACHER_NAME, KEY_LOCATION, KEY_MAC, KEY_DATE, KEY_START, KEY_FINISH, KEY_PRESENT}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        // return contact
        return new Class(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10));
    }

    // code to get all contacts in a list view
    public List<Class> getAllClasses() {
        List<Class> classList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CLASSES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Class class_event = new Class();
                class_event.setId(cursor.getInt(0));
                class_event.setCode(cursor.getString(1));
                class_event.setName(cursor.getString(2));
                class_event.setTeacherID(cursor.getInt(3));
                class_event.setTeacherName(cursor.getString(4));
                class_event.setLocation(cursor.getString(5));
                class_event.setMac(cursor.getString(6));
                class_event.setDate(cursor.getString(7));
                class_event.setStart(cursor.getString(8));
                class_event.setFinish(cursor.getString(9));
                class_event.setPresent(cursor.getString(10));
                // Adding contact to list
                classList.add(class_event);
            } while (cursor.moveToNext());
        }

        // return contact list
        return classList;
    }

    // Deleting single contact
    public void deleteClass(Class class_event) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASSES, KEY_ID + " = ?", new String[]{String.valueOf(class_event.getId())});
        db.close();
    }

    // Deleting single contact
    public void updateClassPresence(Class class_event, String present) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_PRESENT, present);
        db.update(TABLE_CLASSES, cv, KEY_ID + " = ?", new String[]{String.valueOf(class_event.getId())});
        db.close();
    }

}