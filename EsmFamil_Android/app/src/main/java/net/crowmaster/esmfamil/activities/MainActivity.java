package net.crowmaster.esmfamil.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import net.crowmaster.esmfamil.R;
import net.crowmaster.esmfamil.adapters.DrawerAdapter;
import net.crowmaster.esmfamil.entities.IconTitleEnt;
import net.crowmaster.esmfamil.fragments.EditProfileFragment;
import net.crowmaster.esmfamil.fragments.GameListFragment;
import net.crowmaster.esmfamil.providers.GameListContentProvider;
import net.crowmaster.esmfamil.services.EventCheckerService;
import net.crowmaster.esmfamil.util.ProgressiveToast;
import net.crowmaster.esmfamil.util.database.DBhelper;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    public TextView mTitle;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public GameListFragment mGameListFragment;
    public EditProfileFragment mEditProfileFragment;
    private SharedPreferences sp;
    private SharedPreferences.OnSharedPreferenceChangeListener spcl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        initUI();
        //enableShortcut();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void enableShortcut() {
        ((RippleView)findViewById(R.id.waiting_game_shortcut)).setVisibility(View.VISIBLE);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.tween);
        ((RippleView)findViewById(R.id.waiting_game_shortcut)).startAnimation(myFadeInAnimation);
        ((RippleView)findViewById(R.id.waiting_game_shortcut)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long prevGID =
                                getSharedPreferences("GameStats",MODE_PRIVATE).getLong("joinedGameGID",-1);
                        if(prevGID!=-1){// go to waiting activity as joined member

                        } else { // go to waiting activity as creator

                        }
                    }
                }
        );
    }

    public void disableShortcut() {
        ((RippleView)findViewById(R.id.waiting_game_shortcut)).setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*sp = getSharedPreferences("GameStats",MODE_PRIVATE);
        if(sp.getBoolean("activateShortcut",false)==false){
            disableShortcut();
        }else {
            enableShortcut();
        }
        sp.registerOnSharedPreferenceChangeListener(this);*/
    }

    private void initUI() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle("");
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.void_str, R.string.void_str);
        mDrawerLayout.setDrawerListener(mToggle);
        String[] drawerTitle = getResources().getStringArray(R.array.drawer_titles);
        TypedArray drawerIcons = getResources().obtainTypedArray(R.array.drawer_icons);
        ArrayList<IconTitleEnt> items = new ArrayList<IconTitleEnt>();
        for(int i = 0 ; i < drawerTitle.length ; i++ )
            items.add(new IconTitleEnt(drawerTitle[i], drawerIcons.getResourceId(i,-1)));
        drawerIcons.recycle();
        ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        DrawerAdapter mAdapter = new DrawerAdapter(this,R.layout.drawer_row,items);
        drawerList.setAdapter(mAdapter);
        drawerList.setItemChecked(0,true);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                switch (i){
                    case 0://لیست بازی ها<
                        if(mGameListFragment == null){
                            mGameListFragment = new GameListFragment();
                        }
                        if(!mGameListFragment.isVisible()) {

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                                    mGameListFragment).commit();
                            mTitle.setText("لیست بازی ها");
                        }
                        break;

                    case 1://آمار بازی های من

                        break;

                    case 2://ویرایش پروفایل
                        if(mEditProfileFragment==null) {
                            mEditProfileFragment = new EditProfileFragment();
                        }
                        if (!mEditProfileFragment.isVisible()){
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                                    mEditProfileFragment).commit();
                        }
                        mTitle.setText("ویرایش پروفایل");

                        break;

                    case 3://تنظیمات برنامه

                        break;
                    case 4://خروج از حساب
                        getSharedPreferences("userCredits",MODE_PRIVATE).edit().remove("username")
                                .remove("password").commit();

                        AsyncHttpClient myClient = new AsyncHttpClient();
                        final PersistentCookieStore cookieStore = new PersistentCookieStore(MainActivity.this);
                        myClient.setCookieStore(cookieStore);
                        myClient.get(MainActivity.this,
                                "http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/logout",
                                new JsonHttpResponseHandler(){
                                    @Override
                                    public void onStart() {
                                        super.onStart();
                                        ProgressiveToast.show(MainActivity.this, "در حال خروج...");
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
                                            if(response.getString("result").equals("30")) {
                                                cookieStore.clear();
                                                stopService(new Intent(MainActivity.this, EventCheckerService.class));
                                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                MainActivity.this.finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                        Toast.makeText(MainActivity.this,"خطا در ارتباط با سرور",Toast.LENGTH_SHORT).show();
                                    }

                                });

                        break;
                }
                mDrawerLayout.closeDrawers();
            }
        });



        refreshUserInfo();


        mGameListFragment = new GameListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                mGameListFragment).commit();
        mTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "bkoodak.ttf"));
        mTitle.setText("لیست بازی ها");
    }

    public void refreshUserInfo(){
        String val = getSharedPreferences("userCredits", MODE_PRIVATE).getString("name","خوش آمدید");
        ((TextView)findViewById(R.id.username)).setText(val);
        ((TextView)findViewById(R.id.username)).setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "bkoodak.ttf"));
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(findViewById(R.id.drawer_inner))){
            mDrawerLayout.closeDrawers();
        } else if(mGameListFragment!=null && !mGameListFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                    mGameListFragment).commit();

        } else if(mGameListFragment==null){
            mGameListFragment = new GameListFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                    mGameListFragment).commit();

        } else {
            this.finish();
            super.onBackPressed();
        }
    }




}
