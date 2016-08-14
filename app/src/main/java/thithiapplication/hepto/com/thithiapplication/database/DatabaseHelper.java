package thithiapplication.hepto.com.thithiapplication.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import thithiapplication.hepto.com.thithiapplication.model.Details;

/**
 * Created by Balamurugan_G on 6/20/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "detailsManager";

    // Table Names
    private static final String TABLE_THITHI = "thithi";
    private static final String TABLE_STAR = "star";
    private static final String TABLE_THITHI_STAR = "thithi_star_data_2020";

    // Common column names
    private static final String KEY_ID = "id";

    // THITHI/STAR Table - common column nmaes
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "sdate";
    private static final String KEY_TIME = "stime";

    // THITHI_STAR_DATA Table - column names
    private static final String KEY_EDATE = "edate";
    private static final String KEY_SUN_TIME = "sunraise";
    private static final String KEY_SUN = "sun";
    private static final String KEY_MOON = "moon";
    private static final String KEY_THITHI_FRM = "thithifrom";
    private static final String KEY_THITHI_TO = "thithito";
    private static final String KEY_STAR_FRM = "starfrom";
    private static final String KEY_STAR_TO = "starto";

    // Table Create Statements
    // THITHI SAVED DATA table create statement
    private static final String CREATE_TABLE_THITHI = "CREATE TABLE "
            + TABLE_THITHI + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_DATE + " DATE," + KEY_TIME
            + " FLOAT" + ")";

    // STAR SAVED DATA table create statement
    private static final String CREATE_TABLE_STAR = "CREATE TABLE "
            + TABLE_STAR + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_DATE + " DATE," + KEY_TIME
            + " FLOAT" + ")";

    // THITHI_STAT_DATA table create statement
    private static final String CREATE_TABLE_THITHI_STAR = "CREATE TABLE "
            + TABLE_THITHI_STAR + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EDATE + " DATE," + KEY_SUN_TIME
            + " FLOAT," + KEY_SUN + " FLOAT," + KEY_MOON
            + " FLOAT," + KEY_THITHI_FRM + " FLOAT," + KEY_THITHI_TO
            + " FLOAT," + KEY_STAR_FRM + " FLOAT," + KEY_STAR_TO
            + " FLOAT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_THITHI);
        db.execSQL(CREATE_TABLE_STAR);
        db.execSQL(CREATE_TABLE_THITHI_STAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THITHI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THITHI_STAR);

        // create new tables
        onCreate(db);
    }



    // ------------------------ "THITHI CALC" table methods ----------------//

    /**
     * Creating a SAVED STAR DATA
     */
    public long createThithiCalcTable(String edate,String sunraise,String sun,String moon,String ththifrom,String ththito,String starfrom, String starto) {
        SQLiteDatabase db = this.getWritableDatabase();




        ContentValues values = new ContentValues();
        values.put(KEY_EDATE, edate);
        values.put(KEY_SUN_TIME, Float.parseFloat(sunraise));
        values.put(KEY_SUN, Float.parseFloat(sun));
        values.put(KEY_MOON, Float.parseFloat(moon));
        values.put(KEY_THITHI_FRM, Float.parseFloat(ththifrom));
        values.put(KEY_THITHI_TO, Float.parseFloat(ththito));
        values.put(KEY_STAR_FRM, Float.parseFloat(starfrom));
        values.put(KEY_STAR_TO, Float.parseFloat(starto));

        // insert row
        long s_id = db.insert(TABLE_THITHI_STAR, null, values);

        return s_id;
    }

    public int getThithiCalcTableCount() {
        String countQuery = "SELECT  * FROM " + TABLE_THITHI_STAR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }




    // ------------------------ "THITHI" table methods ----------------//

    /**
     * Creating a SAVED THITHI DATA
     */
    public long createThithi(Details details) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, details.getName());
        values.put(KEY_DATE, details.getDate());
        values.put(KEY_TIME, details.getTime());

        // insert row
        long t_id = db.insert(TABLE_THITHI, null, values);

        return t_id;
    }

    /**
     * getting all THITHIS
     * */
    public List<Details> getAllThithi() {
        List<Details> thithi = new ArrayList<Details>();
        String selectQuery = "SELECT  * FROM " + TABLE_THITHI;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Details td = new Details();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                td.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                td.setTime(c.getString(c.getColumnIndex(KEY_TIME)));

                // adding to thithi list
                thithi.add(td);
            } while (c.moveToNext());
        }

        return thithi;
    }

    /**
     * getting THITHI count
     */
    public int getThithiCount() {
        String countQuery = "SELECT  * FROM " + TABLE_THITHI;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }



    // ------------------------ "THITHI" table methods ----------------//

    /**
     * Creating a SAVED STAR DATA
     */
    public long createStar(Details details) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, details.getName());
        values.put(KEY_DATE, details.getDate());
        values.put(KEY_TIME, details.getTime());

        // insert row
        long s_id = db.insert(TABLE_STAR, null, values);

        return s_id;
    }

    /**
     * getting all STARS
     * */
    public List<Details> getAllStar() {
        List<Details> star = new ArrayList<Details>();
        String selectQuery = "SELECT  * FROM " + TABLE_STAR;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Details td = new Details();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                td.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                td.setTime(c.getString(c.getColumnIndex(KEY_TIME)));

                // adding to star list
                star.add(td);
            } while (c.moveToNext());
        }

        return star;
    }

    /**
     * getting STAR count
     */
    public int getStarCount() {
        String countQuery = "SELECT  * FROM " + TABLE_STAR;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }


}