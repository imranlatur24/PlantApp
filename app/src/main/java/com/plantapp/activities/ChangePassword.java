package com.plantapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;


import com.plantapp.R;
import com.plantapp.data.APIService;
import com.plantapp.data.APIUrl;
import com.plantapp.model.ResponseResult;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePassword extends BaseActivity implements View.OnClickListener {
    private String customer_id,old_password,new_password,comfrim_password;
    private static final String TAG = "ChangePassword";
    private EditText edtNewPassword,edtConfirmPasswordC;
    private Button btnChangePassword;
    private Toolbar toolbar;
  //  private TextToSpeech mTTS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        getSupportActionBar().setTitle(R.string.change_passord);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

    }

    private void init()
    {

        prefManager.connectDB();
        customer_id = prefManager.getString("cus_id");
        prefManager.closeDB();

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPasswordC = findViewById(R.id.edtConfirmPasswordC);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);

        //tts
       /* mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTS.setLanguage(new Locale("en","IN"));
            }
        });*/
    }

    private void changePassword(String customer_id, String new_password) {
        //defining a progress dialog to show while signing up
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Changing password...");
        progressDialog.show();


        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);


        //defining the call
        System.out.println("#Customer id : "+customer_id+" Password : "+new_password);
        Call<ResponseResult> call = service.callChangePassword(APIUrl.KEY,customer_id,new_password
        );

        call.enqueue(new Callback<ResponseResult>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                progressDialog.dismiss();
                try{
                    System.out.println(TAG + "& Response Change Password" + response.body().getResponse());
                    if (Integer.parseInt(response.body().getResponse()) == 101) {
                        prefManager.connectDB();
                        prefManager.setBoolean("isLogin",false);
                        prefManager.closeDB();
                        /*mTTS.speak("Password changed successfully" , TextToSpeech.QUEUE_ADD, null, null);
                        mTTS.setPitch((float)0.8);
                        mTTS.setSpeechRate((float)0.2);*/
                        Toasty.success(getApplicationContext(), "Password changed successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePassword.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else if (Integer.parseInt(response.body().getResponse()) == 102) {
                        Toasty.error(getApplicationContext(),"Mobile No Already Exist, Please Try Another Mobile Number", Toasty.LENGTH_LONG).show();
                    }
                }catch (NullPointerException e)
                {
                    Toasty.error(getApplicationContext(),"Password change failed, try again", Toasty.LENGTH_SHORT).show();
                }
                catch (NumberFormatException e)
                {
                    Toasty.error(getApplicationContext(),"Password change failed, try again", Toasty.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: "+t.getMessage());
                Toasty.error(getApplicationContext(), t.getMessage(), Toasty.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnChangePassword:
                new_password = edtNewPassword.getText().toString().trim();
                comfrim_password = edtConfirmPasswordC.getText().toString().trim();

                if(TextUtils.isEmpty(new_password)){
                    Toasty.error(ChangePassword.this,"Enter New Password", Toasty.LENGTH_SHORT).show();
                    return;
                }
                /*if(new_password.length() < 6){
                    Toasty.error(ChangePassword.this,"New Password Is Less Than 6 Letters", Toasty.LENGTH_SHORT).show();
                    return;
                }*/
                if(TextUtils.isEmpty(comfrim_password)){
                    Toasty.error(ChangePassword.this,"Enter Confirm Password", Toasty.LENGTH_SHORT).show();                    return;
                }
                if(!comfrim_password.equals(new_password)){
                    Toasty.error(this, "New password and confirm password does not match", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!isNetworkAvailable()) {
                    Toasty.error(this, "No internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    changePassword(customer_id, new_password);
                }
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
