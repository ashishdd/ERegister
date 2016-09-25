package dashing.dual.com.eregister;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;

/**
 * Created by ashishrawat on 6/4/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "Sl_no";
    public static final String CONTACTS_COLUMN_NAME = "Name";
    public static final String CONTACTS_COLUMN_UID = "MID_or_Phone";
    public static final String CONTACTS_COLUMN_APPROVER = "Approver";
    public static final String CONTACTS_COLUMN_PURPOSE = "Purpose";
    public static final String CONTACTS_COLUMN_IN_TIME = "In_Time";
    public static final String CONTACTS_COLUMN_OUT_TIME = "Out_Time";
    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table contacts " +
                        "("+ CONTACTS_COLUMN_ID +" integer primary key, "
                        + CONTACTS_COLUMN_NAME +" text,"
                        + CONTACTS_COLUMN_UID +" text,"
                        + CONTACTS_COLUMN_PURPOSE +" text,"
                        + CONTACTS_COLUMN_APPROVER +" text,"
                        + CONTACTS_COLUMN_IN_TIME  + " text,"
                        + CONTACTS_COLUMN_OUT_TIME  + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact  (String name, String uid, String purpose, String approver, String in_time, String out_time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_UID, uid);
        contentValues.put(CONTACTS_COLUMN_PURPOSE, purpose);
        contentValues.put(CONTACTS_COLUMN_APPROVER, approver);
        contentValues.put(CONTACTS_COLUMN_IN_TIME, in_time);
        contentValues.put(CONTACTS_COLUMN_OUT_TIME, out_time);
        db.insert("contacts", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where "+ CONTACTS_COLUMN_ID +"="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String uid, String in_time, String out_time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, name);
        contentValues.put(CONTACTS_COLUMN_UID, uid);
        contentValues.put(CONTACTS_COLUMN_IN_TIME, in_time);
        contentValues.put(CONTACTS_COLUMN_OUT_TIME, out_time);
        db.update("contacts", contentValues, CONTACTS_COLUMN_ID +" = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                CONTACTS_COLUMN_ID +" = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllContacts()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)) + " : " +
                            res.getString(res.getColumnIndex(CONTACTS_COLUMN_UID)) + " : " +
                            res.getString(res.getColumnIndex(CONTACTS_COLUMN_IN_TIME))+ " : " +
                            res.getString(res.getColumnIndex(CONTACTS_COLUMN_OUT_TIME)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllInVMContacts()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where Out_Time = 0 AND MID_or_Phone NOT LIKE \"M%\"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)) + " : " +
                    res.getString(res.getColumnIndex(CONTACTS_COLUMN_UID)));
            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList<String> getAllInOContacts()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where Out_Time = 0 AND MID_or_Phone LIKE \"M%\" ", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)) + ":" +
                    res.getString(res.getColumnIndex(CONTACTS_COLUMN_UID)));
            res.moveToNext();
        }
        return array_list;
    }

    public void outUpdate(List<String> out){
        Calendar c = Calendar.getInstance();
        String date = "" + c.get(Calendar.YEAR) +":"+ c.get(Calendar.MONTH) +":"+ c.get(Calendar.DATE)+":"
                + c.get(Calendar.HOUR) +":"+ c.get(Calendar.MINUTE) +":"+ c.get(Calendar.SECOND);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_OUT_TIME, date);
        db.update("contacts", contentValues, "MID_or_Phone = ? AND name = ?", new String[] {out.get(1), out.get(0)});

    }
}