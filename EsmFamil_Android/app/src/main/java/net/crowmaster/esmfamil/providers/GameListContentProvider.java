package net.crowmaster.esmfamil.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import net.crowmaster.esmfamil.util.database.DBhelper;

/**
 * Created by root on 4/28/15.
 */
public class GameListContentProvider extends ContentProvider {

    private DBhelper db;
    private static final String AUTHORITY = "net.crowmaster.esmfamil.gamelistContentProvider";
    private static final String TABLE="GameList";
    private static final int GAMELIST = 200;
    private static final UriMatcher MATCHER;
    public static final class Constants implements BaseColumns {
        public static final Uri GamestsURL=Uri.parse("content://" + AUTHORITY + "/"+TABLE);
    }

    private String[] colorPreset = {"#2196f3","#f44336","#e91e63","#9c27b0","#673ab7",
            "#3f51b5","#03a9f4","#009688",
            "#4caf50","#8bc34a","#cddc39","#ffc107",
            "#ff9800","#ff5722","#795548","#9e9e9e","#607d8b"};


    static {
        MATCHER=new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(AUTHORITY,
                TABLE, GAMELIST);
        /*MATCHER.addURI(AUTHORITY,
                TABLE + "/#", CONTACTS_ID);*/
    }
    @Override
    public boolean onCreate() {
        db = new DBhelper(getContext());
        return (db==null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        qb.setTables(TABLE);
        Cursor c=
                qb.query(db.getReadableDatabase(), projection, selection,
                        selectionArgs, null, null, sortOrder);


        c.setNotificationUri(getContext().getContentResolver(), uri);
        //c.moveToFirst();
        return(c);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        values.put(DBhelper.GAME_LIST_COLUMN_COLOR,
                colorPreset[values.getAsInteger(DBhelper.GAME_LIST_COLUMN_GID)%colorPreset.length]);
        long rowID=
                db.getWritableDatabase().insert(TABLE, null, values);

        if (rowID > 0) {
            Uri url=
                    ContentUris.withAppendedId(Constants.GamestsURL,
                            rowID);
            getContext().getContentResolver().notifyChange(url, null);

            return(url);
        }

        try {
            throw new SQLException("Failed to insert row into " + uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int count = db.getWritableDatabase().delete(TABLE, s, strings);

        getContext().getContentResolver().notifyChange(uri, null);

        return(count);

    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {
        int count=
                db.getWritableDatabase()
                        .update(TABLE, values, where, whereArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return(count);
    }
}
