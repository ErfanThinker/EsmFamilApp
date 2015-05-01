package net.crowmaster.esmfamil.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.wrapp.floatlabelededittext.FloatLabeledEditTextLeftSided;
import com.wrapp.floatlabelededittext.FloatLabeledEditTextRightSided;

import net.crowmaster.esmfamil.R;
import net.crowmaster.esmfamil.activities.LoginActivity;
import net.crowmaster.esmfamil.util.EmailValidator;
import net.crowmaster.esmfamil.util.PasswordValidator;
import net.crowmaster.esmfamil.util.PersianCalendar;
import net.crowmaster.esmfamil.util.ProgressiveToast;
import net.crowmaster.esmfamil.widgets.PersianDatePicker;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * Created by root on 3/21/15.
 */
public class SignupFragment extends Fragment {
    FloatLabeledEditTextRightSided mUserName;
    FloatLabeledEditTextRightSided mName;
    FloatLabeledEditTextLeftSided mrePass;
    FloatLabeledEditTextLeftSided mEmail;
    FloatLabeledEditTextLeftSided mPass;
    PersianDatePicker mPersianDatePicker;
    ButtonFlat submitBTN;
    int mode = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.signup_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Typeface tf = Typeface.createFromAsset(getResources().getAssets(),
                "bnazanin.ttf");
        if(isVisible()) {
            ((View) view.findViewById(R.id.signup_root_view)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mode == 0) {
                        ((View) view.findViewById(R.id.signup_root_view)).setBackgroundResource(R.drawable.paper_background_0);
                        mode++;
                    } else if (mode == 1) {
                        ((View) view.findViewById(R.id.signup_root_view)).setBackgroundResource(R.drawable.swirl_background);
                        mode++;
                    } else if (mode == 2) {
                        ((View) view.findViewById(R.id.signup_root_view)).setBackgroundResource(R.drawable.paper_background_2);
                        mode++;
                    } else if (mode == 3) {
                        ((View) view.findViewById(R.id.signup_root_view)).setBackgroundResource(R.drawable.paper_4_super_minimized_background);
                        mode++;
                    } else {
                        ((View) view.findViewById(R.id.signup_root_view)).setBackgroundResource(R.drawable.paper_4_full_minus_left);
                        mode = 0;
                    }
                }
            });

            ((TextView)view.findViewById(R.id.bdTV)).setTypeface(tf);

            mName = (FloatLabeledEditTextRightSided) view.findViewById(R.id.name_entry);
            mName.setTextColor(getResources().getColor(R.color.midnight_blue),
                    getResources().getColor(R.color.hintColor),
                    getResources().getColor(R.color.accentColor), tf);

            mUserName = (FloatLabeledEditTextRightSided) view.findViewById(R.id.username_entry);
            mUserName.setTextColor(getResources().getColor(R.color.midnight_blue),
                    getResources().getColor(R.color.hintColor),
                    getResources().getColor(R.color.accentColor), tf);

            mEmail = (FloatLabeledEditTextLeftSided) view.findViewById(R.id.email_entry);
            mEmail.setTextColor(getResources().getColor(R.color.midnight_blue),
                    getResources().getColor(R.color.hintColor),
                    getResources().getColor(R.color.accentColor), tf);

            mPass = (FloatLabeledEditTextLeftSided) view.findViewById(R.id.password_entry);
            mPass.setTextColor(getResources().getColor(R.color.midnight_blue),
                    getResources().getColor(R.color.hintColor),
                    getResources().getColor(R.color.accentColor), tf);

            mrePass = (FloatLabeledEditTextLeftSided) view.findViewById(R.id.repeat_password_entry);
            mrePass.setTextColor(getResources().getColor(R.color.midnight_blue),
                    getResources().getColor(R.color.hintColor),
                    getResources().getColor(R.color.accentColor), tf);

            mPersianDatePicker = (PersianDatePicker) view.findViewById(R.id.bdate);



            submitBTN = (ButtonFlat) view.findViewById(R.id.signup_button);
            submitBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PasswordValidator passwordValidator = new PasswordValidator();
                    EmailValidator emailValidator = new EmailValidator();
                    String passStr = mPass.getEditText().getText().toString().trim();
                    String rePassStr = mrePass.getEditText().getText().toString().trim();
                    PersianCalendar pCal = mPersianDatePicker.getDisplayPersianDate();
                    int year= (new PersianCalendar()).getPersianYear();
                    if (mName.getEditText().getText().toString().isEmpty()){
                        Toast.makeText(getActivity(),"فیلد نام نمیتواند خالی بماند.",Toast.LENGTH_SHORT).show();
                        return ;
                    }else if((year-pCal.getPersianYear()) < 8 || (year<pCal.getPersianYear())){
                        Toast.makeText(getActivity(),"شما باید حداقل ۸ سال سن داشته باشید.",Toast.LENGTH_SHORT).show();
                        return ;
                    }else if (mUserName.getEditText().getText().toString().isEmpty()){
                        Toast.makeText(getActivity(),"فیلد نام کاربری نمیتواند خالی باشد.",Toast.LENGTH_SHORT).show();
                        return ;
                    }else if (!emailValidator.validate(mEmail.getEditText().getText().toString())){
                        Toast.makeText(getActivity(),"پست الکترونیکی معتبر نمیباشد!",Toast.LENGTH_SHORT).show();
                        return ;
                    }else if(!passwordValidator.validate(passStr)){
                        Toast.makeText(getActivity(),"رمز عبور باید بین شش تا بیست کاراکتر داشته باشد و شامل یک حرف کوچک و یک حرف بزرگ و یک رقم و یک کاراکتر مخصوص باشد. لطفا دوباره اقدام فرمایید.",Toast.LENGTH_LONG).show();
                        return ;
                    }else if(!passStr.equals(rePassStr)){
                        Toast.makeText(getActivity(),"خطا! رمز عبور و تکرار رمز عبور همخوانی ندارند.",Toast.LENGTH_SHORT).show();
                        return ;
                    }else
                        {
                        AsyncHttpClient myClient = new AsyncHttpClient();
                        RequestParams params = new RequestParams();

                        ////////actual///////////////
                        params.put("name", mName.getEditText().getText().toString().trim());
                        params.put("nickname", mUserName.getEditText().getText().toString().trim());
                        params.put("email", mEmail.getEditText().getText().toString().trim());
                        params.put("password", mPass.getEditText().getText().toString().trim());
                        params.put("bday", pCal.getPersianDay());
                        params.put("bmonth", pCal.getPersianMonth());
                        params.put("byear", pCal.getPersianYear());



                        myClient.post(getActivity(),"http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/registerUser",
                                params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onStart() {
                                        super.onStart();
                                        ProgressiveToast.show(getActivity(),"در حال برقراری ارتباط با سرور...");
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        try {
                                            if(response.getString("result").equals("21")){
                                                Toast.makeText(getActivity(),"ایمیل شما معتبر نمی باشد.",Toast.LENGTH_SHORT).show();
                                            } else if(response.getString("result").equals("30")){
                                                Toast.makeText(getActivity(),
                                                        "ثبت نام شما با موفقیت انجام شد. لطفا با مراجعه به ایمیل خود جهت فعال نمودن اکانتتان اقدام فرمایید.",
                                                            Toast.LENGTH_LONG).show();
                                                if(((LoginActivity)getActivity()).mLoginFragment==null)
                                                    ((LoginActivity)getActivity()).mLoginFragment = new LoginFragment();
                                                ((LoginActivity)getActivity()).getSupportFragmentManager().
                                                        beginTransaction().replace(R.id.fragment_holder,
                                                        ((LoginActivity)getActivity()).mLoginFragment).commit();
                                            }else if(response.getString("result").equals("23")){
                                                Toast.makeText(getActivity(),"ثبت نام با موفقیت انجام شد ولی ما موفق به فرستادن ایمیل فعال سازی نشدیم. لطفا با واحد پشتیبانی تماس حاصل فرمایید.",Toast.LENGTH_LONG).show();
                                            } else if(response.getString("result").equals("24")){
                                                Toast.makeText(getActivity(),"مشکلی در ثبت نام شما در سایت رخ داده است. لطفا با واحد پشتیبانی تماس حاصل نمایید.",Toast.LENGTH_LONG).show();
                                            } else if(response.getString("result").equals("25")){
                                                Toast.makeText(getActivity(),"این نام کاربری قبلا در سیستم ثبت شده است لطفا از نام کاربری دیگری استفاده نمایید.",Toast.LENGTH_LONG).show();
                                            } else if(response.getString("result").equals("26")){
                                                Toast.makeText(getActivity(),"ایمیل شما در سیستم ثبت شده است. لطفا از ایمیل دیگری استفاده نمایید..",Toast.LENGTH_LONG).show();
                                            } else if(response.getString("result").equals("27")){
                                                Toast.makeText(getActivity(),"پارامتر های ارسالی اشتباه میباشند!",Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(),"خطای نامشخص",Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                        Toast.makeText(getActivity(),"خطا در برقراری ارتباط با سرور!" + "\n کد خطا : \n" + Integer.toString(statusCode),Toast.LENGTH_SHORT).show();
                                        Log.e("Failure", Integer.toString(statusCode));
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        ProgressiveToast.dismiss();
                                    }
                                });

                    }
                }
            });

        }
    }


}
