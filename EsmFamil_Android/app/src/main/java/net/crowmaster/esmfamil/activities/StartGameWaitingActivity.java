package net.crowmaster.esmfamil.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import net.crowmaster.esmfamil.R;
import net.crowmaster.esmfamil.services.EventCheckerService;
import net.crowmaster.esmfamil.util.ProgressiveToast;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 5/1/15.
 */
public class StartGameWaitingActivity extends AppCompatActivity {
    ButtonFlat returnBTN;
    ButtonFlat cancelBTN;
    String mode = "";
    Toolbar mToolbar;
    long gid = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_activity);
        mode = getIntent().getStringExtra("mode");
        gid = getIntent().getIntExtra("gid", -1);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        initUI();

        returnBTN = (ButtonFlat) findViewById(R.id.waiting_return_btn);
        cancelBTN = (ButtonFlat) findViewById(R.id.waiting_cancel_button);

        returnBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnWithoutLeavingGame();
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveGame();
            }
        });
        startService(new Intent(this, EventCheckerService.class).putExtra("updateInterval",2));

    }

    public void initUI(){
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setTitle("");
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_arrow_back);
    }

    private void returnWithoutLeavingGame(){
        if(gid!=-1){
            getSharedPreferences("GameStats",MODE_PRIVATE).edit().putLong("joinedGameGID",gid).commit();
        } else {
            getSharedPreferences("GameStats",MODE_PRIVATE).edit().remove("joinedGameGID").commit();
        }

        StartGameWaitingActivity.this.finish();
    }

    @Override
    protected void onPause() {
        returnWithoutLeavingGame();
        super.onPause();
    }

    private void leaveGame(){
        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore mCookies = new PersistentCookieStore(StartGameWaitingActivity.this);
        client.setCookieStore(mCookies);
        if(mode.equals("creator")){
            client.get(StartGameWaitingActivity.this,
                    "http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/game/removeGame",
                    new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    super.onStart();
                    ProgressiveToast.show(StartGameWaitingActivity.this
                            ,"در حال ترک کردن بازی." + "\n" + "لطفاً صبور باشید...");
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
                            startService(new Intent(StartGameWaitingActivity.this,
                                    EventCheckerService.class).putExtra("updateInterval",8));
                            StartGameWaitingActivity.this.finish();
                        } else if ((response.getString("result")).equals("34")) {
                            Toast.makeText(getApplicationContext(), "خطا! لطفا خارج شده و دوباره اقدام به ورود نمایید.",
                                    Toast.LENGTH_LONG).show();
                            startService(new Intent(StartGameWaitingActivity.this,
                                    EventCheckerService.class).putExtra("updateInterval",8));
                            StartGameWaitingActivity.this.finish();

                        } else if ((response.getString("result")).equals("42")) {
                            Toast.makeText(getApplicationContext(), "خطا! این بازی دیگر وجود ندارد.",
                                    Toast.LENGTH_LONG).show();
                            //TODO remove user creds from sharedPref
                            startService(new Intent(StartGameWaitingActivity.this,
                                    EventCheckerService.class).putExtra("updateInterval",8));
                            StartGameWaitingActivity.this.finish();
                        }  else if ((response.getString("result")).equals("43")) {
                            Toast.makeText(getApplicationContext(), "شرمنده نشد که بشه!",
                                    Toast.LENGTH_LONG).show();
                            //Shall I keep user here???
                        } else {
                            Toast.makeText(getApplicationContext(), "خطای نامشخص", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Exception in login", e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(getApplicationContext(),
                            "خطا در برقراری ارتباط با سرور!" + "\n کد خطا : \n" + Integer.toString(statusCode),
                            Toast.LENGTH_SHORT).show();
                    Log.e("Failure", Integer.toString(statusCode));
                }
            });
        }else {
            RequestParams params = new RequestParams("gid",gid);
            client.post(StartGameWaitingActivity.this,
                    "http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/game/removePlayerFromGame",
                    params , new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    super.onStart();
                    ProgressiveToast.show(StartGameWaitingActivity.this
                            ,"در حال ترک کردن بازی." + "\n" + "لطفاً صبور باشید...");
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
                            StartGameWaitingActivity.this.finish();
                        } /*else if ((response.getString("result")).equals("34")) {
                            Toast.makeText(getApplicationContext(), "خطا! لطفا خارج شده و دوباره اقدام به ورود نمایید.",
                                    Toast.LENGTH_LONG).show();
                        }*/ else {
                            Toast.makeText(getApplicationContext(), "خطای نامشخص", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Exception in login", e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(getApplicationContext(),
                            "خطا در برقراری ارتباط با سرور!" + "\n کد خطا : \n" + Integer.toString(statusCode),
                            Toast.LENGTH_SHORT).show();
                    Log.e("Failure", Integer.toString(statusCode));
                }
            });

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            returnWithoutLeavingGame();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        returnWithoutLeavingGame();
        //super.onBackPressed();
    }
}
