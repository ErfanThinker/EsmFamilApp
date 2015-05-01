package net.crowmaster.esmfamil.fragments;

import android.content.Context;
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
import net.crowmaster.esmfamil.activities.MainActivity;
import net.crowmaster.esmfamil.util.PasswordValidator;
import net.crowmaster.esmfamil.util.ProgressiveToast;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 4/28/15.
 */
public class EditProfileFragment extends Fragment {
    FloatLabeledEditTextRightSided mName;
    FloatLabeledEditTextLeftSided mrePass;
    FloatLabeledEditTextLeftSided mPass;
    FloatLabeledEditTextLeftSided mOldPass;
    ButtonFlat mEditNameSubmit;
    ButtonFlat mChangePassSubmit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_profile_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Typeface tf = Typeface.createFromAsset(getResources().getAssets(),
                "bnazanin.ttf");
        mName = (FloatLabeledEditTextRightSided) view.findViewById(R.id.name_entry);
        mName.setTextColor(getResources().getColor(R.color.midnight_blue),
                getResources().getColor(R.color.hintColor),
                getResources().getColor(R.color.accentColor), tf);

        mOldPass = (FloatLabeledEditTextLeftSided) view.findViewById(R.id.old_password_entry);
        mOldPass.setTextColor(getResources().getColor(R.color.midnight_blue),
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

        mEditNameSubmit = (ButtonFlat) view.findViewById(R.id.edit_name_submit);

        mChangePassSubmit = (ButtonFlat) view.findViewById(R.id.change_pass_submit);

        mEditNameSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mName.getEditText().getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(),"فیلد 'نام و نام خانوادگی' نمیتواند خالی باشد.",
                            Toast.LENGTH_SHORT).show();
                }else {
                    AsyncHttpClient client = new AsyncHttpClient();
                    PersistentCookieStore mCookieStore = new PersistentCookieStore(getActivity());
                    client.setCookieStore(mCookieStore);
                    RequestParams params = new RequestParams();
                    params.put("name",mName.getEditText().getText().toString().trim());
                    client.post(getActivity(),"http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/editUser",
                            params, new JsonHttpResponseHandler(){
                                @Override
                                public void onStart() {
                                    super.onStart();
                                    ProgressiveToast.show(getActivity(),"در حال ارسال اطلاعات...");
                                }

                                @Override
                                public void onFinish() {
                                    super.onFinish();
                                    ProgressiveToast.dismiss();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    Log.e("EditProfileSuccess",response.toString());
                                    try {
                                        if(response.getString("result").equals("27")){
                                            Toast.makeText(getActivity(),"پارامتر های ارسالی اشتباه هستند!",Toast.LENGTH_SHORT).show();
                                        } else if(response.getString("result").equals("34")){
                                            Toast.makeText(getActivity(),"لطفا از حساب کاربری خارج شده و دوباره اقدام به ورود و عملیات نمایید.",Toast.LENGTH_SHORT).show();
                                        } else if(response.getString("result").equals("30")){
                                            Toast.makeText(getActivity(),"عملیات با موفقیت انجام شد!",Toast.LENGTH_SHORT).show();
                                            getActivity().getSharedPreferences("userCredits", Context.MODE_PRIVATE).edit().
                                                    putString("name",mName.getEditText().getText().toString().trim()).commit();
                                            ((MainActivity)getActivity()).refreshUserInfo();
                                        } else if(response.getString("result").equals("33")){
                                            Toast.makeText(getActivity(),"مایه ی شرمندگیه ولی نشد که بشه!",Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(),"خطای نامشخص",Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e("ExceptionInEditProfile",e.getMessage());
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(getActivity(),"خطا در برقراری ارتباط با سرور.",Toast.LENGTH_SHORT).show();
                                    Log.e("Failure", Integer.toString(statusCode));
                                }
                            });
                }
            }
        });

        mChangePassSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordValidator mPasswordValidator = new PasswordValidator();
                if(!mPass.getEditText().getText().toString().trim().equals(
                        mrePass.getEditText().getText().toString().trim())){
                    Toast.makeText(getActivity(),"خطا! رمز عبور جدید با تکرار رمز عبور جدید مطابقت ندارد. لطفا پس از تصحیح دوباره اقدام فرمایید.",Toast.LENGTH_LONG).show();
                } else if(mPass.getEditText().getText().toString().trim().isEmpty() ||
                        mrePass.getEditText().getText().toString().trim().isEmpty() ||
                        mOldPass.getEditText().getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(),"پر کردن هر سه فیلد اجباری میباشد!",Toast.LENGTH_SHORT).show();

                } else if(!mPasswordValidator.validate(mPass.getEditText().getText().toString().trim())){
                    Toast.makeText(getActivity(),"رمز عبور باید بین شش تا بیست کاراکتر داشته باشد و شامل یک حرف کوچک و یک حرف بزرگ و یک رقم و یک کاراکتر مخصوص باشد. لطفا دوباره اقدام فرمایید.",Toast.LENGTH_SHORT).show();

                } else {
                    AsyncHttpClient mClient = new AsyncHttpClient();
                    PersistentCookieStore mCookie = new PersistentCookieStore(getActivity());
                    mClient.setCookieStore(mCookie);
                    RequestParams params = new RequestParams();
                    params.put("oldPassword",mOldPass.getEditText().getText().toString().trim());
                    params.put("newPassword",mPass.getEditText().getText().toString().trim());
                    mClient.post(getActivity(),"http://www.namefamily.ir/EsmFamil/CodeIgniter_2.2.0/index.php/changePassword",
                            params, new JsonHttpResponseHandler(){
                                @Override
                                public void onStart() {
                                    super.onStart();
                                    ProgressiveToast.show(getActivity(),"در حال ارسال اطلاعات...");
                                }

                                @Override
                                public void onFinish() {
                                    super.onFinish();
                                    ProgressiveToast.dismiss();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    Log.e("EditProfileSuccess",response.toString());
                                    try {
                                        if(response.getString("result").equals("27")){
                                            Toast.makeText(getActivity(),"پارامتر های ارسالی اشتباه هستند!",Toast.LENGTH_SHORT).show();
                                        } else if(response.getString("result").equals("34")){
                                            Toast.makeText(getActivity(),"لطفا از حساب کاربری خارج شده و دوباره اقدام به ورود و عملیات نمایید.",Toast.LENGTH_SHORT).show();
                                        } else if(response.getString("result").equals("30")){
                                            Toast.makeText(getActivity(),"رمز عبور با موفقیت تغییر کرد.",Toast.LENGTH_SHORT).show();
                                            getActivity().getSharedPreferences("userCredits", Context.MODE_PRIVATE).edit().
                                                    putString("password",mPass.getEditText().getText().toString().trim()).commit();
                                        } else if(response.getString("result").equals("36")){
                                            Toast.makeText(getActivity(),"مایه ی شرمندگیه ولی نشد که بشه!",Toast.LENGTH_SHORT).show();
                                        } else if(response.getString("result").equals("37")){
                                            Toast.makeText(getActivity(),"رمز عبور قبلی اشتباه وارد شده است.",Toast.LENGTH_SHORT).show();
                                        } else{
                                            Toast.makeText(getActivity(),"خطای نامشخص",Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.e("ExceptionInEditProfile",e.getMessage());
                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    Toast.makeText(getActivity(),"خطا در برقراری ارتباط با سرور.",Toast.LENGTH_SHORT).show();
                                    Log.e("Failure", Integer.toString(statusCode));

                                }
                            });
                }

            }
        });
    }
}
