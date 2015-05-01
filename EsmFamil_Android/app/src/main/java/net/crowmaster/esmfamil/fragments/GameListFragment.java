package net.crowmaster.esmfamil.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.wrapp.floatlabelededittext.FloatLabeledEditTextRightSided;

import net.crowmaster.esmfamil.R;
import net.crowmaster.esmfamil.activities.MainActivity;
import net.crowmaster.esmfamil.activities.StartGameWaitingActivity;
import net.crowmaster.esmfamil.adapters.GameListRecyclerAdapter;
import net.crowmaster.esmfamil.adapters.RangeSpinnerAdapter;
import net.crowmaster.esmfamil.entities.GameListItemEnt;
import net.crowmaster.esmfamil.providers.GameListContentProvider;
import net.crowmaster.esmfamil.services.EventCheckerService;
import net.crowmaster.esmfamil.util.HotFixGridLayoutManager;
import net.crowmaster.esmfamil.util.ProgressiveToast;
import net.crowmaster.esmfamil.util.database.DBhelper;
import net.crowmaster.esmfamil.widgets.HotFixRecyclerView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by root on 4/26/15.
 */
public class GameListFragment extends Fragment implements GameListRecyclerAdapter.IOnListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{
    ButtonFloat addBtn;
    HotFixRecyclerView gList;
    HotFixGridLayoutManager glm;
    ArrayList<GameListItemEnt> data;
    GameListRecyclerAdapter adapter;
    SimpleCursorAdapter mAdapter;
    DBhelper db;
    LandingAnimator animator;
    private final int GAMES_LOADER_ID = 2017;
    Bundle mSavedInstanceState;
    View mView;
    boolean firstRun = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_of_games_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSavedInstanceState=savedInstanceState;
        mView = view;
        db = new DBhelper(getActivity());
        if(firstRun)
            db.clearTable();
        addBtn = (ButtonFloat) view.findViewById(R.id.addBTN);
        addBtn.setBackgroundColor(getResources().getColor(R.color.white_dark));
        glm = new HotFixGridLayoutManager(getActivity(),2);
        gList = (HotFixRecyclerView) view.findViewById(R.id.listOfGames);
        glm.setOrientation(LinearLayoutManager.VERTICAL);
        glm.setSmoothScrollbarEnabled(true);
        gList.setLayoutManager(glm);
        gList.setHasFixedSize(true);
        data = new ArrayList<GameListItemEnt>();
        adapter = new GameListRecyclerAdapter(getActivity(),data , this);
        animator = new LandingAnimator();
        gList.setItemAnimator(animator);
        adapter.setHasStableIds(true);
        gList.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View dialogView = ((LayoutInflater)getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.newgame_dialog,null);

                Typeface tf = Typeface.createFromAsset(getResources().getAssets(),
                        "bkoodak.ttf");

                ((TextView)dialogView.findViewById(R.id.newgame_title)).setTypeface(tf);
                ((TextView)dialogView.findViewById(R.id.maxplayertv)).setTypeface(tf);
                ((TextView)dialogView.findViewById(R.id.roundstv)).setTypeface(tf);

                final Spinner maxPlayers = (Spinner) dialogView.findViewById(R.id.maxNumOfPlayersSp);
                maxPlayers.setAdapter(new RangeSpinnerAdapter(getActivity(),2,10));

                final Spinner numOfRounds = (Spinner) dialogView.findViewById(R.id.roundnum_spinner);
                numOfRounds.setAdapter(new RangeSpinnerAdapter(getActivity(),1,20));

                final FloatLabeledEditTextRightSided gameName =
                        (FloatLabeledEditTextRightSided) dialogView.findViewById(R.id.new_game_name);

                gameName.setTextColor(getResources().getColor(R.color.midnight_blue),
                        getResources().getColor(R.color.hintColor),
                        getResources().getColor(R.color.accentColor), tf);

