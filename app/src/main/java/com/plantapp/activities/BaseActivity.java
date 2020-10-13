package com.plantapp.activities;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plantapp.R;
import com.plantapp.data.APIUrl;
import com.plantapp.data.SharedPreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;


public class BaseActivity extends AppCompatActivity {
	
	public static SharedPreferenceManager prefManager;
	public DisplayMetrics metrices;
	//public APIService apiService;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		prefManager = new SharedPreferenceManager(this);
		metrices = getResources().getDisplayMetrics();
		//apiService = APIUrl.getClient().create(APIService.class);
	}
	public void errorOut(Throwable t){
		if(!isNetworkAvailable()){
			Toasty.error(getApplicationContext(), getResources().getString(R.string.error_msg_no_internet), Toasty.LENGTH_LONG).show();
		}else {
			Toasty.error(getApplicationContext(), "Hi "+"\n"+getResources().getString(R.string.create_new) +Toasty.LENGTH_LONG).show();
		}
	}
	public  boolean isNetworkAvailable()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static String getDateText(String timestamp)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String date = format.format(new Date(Long.parseLong(timestamp)));
		String str = date;
		return str;
	}

	public String parseDate(String time) {
		String inputPattern = "yyyy-MM-dd HH:mm:ss";
		String outputPattern = "MMM dd,yyyy hh:mm a";
		SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
		SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

		Date date = null;
		String str = null;

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

}
