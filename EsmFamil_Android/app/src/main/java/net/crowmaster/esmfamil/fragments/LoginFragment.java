package net.crowmaster.esmfamil.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.wrapp.floatlabelededittext.FloatLabeledEditTextLeftSided;
import com.wrapp.floatlabelededittext.FloatLabeledEditTextRightSided;

import net.crowmaster.esmfamil.R;
import net.crowmaster.esmfamil.activities.LoginActivity;
import net.crowmaster.esmfamil.activities.MainActivity;
import net.crowmaster.esmfamil.util.ProgressiveToast;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by root on 3/21/15.
 */
public class LoginFragment extends Fragment {
    FloatLabeledEditTextRightSided mUser;
    FloatLabeledEditTextLeftSided mPass;
    ButtonFlat login;
    ButtonFlat signup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Typeface tf = Typeface.createFromAsset(getResources().getAssets(),
                "bnazanin.ttf");

        mUser = (FloatLabeledEditTextRightSided) view.findViewById(R.id.username_entry);
        mUser.setTextColor(getResources().getColor(R.color.midnight_blue),
                getResources().getColor(R.color.hintColor),
                getResources().getColor(R.color.accentColor), tf);

        mPass = (FloatLabeledEditTextLeftSided) view.findViewById(R.id.password_entry);
        mPass.setTextColor(getResources().getColor(R.color.midnight_blue),
                getResources().getColor(R.color.hintColor),
                getResources().getColor(R.color.accentColor), tf);

        login = (ButtonFlat) view.findViewById(R.id.login_button);
        signup = (ButtonFlat) view.findViewById(R.id.signup_button);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((LoginActivity)getActivity()).mSignupFragment = new SignupFragment();
                ((LoginActivity)getActivity()).getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragment_holder,
                        ((LoginActivity)getActivity()).mSignupFragment).commit();
                ((LoginActivity)getActivity()).initUIForSignUp();


            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient myClient = new AsyncHttpClient();
                PersistentCookieStore cookieStore = new PersistentCookieStore(getActivity());
                myClient.setCookieStore(cookieStore);
                RequestParams params = new RequestParams();
                params.put("nickname",mUser.getEditText().getText().toString().trim());
                params.put("password",mPass.getEditText().getText().toString().trim());
                myClient.post(getActivity(),"http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/login",
                        params,new JsonHttpResponseHandler(){

                            @Override
                            public void onStart() {
                                super.onStart();
                                ProgressiveToast.show(getActivity(), "در حال برقراری ارتباط با سرور...");
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();
                                ProgressiveToast.dismiss();
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);

                                //Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
                                //Log.e("Success",response.toString() + " and status code is: " + Integer.toString(statusCode));

                                try {
                                    if((response.getString("result")).equals("30")) {
                                        getActivity().getSharedPreferences("userCredits", Context.MODE_PRIVATE).edit().
                                                putString("username",mUser.getEditText().getText().toString().trim()).
                                                putString("password",mPass.getEditText().getText().toString().trim()).
                                                putString("name",response.getString("name")).commit();
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                        getActivity().finish();
                                    } else if((response.getString("result")).equals("28")){
                                        Toast.makeText(getActivity(),"نام کاربری یا رمز عبور اشتباه می باشد. لطفا دوباره تلاش نمایید.",Toast.LENGTH_LONG).show();
                                    } else if((response.getString("result")).equals("29")){
                                        Toast.makeText(getActivity(),"ایمیل شما هنوز فعال نگردیده است. لطفا  پس از فعال نمودن آن اقدام به ورود نمایید.",Toast.LENGTH_LONG).show();
                                    } else if(response.getString("result").equals("27")){
                                        Toast.makeText(getActivity(),"پارامتر های ارسالی اشتباه میباشند!",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(),"خطای نامشخص",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("Exception in login",e.getMessage());
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                Toast.makeText(getActivity(), "خطا در برقراری ارتباط با سرور!" + "\n کد خطا : \n" + Integer.toString(statusCode), Toast.LENGTH_SHORT).show();
                                Log.e("Failure", Integer.toString(statusCode));
                            }
                        });





            }
        });
        //startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
