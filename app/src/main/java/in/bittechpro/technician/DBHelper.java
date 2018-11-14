package in.bittechpro.technician;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigInteger;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TECH_DB";
    private static final String TABLE_SMS ="SMS";
    private static final String TABLE_EMP ="EMP";
    private static final String TABLE_DEV ="DEV";

    DBHelper(Context context) {
        super(context, DB_NAME, null, 11);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SMS + " (ID BIGINT PRIMARY KEY ,NUMBER TEXT,BODY TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DEV + " (ID BIGINT PRIMARY KEY ,NAME TEXT,ROLL TEXT DEFAULT 'Employee not assigned', STATE INTEGER DEFAULT 0)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EMP + " (ID BIGINT PRIMARY KEY,NAME TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_SMS);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_EMP);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_DEV);

        onCreate(sqLiteDatabase);

    }

    /////////////SMS///////////////
    boolean insertSMS(String number, String body, BigInteger date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", String.valueOf(date));
        contentValues.put("NUMBER",number);
        contentValues.put("BODY",body);
        long result = db.insertWithOnConflict(TABLE_SMS,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    Cursor getAllSms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from "+TABLE_SMS,null);
    }

    Cursor getLastSms(BigInteger number){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from SMS where NUMBER = "+number+" order by ID desc limit 1",null);
    }

    //////////////EMP////////////////

    boolean insertEMP(String name, BigInteger number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", String.valueOf(number));
        contentValues.put("NAME",name);
        long result = db.insertWithOnConflict(TABLE_EMP,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    Cursor getAllEMP() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from "+TABLE_EMP,null);
    }

    Cursor getOneEMP(BigInteger id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from "+TABLE_EMP +" WHERE ID = "+id,null);
    }

    boolean deleteOneEMP(BigInteger id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_EMP, "ID = "+id,null);
        return  result!=-1;
    }
    boolean updateEMP(BigInteger id,BigInteger number,String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", String.valueOf(number));
        contentValues.put("NAME",name);
        db.update(TABLE_EMP, contentValues, "ID = "+id,null);
        return true;
    }

    //////////////DEV////////////////

    boolean insertDEV(String name, BigInteger number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", String.valueOf(number));
        contentValues.put("NAME",name);
        long result = db.insertWithOnConflict(TABLE_DEV,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        //createDevCompTable(String.valueOf(number));
        return result != -1;
    }

    Cursor getAllDev() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from "+TABLE_DEV,null);
    }

    Cursor getOneDev(BigInteger id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from "+TABLE_DEV +" WHERE ID = "+id,null);
    }

    boolean deleteOneDev(BigInteger id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_DEV, "ID = "+id,null);
        return  result!=-1;
    }
    boolean updateDev(BigInteger id,BigInteger number,String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", String.valueOf(number));
        contentValues.put("NAME",name);
        db.update(TABLE_DEV, contentValues, "ID = "+id,null);
        return true;
    }
    boolean updateDev(BigInteger id,int state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STATE",state);
        db.update(TABLE_DEV, contentValues, "ID = "+id,null);
        return true;
    }
    boolean updateDev_emp(BigInteger id,String assign){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", String.valueOf(id));
        contentValues.put("ROLL",assign);
        db.update(TABLE_DEV, contentValues, "ID = "+id,null);
        return true;
    }

    /////////////DYNAMIC TABLE/////
    void createDevCompTable(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS \"" + table + "\" (ID INTEGER PRIMARY KEY , STATE INTEGER DEFAULT 0)");
        int i;
        for(i=1;i<=15;i++) {
            /*ContentValues contentValues = new ContentValues();
            contentValues.put("ID", String.valueOf(i));
            db.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);*/
            db.execSQL("INSERT OR REPLACE  INTO \""+table+"\"(ID) VALUES ("+String.valueOf(i)+")");
        }
    }
    boolean updateComp(String table,int id,int state){
        SQLiteDatabase db = this.getWritableDatabase();
        /*ContentValues contentValues = new ContentValues();
        contentValues.put("STATE",state);
        db.update(table, contentValues, "ID = "+id,null);*/
        db.execSQL("UPDATE \""+table+"\" SET STATE="+String.valueOf(state)+" WHERE ID = "+String.valueOf(id));
        return true;
    }
    Cursor getOneDevState(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from \""+table +"\" WHERE STATE = 1",null);
    }



}
