package net.crowmaster.esmfamil.services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import net.crowmaster.esmfamil.providers.GameListContentProvider;
import net.crowmaster.esmfamil.util.database.DBhelper;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 4/30/15.
 */
public class EventCheckerService extends Service {

    PersistentCookieStore myCookieStore;
    DBhelper mydb = null;
    private static Handler handler;
    private static Runnable runnable ;
    private int UPDATE_INTERVAL = 8;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UPDATE_INTERVAL = intent.getIntExtra("updateInterval",8);
        runnable = new Runnable() {
            @Override
            public void run() {
                updateInfo();
            }
        };
        handler = new Handler();
        handler.post(runnable);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(mydb!=null)
            mydb.close();
        super.onDestroy();
    }

    private void updateInfo() {
        if(mydb == null){
            mydb = new DBhelper(getApplicationContext());
        }

        AsyncHttpClient client = new AsyncHttpClient();
        myCookieStore = new PersistentCookieStore(getApplicationContext());
        client.setCookieStore(myCookieStore);
        client.get(getApplicationContext(),
                "http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/game/getListOfGames",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        JSONArray games = new JSONArray();

                        try {
                            games =response.getJSONArray("gameList");

                            mydb.clearTable();


                            for(int i = 0; i<games.length(); i++){
                                ContentValues cv = new ContentValues();
                                cv.put(DBhelper.GAME_LIST_COLUMN_CAPACITY,games.getJSONObject(i)
                                        .getInt("maxnumofplayers"));
                                cv.put(DBhelper.GAME_LIST_COLUMN_CREATOR,games.getJSONObject(i)
                                        .getString("creaternickname"));
                                cv.put(DBhelper.GAME_LIST_COLUMN_GAME_NAME,games.getJSONObject(i)
                                        .getString("gname"));
                                cv.put(DBhelper.GAME_LIST_COLUMN_GID,games.getJSONObject(i)
                                        .getInt("gid"));
                                cv.put(DBhelper.GAME_LIST_COLUMN_JOINED,games.getJSONObject(i)
                                        .getInt("currentlyJoined"));
                                cv.put(DBhelper.GAME_LIST_COLUMN_ROUNDS,games.getJSONObject(i)
                                        .getInt("rounds"));


                                getApplicationContext().getContentResolver().insert(
                                        GameListContentProvider.Constants.GamestsURL, cv);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Error parsing JSONOBJ",e.getMessage());
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if(handler == null){
                            handler = new Handler();
                        }
                        handler.postDelayed(runnable, UPDATE_INTERVAL*1000);

                    }
                });
    }
}
