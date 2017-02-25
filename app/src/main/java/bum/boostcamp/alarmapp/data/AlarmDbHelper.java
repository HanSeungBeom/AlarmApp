package bum.boostcamp.alarmapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import bum.boostcamp.alarmapp.data.AlarmContract.AlarmEntry;


//작성 완료.

public class AlarmDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "alarmsDb.db";
    private static final int VERSION = 1;
    AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + AlarmEntry.TABLE_NAME + " (" +
                AlarmEntry._ID               + " INTEGER PRIMARY KEY, " +
                AlarmEntry.COLUMN_AMPM      + " INTEGER NOT NULL, " + //AM:0 PM:1
                AlarmEntry.COLUMN_HOUR      + " INTEGER NOT NULL," +
                AlarmEntry.COLUMN_HOUR_OF_DAY +" INTEGER NOT NULL,"+
                AlarmEntry.COLUMN_MINUTES   + " INTEGER NOT NULL," +
                AlarmEntry.COLUMN_DAYS      + " TEXT NOT NULL," +       //1110011 일월화수목금토일
                AlarmEntry.COLUMN_ACTIVE    + " INTEGER NOT NULL," +  //NONACTIVE:0 ACTIVE:1
                AlarmEntry.COLUMN_MEMO      + " TEXT," +
                AlarmEntry.COLUMN_DATE      + " TEXT NOT NULL," +
                AlarmEntry.COLUMN_BELLTYPE+" INTEGER NOT NULL,"+
                AlarmEntry.COLUMN_NO_REPEAT_YEAR  + " INTEGER,"+
                AlarmEntry.COLUMN_NO_REPEAT_MONTH+" INTEGER," +
                AlarmEntry.COLUMN_NO_REPEAT_DAYS+" INTEGER);";

        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
