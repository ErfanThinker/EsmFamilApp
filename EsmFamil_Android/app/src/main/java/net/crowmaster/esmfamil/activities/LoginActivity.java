package net.crowmaster.esmfamil.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import net.crowmaster.esmfamil.R;
import net.crowmaster.esmfamil.fragments.LoginFragment;
import net.crowmaster.esmfamil.fragments.SignupFragment;

/**
 * Created by root on 3/20/15.
 */
public class LoginActivity extends AppCompatActivity {
    Toolbar mToolbar;

    public LoginFragment mLoginFragment;
    public SignupFragment mSignupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        initUI();

        mLoginFragment = new LoginFragment();

        ((TextView)findViewById(R.id.title)).setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                "bkoodak.ttf"));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,mLoginFragment).commit();
    }

    private void initUI() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle("");
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(false);

    }

    public void initUIForSignUp(){
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_arrow_back);
    }

    @Override
    public void onBackPressed() {
        if(mSignupFragment!=null && mSignupFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,mLoginFragment).commit();
            initUI();
            return ;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            if(mSignupFragment!=null && mSignupFragment.isVisible()){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,mLoginFragment).commit();
                initUI();
                return true;
            }
        return super.onOptionsItemSelected(item);
    }
}
