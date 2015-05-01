package net.crowmaster.esmfamil.util.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by root on 4/28/15.
 */
public class DBhelper extends SQLiteOpenHelper {

    private Context mContext;

    public static final String DATABASE_NAME = "NameFamily.db";

    //SelfContactTable
    public static final String GAME_LIST_TABLE_NAME = "GameList";
    public static final String GAME_LIST_COLUMN_ID = "GameKey";
    public static final String GAME_LIST_COLUMN_GAME_NAME = "GameName";
    public static final String GAME_LIST_COLUMN_GID = "GID";
    public static final String GAME_LIST_COLUMN_CREATOR = "GameCreatorNickName";
    public static final String GAME_LIST_COLUMN_JOINED = "GamedJoinedNumber";
    public static final String GAME_LIST_COLUMN_CAPACITY = "GameCapacity";
    public static final String GAME_LIST_COLUMN_ROUNDS = "GameRounds";
    public static final String GAME_LIST_COLUMN_COLOR = "GameColor";


    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table GameList " +
                        "(GameKey INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " GameName TEXT, GID INTEGER, GameCreatorNickName TEXT," +
                        " GamedJoinedNumber INTEGER, GameCapacity INTEGER," +
                        " GameRounds INTEGER, GameColor TEXT);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        try {
            throw new Exception("How did we get here?");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(GAME_LIST_TABLE_NAME,null,null);
    }
}