                final MaterialDialog mMaterialDialog = new MaterialDialog(getActivity());
                mMaterialDialog.setBackgroundResource(R.drawable.paper_background_2)
                        .setView(dialogView).setPositiveButton("تایید", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (gameName.getEditText().getText().toString().trim().isEmpty()) {
                            Toast.makeText(getActivity(), "لطفا یک نام برای بازی در نظر بگیرید.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mMaterialDialog.dismiss();
                        AsyncHttpClient client = new AsyncHttpClient();
                        PersistentCookieStore cookies = new PersistentCookieStore(getActivity());
                        client.setCookieStore(cookies);
                        RequestParams params = new RequestParams();
                        params.put("maxPlayer", (int) maxPlayers.getSelectedItem());
                        params.put("rounds", (int) numOfRounds.getSelectedItem());
                        params.put("gname", gameName.getEditText().getText().toString().trim());
                        client.post(getActivity(),
                                "http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/game/createNewGame",
                                params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onStart() {
                                        super.onStart();
                                        ProgressiveToast.show(getActivity(), "در حال ساخت بازی جدید...");
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        ProgressiveToast.dismiss();
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        try {
                                            if ((response.getString("result")).equals("30")) {
                                                startActivity(new Intent(getActivity(), StartGameWaitingActivity.class)
                                                .putExtra("mode","creator"));
                                            } else if ((response.getString("result")).equals("34")) {
                                                Toast.makeText(getActivity(), "خطا! لطفا خارج شده و دوباره اقدام به ورود نمایید.", Toast.LENGTH_LONG).show();
                                            } else if (response.getString("result").equals("27")) {
                                                Toast.makeText(getActivity(), "پارامتر های ارسالی اشتباه میباشند!", Toast.LENGTH_SHORT).show();
                                            } else if (response.getString("result").equals("38")) {
                                                Toast.makeText(getActivity(), "خطا! برای ساختن بازی جدید باید بازی کنونی اتمام یابد.", Toast.LENGTH_SHORT).show();
                                            } else if (response.getString("result").equals("39")) {
                                                Toast.makeText(getActivity(), "خطاَ شما یک بازی ساخته اید که هنوز اتمام نیافته است. تا اتمام آن قادر به ساخت بازی جدید نخواهید بود.", Toast.LENGTH_LONG).show();
                                            } else if (response.getString("result").equals("41")) {
                                                Toast.makeText(getActivity(), "باعث خجالته ولی نشد که بشه!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "خطای نامشخص", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.e("Exception in login", e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        super.onFailure(statusCode, headers, responseString, throwable);
                                        Toast.makeText(getActivity(), "خطا در برقراری ارتباط با سرور!" + "\n کد خطا : \n" + Integer.toString(statusCode), Toast.LENGTH_SHORT).show();
                                        Log.e("Failure", Integer.toString(statusCode));
                                    }
                                });

                    }
                }).setNegativeButton("انصراف", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                }).setCanceledOnTouchOutside(true);

                mMaterialDialog.show();
            }
        });
        setRetainInstance(true);
        if(isVisible()){
            initListLoad();
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initListLoad();
                }
            },500);
        }


    }

    private void initListLoad() {

        if(firstRun){

            AsyncHttpClient client = new AsyncHttpClient();
            PersistentCookieStore cookies = new PersistentCookieStore(getActivity());
            client.setCookieStore(cookies);
            client.get(getActivity(),"http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/game/getListOfGames",
                    new JsonHttpResponseHandler(){
                        @Override
                        public void onStart() {
                            super.onStart();
                            ProgressiveToast.show(getActivity(),"در حال دریافت لیست بازی ها...");
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            ProgressiveToast.dismiss();
                            getActivity().startService(new Intent(getActivity(), EventCheckerService.class));
                            getLoaderManager().initLoader(GAMES_LOADER_ID, mSavedInstanceState,
                                    GameListFragment.this);
                            firstRun = false;
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            JSONArray games = new JSONArray();
                            JSONObject activeGame = new JSONObject();

                            try {
                                games =response.getJSONArray("gameList");
                                activeGame = response.getJSONObject("activeGame");
                                if(activeGame.length()>0 && !activeGame.isNull("gameState")
                                        && activeGame.getInt("gameState") == 0){
                                    ((MainActivity)getActivity()).enableShortcut();
                                } else {
                                    ((MainActivity)getActivity()).disableShortcut();
                                }



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


                                    getActivity().getContentResolver().insert(
                                            GameListContentProvider.Constants.GamestsURL, cv);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("Error parsing JSONOBJ",e.getMessage());
                            }
                        }



                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Toast.makeText(getActivity(), "خطا در برقراری ارتباط با سرور!" + "\n کد خطا : \n" + Integer.toString(statusCode), Toast.LENGTH_SHORT).show();
                            Log.e("Failure", Integer.toString(statusCode));
                        }


                    });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        db = new DBhelper(getActivity());
        getLoaderManager()
                .restartLoader(GAMES_LOADER_ID, mSavedInstanceState, GameListFragment.this);

    }

    @Override
    public void onPause() {
        getLoaderManager().destroyLoader(GAMES_LOADER_ID);
        if(gList!=null)
            gList.stopScroll();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(gList!=null)
            gList.stopScroll();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (animator != null) {
            animator.endAnimations();
        }
        if(gList!=null)
            gList.stopScroll();

        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        //Adapter item click listener
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                GameListContentProvider.Constants.GamestsURL, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        new AsyncGameListLoader(data).execute();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setData(null);

    }


    private class AsyncGameListLoader  extends AsyncTask<Void,Void,ArrayList<GameListItemEnt>>{
        Cursor cursor;
        public AsyncGameListLoader(Cursor c){
            cursor = c;
        }

        @Override
        protected ArrayList<GameListItemEnt> doInBackground(Void... voids) {
            ArrayList<GameListItemEnt> games=new ArrayList<GameListItemEnt>();
            if(cursor!=null && cursor.moveToFirst())
                while(cursor.isAfterLast() == false){
                    games.add(new GameListItemEnt(cursor.getLong(cursor.getColumnIndex(DBhelper.GAME_LIST_COLUMN_GID)),
                            cursor.getLong(cursor.getColumnIndex(DBhelper.GAME_LIST_COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(DBhelper.GAME_LIST_COLUMN_GAME_NAME)),
                            cursor.getString(cursor.getColumnIndex(DBhelper.GAME_LIST_COLUMN_CREATOR)),
                            cursor.getInt(cursor.getColumnIndex(DBhelper.GAME_LIST_COLUMN_CAPACITY)),
                            cursor.getInt(cursor.getColumnIndex(DBhelper.GAME_LIST_COLUMN_JOINED)),
                            cursor.getInt(cursor.getColumnIndex(DBhelper.GAME_LIST_COLUMN_ROUNDS)),
                            cursor.getString(cursor.getColumnIndex(DBhelper.GAME_LIST_COLUMN_COLOR))));
                    cursor.moveToNext();
                }
            return games;
        }

        @Override
        protected void onPostExecute(ArrayList<GameListItemEnt> gameListItemEnts) {
            super.onPostExecute(gameListItemEnts);
            adapter.setData(gameListItemEnts);
            if(mView.isShown()) {
                gList.setVisibility(View.VISIBLE);
                if (!(adapter.getItemCount() > 0)) {
                    ((TextView) mView.findViewById(R.id.empty_list_layout)).setVisibility(View.VISIBLE);
                    ((TextView) mView.findViewById(R.id.empty_list_layout)).setText("در حال حاضر بازی ای وجود ندارد!");
                } else {
                    ((TextView) mView.findViewById(R.id.empty_list_layout)).setVisibility(View.GONE);
                }
            }
        }
    }
}
