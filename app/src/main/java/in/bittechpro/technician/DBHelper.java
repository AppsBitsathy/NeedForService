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
    private static final String TABLE_LOG ="NFSLOG";

    DBHelper(Context context) {
        super(context, DB_NAME, null, 20);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SMS + " (ID BIGINT PRIMARY KEY, NUMBER TEXT,BODY TEXT, STATE INTEGER DEFAULT 1)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DEV + " (ID BIGINT PRIMARY KEY, NAME TEXT, STATE INTEGER DEFAULT 0)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EMP + " (ID BIGINT PRIMARY KEY, NAME TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOG + " (ID BIGINT PRIMARY KEY, DEV_NAME TEXT NOT NULL, DEV_NUMBER BIGINT NOT NULL, COMP INTEGER NOT NULL, EMP_NAME TEXT, EMP_NUMBER BIGINT, ASSIGN_DATE BIGINT, FINISH_DATE BIGINT, STATE INTEGER DEFAULT 2)");

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
        long result = db.insertWithOnConflict(TABLE_SMS,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        return result == 1;
    }

    Cursor getAllSms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from "+TABLE_SMS+" where state = 1 order by id asc",null);
    }

    Cursor getLastSms(BigInteger number){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from SMS where NUMBER = "+number+" order by ID desc limit 1",null);
    }
    void smsState(BigInteger id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STATE",0);
        db.update(TABLE_SMS,contentValues,"ID = "+String.valueOf(id),null);
    }

    //////////////EMP////////////////

    boolean insertEMP(String name, BigInteger number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", String.valueOf(number));
        contentValues.put("NAME",name);
        long result = db.insertWithOnConflict(TABLE_EMP,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
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
        long result = db.insertWithOnConflict(TABLE_DEV,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        Log.d("result", "insertDEV: "+result);
        if(result!=-1){
            createDevCompTable(String.valueOf(number));
        }
        return result != -1 ;
    }

    Cursor getAllDev() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from "+TABLE_DEV+" ORDER BY STATE DESC, NAME ASC",null);
    }

    Cursor getOneDev(BigInteger id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from "+TABLE_DEV +" WHERE ID = "+id,null);
    }

    boolean deleteOneDev(BigInteger id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_DEV, "ID = "+id,null);
        return  result!=-1 && deleteCompTable(String.valueOf(id));
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
    private Boolean createDevCompTable(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS \"" + table + "\" (ID INTEGER PRIMARY KEY , STATE INTEGER DEFAULT 0, LOG_ID BIGINT DEFAULT 0, ASSIGNED TEXT DEFAULT 'Not yet assigned')");
        int i;
        for(i=1;i<=15;i++) {
            db.execSQL("INSERT OR REPLACE  INTO \""+table+"\"(ID) VALUES ("+String.valueOf(i)+")");
        }
        return  true;
    }
    boolean updateComp(String table,int id,int state,BigInteger log_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE \""+table+"\" SET STATE="+String.valueOf(state)+", LOG_ID = "+String.valueOf(log_id)+" WHERE ID = "+String.valueOf(id));
        return true;
    }
    boolean updateCompAssigned(String table,int id,String emp){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE \""+table+"\" SET ASSIGNED = \""+emp+"\" WHERE ID = "+String.valueOf(id));
        return true;
    }
    Cursor getDevCompState(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from \""+table +"\" WHERE STATE = 1",null);
    }
    Cursor getCompTable(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from \""+table +"\"",null);
    }
    private boolean deleteCompTable(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS \""+id+"\"");
        return  true;
    }
    BigInteger getLogId(String table,int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from \""+table +"\" WHERE id = "+id,null);
        res.moveToNext();
        BigInteger log_id = new BigInteger(res.getString(2));
        res.close();
        return log_id;
    }

    /////////////NFS LOG///////////
    boolean insertLog(BigInteger id,String dev_name, BigInteger dev_number, int comp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", String.valueOf(id));
        contentValues.put("DEV_NAME",dev_name);
        contentValues.put("DEV_NUMBER",String.valueOf(dev_number));
        contentValues.put("COMP",comp);
        long result = db.insertWithOnConflict(TABLE_LOG,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        return result == 1;
    }
    boolean updateLog(BigInteger id,String name,BigInteger number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMP_NAME", name);
        contentValues.put("EMP_NUMBER", String.valueOf(number));
        BigInteger date = BigInteger.valueOf(System.currentTimeMillis());
        contentValues.put("ASSIGN_DATE", String.valueOf(date));
        contentValues.put("STATE","1");
        db.update(TABLE_LOG, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        return true;
    }
    boolean updateLog(BigInteger id,BigInteger date,int state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FINISH_DATE", String.valueOf(date));
        contentValues.put("STATE", state);
        db.update(TABLE_LOG, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        return true;
    }

    ////////EXPORT LOG//////////
    Cursor exportLog(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select ID,DEV_NAME,DEV_NUMBER,COMP,EMP_NAME,EMP_NUMBER,ASSIGN_DATE,FINISH_DATE,STATE from "+TABLE_LOG,null);
    }
}
