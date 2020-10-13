package com.plantapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.plantapp.R;
import com.plantapp.data.APIService;
import com.plantapp.data.APIUrl;
import com.plantapp.data.GPSTracker;
import com.plantapp.model.CityListModel;
import com.plantapp.model.DistrictModel;
import com.plantapp.model.ResponseResult;
import com.plantapp.model.StateList;
import com.plantapp.model.TalukaListModel;
import com.plantapp.view.TextInputAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Registration extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = "Registration";
    private Button btn_register,btn_Login;
    private EditText edtAddress,edtUsername,edtMobile,edtEmail,edtPassword,edtConfirmPassword;
    private APIService apiService;
    private ProgressDialog progressDialog1, progressDialog2, progressDialog3, progressDialog4;
    private String username,mobile,email,password,confirmpassword,address;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private Pattern pattern;
    private Matcher matcher;
   // private TextToSpeech mTTS;
    //update location
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 1000; /* 2 sec */
    private LocationManager locationManager;
    public Geocoder geocoder;
    private ProgressDialog progressDialog;
    public String lati, longi;
    public GPSTracker gps;
    public TextView result, latitudeLL, longitudeLL, textView_CustomerName, textView_CustomerEmail;
    public double latitude;
    public double longitude;
    private static SwipeRefreshLayout swipeContainer;
    private List<Address> addressList;


    private ArrayList<StateList> stateLists;
    private ArrayList<CityListModel.CityList> cityLists;
    private ArrayList<TalukaListModel.TalukaList> talukaLists;
    private ArrayList<String> stateName,cityName,talukaName;
    private String statename,cityname,talukaname;
    private TextInputAutoCompleteTextView textinput_state,textinput_city,textinput_taluka;
    private String state,taluka,city;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        init();
        getState();
        //requestPermission();
        //checkPermission();
       // getState();
        /*gps = new GPSTracker(Registration.this,Registration.this);
        gps.showSettingsAlert();*/
    }

    private void init() {

        if (ContextCompat.checkSelfPermission(Registration.this,
                Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED

                && ContextCompat.checkSelfPermission(Registration.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Registration.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        geocoder = new Geocoder(this, Locale.getDefault());
        result = findViewById(R.id.resultmain);
        latitudeLL = findViewById(R.id.latitudemain);
        longitudeLL = findViewById(R.id.longitudemain);


        apiService = APIUrl.getClient().create(APIService.class);
        pattern = Pattern.compile(EMAIL_PATTERN);
        btn_register = findViewById(R.id.btn_register);
        btn_Login = findViewById(R.id.btn_Login);
        edtUsername = findViewById(R.id.edtUsername);
        edtAddress = findViewById(R.id.edtAddress);
        edtMobile = findViewById(R.id.edtMobile);
        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    checkMobileNumber(String.valueOf(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtAddress = findViewById(R.id.edtAddress);

        btn_register.setOnClickListener(this);

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this,LoginActivity.class));
            }
        });

        //TTs
        /*mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTS.setLanguage(new Locale("en","IN"));
            }
        });*/

        textinput_state = findViewById(R.id.textinput_state);
        textinput_city = findViewById(R.id.textinput_city);
        textinput_taluka = findViewById(R.id.textinput_taluka);
        textinput_city.setOnClickListener(this);
        textinput_state.setOnClickListener(this);
        textinput_taluka.setOnClickListener(this);

        textinput_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < stateLists.size(); i++) {
                    if (textinput_state.getText().toString().equals(stateLists.get(i).getState_name())) {
                        state = stateLists.get(i).getState_id();
                        //edit_state.setText("");
                        Registration.this.getCity(state);
                        //spinner_vehicle.setText("");
                        //state = "";
                        //taluka = ""
                        System.out.println("#state Id " + state);
                        //Toast.makeText(gpsTracker, "Vehical Id "+vehicle, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        textinput_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < cityLists.size(); i++) {
                    if (textinput_city.getText().toString().equals(cityLists.get(i).getCity_name())) {
                        city = cityLists.get(i).getCity_id();
                        //edit_state.setText("");
                        Registration.this.getTaluka(city);
                        //spinner_vehicle.setText("");
                        //state = "";
                        //taluka = ""
                        System.out.println("#city Id " + city);
                        //Toast.makeText(gpsTracker, "Vehical Id "+vehicle, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        textinput_taluka.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < talukaLists.size(); i++) {
                    if (textinput_taluka.getText().toString().equals(talukaLists.get(i).gettaluka_name())) {
                        taluka = talukaLists.get(i).gettaluka_id();
                        //edit_state.setText("");
                        // getTaluka(taluka);
                        //spinner_vehicle.setText("");
                        //state = "";
                        //taluka = ""
                        System.out.println("#taluka Id " + taluka);
                        //Toast.makeText(gpsTracker, "Vehical Id "+vehicle, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        if (ContextCompat.checkSelfPermission(Registration.this,
                Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED

                && ContextCompat.checkSelfPermission(Registration.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Registration.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

    }

    private void checkMobileNumber(final String strMobile) {
        //defining a progress dialog to show while signing up
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();
        progressDialog.setCancelable(false);

        //calling the api
        callCheckMobileNumber(strMobile).enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                //hiding progress dialog
                progressDialog.dismiss();

                try {
                    System.out.println("#response otp " + response.body().getResponse());
                    if (Integer.parseInt(response.body().getResponse()) == 101) {
                        progressDialog.dismiss();
                        System.out.println("#OTP Response : "+response.body().getUserMsgList());

                        // Toasty.success(getApplicationContext(), getResources().getString(R.string.you_will_receive_otp), Toasty.LENGTH_LONG).show();
                         //dialogBoxOTP();

                    } else if (Integer.parseInt(response.body().getResponse()) == 102) {
                        progressDialog.dismiss();
                        System.out.println("#OTP Response : "+response.body().getUserMsgList());
                        Toasty.success(getApplicationContext(), getResources().getString(R.string.useralreadyexist), Toasty.LENGTH_LONG).show();
                        Intent intent = new Intent(Registration.this,LoginActivity.class);
                        intent.putExtra("mobno",strMobile);
                        startActivity(intent);
                        finish();

                        //registerMobile.setText("");
                        //Toasty.error(getApplicationContext(), getResources().getString(R.string.requiredparameter), Toasty.LENGTH_LONG).show();
                    } else if (Integer.parseInt(response.body().getResponse()) == 105) {
                        progressDialog.dismiss();
                        System.out.println(TAG + " Invalid Mobile no");
                        edtMobile.setText("");
                        Toasty.error(getApplicationContext(),"Server Getting Down, please try after sometime", Toasty.LENGTH_LONG).show();
                    } else {
                        progressDialog.dismiss();
                        System.out.println(TAG + " Else Close");
                        edtMobile.setText("");
                        Toasty.error(getApplicationContext(),"Server Getting Down, please try after sometime", Toasty.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    edtMobile.setText("");
                    Toasty.error(getApplicationContext(),"Server Getting Down, please try after sometime", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable throwable) {
                progressDialog.dismiss();
                edtMobile.setText("");
                if (!isNetworkAvailable()) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.error_msg_no_internet), Toasty.LENGTH_LONG).show();
                } else if (throwable instanceof TimeoutException) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.error_msg_timeout), Toasty.LENGTH_LONG).show();
                }
            }
        });
    }

    private Call<ResponseResult> callCheckMobileNumber(String strMobile) {
        System.out.println("KEY MOBILE VERIFICATION "+ APIUrl.KEY+" Mobile "+strMobile);
        return apiService.mobileVerification(
                APIUrl.KEY,
                strMobile
        );
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //spinners
    //state spinner
    private void getState() {
        //defining a progress dialog to show while signing up
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        stateLists = new ArrayList<>();

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);


        //defining the call
        Call<DistrictModel> call = service.getState(APIUrl.KEY);

        //calling the api
        call.enqueue(new Callback<DistrictModel>() {
            @Override
            public void onResponse(Call<DistrictModel> call, Response<DistrictModel> response) {
                //hiding progress dialog
                progressDialog.dismiss();

                if (Integer.parseInt(response.body().getResponse()) == 101) {
                    //Toast.makeText(Registration.this, "#state response" + response.body().getResponse(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < response.body().getDistrict_list().size(); i++) {
                        stateLists.add(new StateList(response.body().getDistrict_list().get(i).getState_id(),
                                response.body().getDistrict_list().get(i).getState_name()));
                    }
                    stateSpinner(stateLists);
                } else if (Integer.parseInt(response.body().getResponse()) == 102) {
                    stateSpinner(stateLists);
                } else if (Integer.parseInt(response.body().getResponse()) == 103) {
                    stateSpinner(stateLists);
                    System.out.println(TAG + " Required Parameter Missing");
                } else if (Integer.parseInt(response.body().getResponse()) == 104) {
                    stateSpinner(stateLists);
                    System.out.println(TAG + " Invalid Key");
                } else if (Integer.parseInt(response.body().getResponse()) == 105) {
                    stateSpinner(stateLists);
                    System.out.println(TAG + " Login failed");
                } else if (Integer.parseInt(response.body().getResponse()) == 106) {
                    stateSpinner(stateLists);
                    System.out.println(TAG + " Page Not Found");
                } else {
                    stateSpinner(stateLists);
                    System.out.println(TAG + " Else Close");
                }
            }

            @Override
            public void onFailure(Call<DistrictModel> call, Throwable t) {
                progressDialog.dismiss();
                stateSpinner(stateLists);
                errorOut(t);
            }
        });
    }

    private void stateSpinner(ArrayList<StateList> vehicleLists) {
        stateName = new ArrayList<>();
        for (StateList data : vehicleLists) {
            stateName.add(data.getState_name());
            if (data.getState_id().equals(stateName)) {
                textinput_state.setText(data.getState_name());
                getCity(state);
                //Toast.makeText(this, "#State spinner Id "+stateName, Toast.LENGTH_SHORT).show();
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, stateName);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
        textinput_state.setThreshold(1);
        // attaching data adapter to spinner
        textinput_state.setAdapter(dataAdapter);
    }


    private void getCity(String state) {
        //defining a progress dialog to show while signing up
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        cityLists = new ArrayList<>();

        apiService.getCity(APIUrl.KEY,state).enqueue(new Callback<CityListModel>() {
            @Override
            public void onResponse(Call<CityListModel> call, Response<CityListModel> response) {
                //hiding progress dialog
                progressDialog.dismiss();
                System.out.println(TAG+"& Response Cities"+ response.body().getResponse());
                if(Integer.parseInt(response.body().getResponse()) == 101) {
                    cityLists = response.body().getCities_list();
                    citySpinner(cityLists);
                }else if(Integer.parseInt(response.body().getResponse()) == 102){
                    citySpinner(cityLists);
                }else if(Integer.parseInt(response.body().getResponse()) == 103){
                    citySpinner(cityLists);
                    System.out.println(TAG+ " Required Parameter Missing");
                }else if(Integer.parseInt(response.body().getResponse()) == 104){
                    citySpinner(cityLists);
                    System.out.println(TAG+ " Invalid Key");
                }else if(Integer.parseInt(response.body().getResponse()) == 105){
                    citySpinner(cityLists);
                    System.out.println(TAG+ " Login failed");
                }else if(Integer.parseInt(response.body().getResponse()) == 106){
                    citySpinner(cityLists);
                    System.out.println(TAG+ " Page Not Found");
                }else {
                    citySpinner(cityLists);
                    System.out.println(TAG+ " Else Close");
                }
            }

            @Override
            public void onFailure(Call<CityListModel> call, Throwable t) {
                progressDialog.dismiss();
                citySpinner(cityLists);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void citySpinner(ArrayList<CityListModel.CityList> vehicleLists) {
        cityName = new ArrayList<>();
        for (CityListModel.CityList data : vehicleLists) {
            cityName.add(data.getCity_name());
            if (data.getCity_id().equals(cityName)) {
                textinput_city.setText(data.getCity_name());
                getTaluka(city);
                System.out.println("& city "+city);
               // Toast.makeText(this, "#city Id "+cityName, Toast.LENGTH_SHORT).show();
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, cityName);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
        textinput_city.setThreshold(1);
        // attaching data adapter to spinner
        textinput_city.setAdapter(dataAdapter);
    }

    //taluka
    private void getTaluka(String city_id) {
        //defining a progress dialog to show while signing up
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        talukaLists = new ArrayList<>();

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);


        //defining the call
        Call<TalukaListModel> call = service.getTaluka(APIUrl.KEY, city_id);

        //calling the api
        call.enqueue(new Callback<TalukaListModel>() {
            @Override
            public void onResponse(Call<TalukaListModel> call, Response<TalukaListModel> response) {
                //hiding progress dialog
                progressDialog.dismiss();

                System.out.println("& Response " + response.body().getResponse());
                if (Integer.parseInt(response.body().getResponse()) == 101) {
                    // Toast.makeText(Registration.this, "taluka response" + response.body().getResponse(), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < response.body().gettaluka_list().size(); i++) {

                        talukaLists.add(new TalukaListModel.TalukaList(response.body().gettaluka_list().get(i).gettaluka_id(),
                                response.body().gettaluka_list().get(i).gettaluka_name(),
                                response.body().gettaluka_list().get(i).getcity_id()));
                    }
                    talukaSpinner(talukaLists);
                } else if (Integer.parseInt(response.body().getResponse()) == 102) {
                    talukaSpinner(talukaLists);
                } else if (Integer.parseInt(response.body().getResponse()) == 103) {
                    talukaSpinner(talukaLists);
                    System.out.println(TAG + " Required Parameter Missing");
                } else if (Integer.parseInt(response.body().getResponse()) == 104) {
                    talukaSpinner(talukaLists);
                    System.out.println(TAG + " Invalid Key");
                } else if (Integer.parseInt(response.body().getResponse()) == 105) {
                    talukaSpinner(talukaLists);
                    System.out.println(TAG + " Login failed");
                } else if (Integer.parseInt(response.body().getResponse()) == 106) {
                    talukaSpinner(talukaLists);
                    System.out.println(TAG + " Page Not Found");
                } else {
                    talukaSpinner(talukaLists);
                    System.out.println(TAG + " Else Close");
                }
            }

            @Override
            public void onFailure(Call<TalukaListModel> call, Throwable t) {
                progressDialog.dismiss();
                talukaSpinner(talukaLists);
                errorOut(t);
            }
        });
    }
    private void talukaSpinner(ArrayList<TalukaListModel.TalukaList> serviceLists) {
        talukaName = new ArrayList<>();
        for (TalukaListModel.TalukaList data : serviceLists) {
            talukaName.add(data.gettaluka_name());
            if (data.gettaluka_id().equals(talukaName)) {
                textinput_taluka.setText(data.gettaluka_name());
                //getState(country);
                //Toast.makeText(this, "#service ddd Id "+talukaName, Toast.LENGTH_SHORT).show();
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, talukaName);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
        textinput_taluka.setThreshold(1);
        // attaching data adapter to spinner
        textinput_taluka.setAdapter(dataAdapter);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_register:

                username = edtUsername.getText().toString().trim();
                address = edtAddress.getText().toString().trim();
                mobile = edtMobile.getText().toString().trim();
                email = edtEmail.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                confirmpassword = edtConfirmPassword.getText().toString().trim();

                if (!isNetworkAvailable()) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.error_msg_no_internet), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.enterusername), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mobile)) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.entermobile), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (mobile.length() < 10) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.entervalidmobileno), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (String.valueOf(mobile.charAt(0)).equals("0") ||
                        String.valueOf(mobile.charAt(0)).equals("1") ||
                        String.valueOf(mobile.charAt(0)).equals("2") ||
                        String.valueOf(mobile.charAt(0)).equals("3") ||
                        String.valueOf(mobile.charAt(0)).equals("4") ||
                        String.valueOf(mobile.charAt(0)).equals("5") ||
                        String.valueOf(mobile.charAt(0)).equals("6")) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.entervalidmobileno), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toasty.error(this,getResources().getString(R.string.enter_email), Toasty.LENGTH_SHORT).show();
                    return;
                } if (email.length() > 1) {
                     matcher = pattern.matcher(email);
                    if (!matcher.matches())
                    {
                    Toasty.error(this, getResources().getString(R.string.enter_valid_email), Toasty.LENGTH_SHORT).show();
                    return;
                    }
                }
                if (TextUtils.isEmpty(password)) {
                    //registerCPassword.setError("Enter Confirm Password");
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.enterpassword), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    // registerPassword.setError("Enter Password should not be less than 6 character");
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.enterpasswordvalidation), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmpassword)) {
                    //registerCPassword.setError("Enter Confirm Password");
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.enterconfirmpassword), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmpassword)) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.oldnewpasswordnotmatch), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(address)) {
                    // registerMobile.setError("Enter Mobile no.");
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.enteraddress), Toasty.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(state)) {
                    // registerMobile.setError("Enter Mobile no.");
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.enterstate), Toasty.LENGTH_SHORT).show();
                    return;
                } if (TextUtils.isEmpty(city)) {
                // registerMobile.setError("Enter Mobile no.");
                Toasty.error(getApplicationContext(), getResources().getString(R.string.entercity), Toasty.LENGTH_SHORT).show();
                return;
            } if (TextUtils.isEmpty(taluka)) {
                // registerMobile.setError("Enter Mobile no.");
                Toasty.error(getApplicationContext(), getResources().getString(R.string.entertaluka), Toasty.LENGTH_SHORT).show();
                return;
            }
                register(username,email,mobile,confirmpassword,address,city,taluka,state);
                break;
        }
    }

    private void register(String username, String email, String mobile, String confirmpassword, String address, String city, String taluka, String state) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        callRegisterService(username,email,mobile,confirmpassword,address,city,taluka,state).enqueue(new Callback<ResponseResult>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                progressDialog.dismiss();
                Log.d(TAG, "response registration : " + response.body().getResponse());
                if (Integer.parseInt(response.body().getResponse()) == 101) {
                  /*  mTTS.speak("Registration  Done Successfully.." , TextToSpeech.QUEUE_ADD, null, null);
                    mTTS.setPitch((float)0.8);
                    mTTS.setSpeechRate((float)0.2);*/
                    Toasty.success(getApplicationContext(), getResources().getString(R.string.registrationdone), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Registration.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else if (Integer.parseInt(response.body().getResponse()) == 102) {
                    progressDialog.dismiss();
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.userexist), Toast.LENGTH_LONG).show();
                }  else {
                    progressDialog.dismiss();
                    System.out.println(TAG + " Else Close");
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.registrationfail), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                progressDialog.dismiss();
                errorOut(t);
            }
        });
    }

    private Call<ResponseResult>
    callRegisterService(final String username, final String email, final String mobile , final String confirmpassword,
                        final String address, final String city, final String taluka, final String state) {
        System.out.println("Username : "+username+" Email : "+" CPassword : "+confirmpassword+email+"Mobile : "+mobile+
                " Address : "+address+"statename"+statename+" Latitude "+latitude+" Longitude"+longitude+"city : "+city+" taluka :"+taluka+" state : "+state);
   /*     Toast.makeText(this, "state id : "+state, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "city id : "+city, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "taluka id : "+taluka, Toast.LENGTH_SHORT).show();
  */      return apiService.callRegister(
                APIUrl.KEY,
                username,
                email,
                mobile,
                confirmpassword,
                address,
                String.valueOf(latitude),
                String.valueOf(longitude),
                city,taluka,state
        );
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Registration.this, new
                String[]{CALL_PHONE, READ_SMS,
                WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 1);
    }

    public boolean checkPermission() {

        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);
        int result10 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result11 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED
                && result5 == PackageManager.PERMISSION_GRANTED
                && result10 == PackageManager.PERMISSION_GRANTED
                && result11 == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location is detedcting..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        System.out.println("#Updated Location in each second"+msg);
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
         //Toast.makeText(getApplicationContext(), ""+latLng, Toast.LENGTH_SHORT).show();
         LatLng latlang = latLng;

         latitude  = location.getLatitude();
         longitude = location.getLongitude();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(Registration.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                     //   Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                   // Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
