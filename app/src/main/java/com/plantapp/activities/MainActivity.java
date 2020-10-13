package com.plantapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.plantapp.BuildConfig;
import com.plantapp.R;
import com.plantapp.data.FileUtil;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.plantapp.activities.BaseActivity.prefManager;

public class MainActivity<positonInt> extends BaseActivity implements AdapterView.OnItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    NavigationView navigationView;
    private View headerView;
    private TextView txtLoginName,txtLoginMobile;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private IntentIntegrator qrScan;
    private ImageView profile_aadhar_fornt,profile_aadhar_back;
    private String adhar_front,adhar_back;
    private boolean adhar_font_boolean,aadhar_back_boolean;
    private static final int REQUEST_CAMERA = 101;
    private static final int SELECT_FILE = 102;
    private File destination,newFile,front_aadhar_path,back_aadhar_path,profile_file;
    String[] country = { "Visitor", "Supplier", "Transport"};
    int positonInt;
    String pos;
    private Menu nav_Menu;
    private Spinner spin;
    private LinearLayout linear_supplier,linear_transport;
    private Boolean isLogin;
    private String whatsAdminMob = "7710881086", mobile="8208441436", name="imran";
    private String LoginName,LoginMobileNo;
    private EditText edt_Name,edt_vehical_name,edt_visitor_mobile,edt_visitor_adharno,
    //transport
    name_of_transport,edt_vehical_driver_name,edt_transport_mobileno,edt_tranport_adharno,edt_weightbridge,edt_tranport_lincenno,
            edt_lr,
    //supplier
    edt_name,edt_supplier_code,edt_supplier_address,edt_gross,edt_tare,edt_net,edt_remark;
    private String cus_pic,visitorName,vehicalNo,visitorMobileno,visitiorAadharno,
    //transport
    nameoftransport,transportVehicalName,transportMobileno,transportAdharno,transportWeightbridge,transportLicenno,
    tranportLR;
    //
    private Button buttonScan,buttonVisitor,btn_transport,btn_supplier;
    JSONObject obj;
    private TextView txt_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //transport
        name_of_transport = findViewById(R.id.name_of_transport);
        edt_vehical_driver_name = findViewById(R.id.edt_vehical_driver_name);
        edt_transport_mobileno = findViewById(R.id.edt_transport_mobileno);
        edt_tranport_adharno = findViewById(R.id.edt_tranport_adharno);
        edt_tranport_lincenno = findViewById(R.id.edt_tranport_lincenno);
        edt_weightbridge = findViewById(R.id.edt_weightbridge);
        btn_transport = findViewById(R.id.btn_transport);

        //visitor details
        edt_Name = findViewById(R.id.edt_Name);
        edt_vehical_name = findViewById(R.id.edt_vehical_name);
        edt_visitor_mobile = findViewById(R.id.edt_visitor_mobile);
        edt_visitor_adharno = findViewById(R.id.edt_visitor_adharno);

        txt_date = findViewById(R.id.txt_date);

        //supplier details
        edt_supplier_code = findViewById(R.id.edt_supplier_code);
        edt_name = findViewById(R.id.edt_name);
        edt_supplier_address = findViewById(R.id.edt_supplier_address);
        edt_gross = findViewById(R.id.edt_gross);
        edt_tare = findViewById(R.id.edt_tare);
        edt_net = findViewById(R.id.edt_net);
        edt_remark = findViewById(R.id.edt_remark);


        buttonScan = findViewById(R.id.buttonScan);
        buttonVisitor = findViewById(R.id.buttonVisitor);
        btn_supplier = findViewById(R.id.btn_supplier);
        btn_transport = findViewById(R.id.btn_transport);
        profile_aadhar_fornt = findViewById(R.id.profile_aadhar_fornt);
        profile_aadhar_back = findViewById(R.id.profile_aadhar_back);
        profile_aadhar_fornt.setOnClickListener(this);
        profile_aadhar_back.setOnClickListener(this);
        qrScan = new IntentIntegrator(this);
        buttonScan.setOnClickListener(this);
        buttonVisitor.setOnClickListener(this);
        btn_transport.setOnClickListener(this);
        btn_supplier.setOnClickListener(this);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        txt_date.setText(date);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //String text = "This is a test";

                    // String toNumber = "917710881086";

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + whatsAdminMob
                            + "&text=" +
                            "Vishwguru, " + "\n" +
                            "Name : " + name + "\n" +
                            "Mobile No : " + mobile + "\n"));

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);

        nav_Menu = navigationView.getMenu();


        txtLoginName = (TextView) headerView.findViewById(R.id.txtLoginName);
        txtLoginMobile = (TextView) headerView.findViewById(R.id.txtLoginMobile);

        prefManager.connectDB();
        isLogin = prefManager.getBoolean("isLogin");
        LoginName = prefManager.getString("cus_name");
        LoginMobileNo = prefManager.getString("cus_mob");
        prefManager.closeDB();


        if (isLogin) {
            txtLoginName.setText(LoginName);
            txtLoginMobile.setText(LoginMobileNo);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        } else {
            txtLoginName.setText(LoginName);
            txtLoginMobile.setText(LoginMobileNo);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
        }


        //code here for content
        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = (Spinner) findViewById(R.id.spinner);
        linear_supplier =  findViewById(R.id.linear_supplier);
        linear_transport =  findViewById(R.id.linear_transport);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //first,  we have to retrieve the item position as a string
                // then, we can change string value into integer
                String item_position = String.valueOf(position);
                positonInt = Integer.valueOf(item_position);
                //pos = String.valueOf(positonInt);
                //Toast.makeText(getApplicationContext(), "value is "+ positonInt, Toast.LENGTH_SHORT).show();
                if(positonInt == 0)
                {
                    buttonScan.setVisibility(View.GONE);
                    buttonVisitor.setVisibility(View.VISIBLE);
                    linear_supplier.setVisibility(View.GONE);
                    linear_transport.setVisibility(View.GONE);
                    //Toast.makeText(MainActivity.this, "1 value is here", Toast.LENGTH_SHORT).show();
                }else if(positonInt == 1)
                {
                    buttonScan.setVisibility(View.VISIBLE);
                    buttonVisitor.setVisibility(View.GONE);
                    //linear_supplier.setVisibility(View.VISIBLE);
                    linear_transport.setVisibility(View.GONE);
                    //Toast.makeText(MainActivity.this, "1 value is here", Toast.LENGTH_SHORT).show();
                }else if(positonInt == 2)
                {
                    buttonVisitor.setVisibility(View.GONE);
                    buttonScan.setVisibility(View.GONE);
                    linear_transport.setVisibility(View.VISIBLE);
                    linear_supplier.setVisibility(View.GONE);
                    // Toast.makeText(SpinnerCustomizationActivity.this, "2 value is here", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        if(!TextUtils.isEmpty(adhar_front)) {
            Picasso.with(MainActivity.this)
                    .load(adhar_front)
                    .placeholder(R.drawable.adhar)
                    .error(R.drawable.adhar)
                    .into(profile_aadhar_fornt);
        }if(!TextUtils.isEmpty(adhar_back)) {
            Picasso.with(MainActivity.this)
                    .load(adhar_back)
                    .placeholder(R.drawable.adhar_back)
                    .error(R.drawable.adhar_back)
                    .into(profile_aadhar_back);
        }
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(),country[position] , Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("NewApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_changeassword) {
            startActivity(new Intent(MainActivity.this,ChangePassword.class));
        }else if (id == R.id.nav_callcenter) {
            startActivity(new Intent(MainActivity.this,SupportActivity.class));
        }  else if (id == R.id.nav_logout) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.app_name));
            alertDialogBuilder.setIcon(R.drawable.logo);
            alertDialogBuilder.setMessage(getString(R.string.logout_now));
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    prefManager.connectDB();
                    prefManager.setBoolean("isLogin", false);
                    prefManager.closeDB();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Vishwguru App\nClick here https://play.google.com/store/apps/details?id=com.plantapp to download the app");
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher_round);
            alertDialogBuilder.setMessage("Are you sure to exit?");
           /* mTTS.speak("Are you sure to exit?", TextToSpeech.QUEUE_ADD, null, null);
            mTTS.setPitch((float)0.7);
            mTTS.setSpeechRate((float)0.5);*/
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    MainActivity.super.onBackPressed();
                  /*  mTTS.speak("Have a Good Day..", TextToSpeech.QUEUE_ADD, null, null);
                    mTTS.setPitch((float)0.7);
                    mTTS.setSpeechRate((float)0.5);*/
                    Toasty.success(getApplicationContext(),"Have a Good Day..",Toasty.LENGTH_SHORT).show();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


    //camera
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel" };
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            }
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);

                    destination = photoFile;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);

                }
            }
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }



    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

       /* if (resultCode == this.RESULT_CANCELED) {
            //Toasty.error(this, "Image pick cancel", Toasty.LENGTH_SHORT).show();
            return;
        } else {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE) {
                    try {
                        destination = FileUtil.from(MainActivity.this,data.getData());
                        newFile = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(50)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    try {
                        newFile = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(50)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                .compressToFile(destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(adhar_font_boolean){
                    Picasso.with(MainActivity.this)
                            .load(destination)
                            .placeholder(R.drawable.pic)
                            .error(R.drawable.pic)
                            .into(profile_aadhar_fornt);
                    profile_file = newFile;
                }

    }
*/
             if (result != null) {
                //if qrcode has nothing in it
                if (result.getContents() == null) {
                    Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                } else {
                    //if qr contains data
                    try {
                        linear_supplier.setVisibility(View.VISIBLE);
                        linear_transport.setVisibility(View.GONE);
                        //converting the data to json
                        Toast.makeText(this, "data fetched", Toast.LENGTH_LONG).show();
                        String ok = result.getContents();
                        System.out.println("value here "+ok);
                        obj = new JSONObject(result.getContents());
                        edt_name.setText(obj.getString("name"));
                        edt_supplier_code.setText(obj.getString("code"));
                        edt_supplier_address.setText(obj.getString("address"));

                        //setting values to textviewskdjfkl

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //if control comes here
                        //that means the encoded format not matches
                        //in this case you can display whatever data is available on the qrcode
                        //to a toast
                        Toast.makeText(this, "data fetched", Toast.LENGTH_LONG).show();
                        String ok = result.getContents();
                        System.out.println("value here "+ok);


                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
}
//onclick
@Override
public void onClick(View view) {
    switch (view.getId()){
        case R.id.buttonVisitor:
            visitorName = edt_Name.getText().toString().trim();
            vehicalNo = edt_vehical_name.getText().toString().trim();
            visitorMobileno = edt_visitor_mobile.getText().toString().trim();
            visitiorAadharno = edt_visitor_adharno.getText().toString().trim();
            MultipartBody.Part cusprofilepic = null;

            if (!isNetworkAvailable()) {
                Toasty.error(getApplicationContext(), getResources().getString(R.string.error_msg_no_internet), Toasty.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(visitorName)) {
                Toasty.error(getApplicationContext(), "enter visitor name", Toasty.LENGTH_SHORT).show();
                return;
            } if (TextUtils.isEmpty(vehicalNo)) {
            Toasty.error(getApplicationContext(), "enter vehical number", Toasty.LENGTH_SHORT).show();
            return;
        }if (TextUtils.isEmpty(visitorMobileno)) {
            Toasty.error(getApplicationContext(), "enter visitor's mobile number", Toasty.LENGTH_SHORT).show();
            return;
        }if (visitorMobileno.length() < 10) {
            Toasty.error(getApplicationContext(), getResources().getString(R.string.entervalidmobileno), Toasty.LENGTH_SHORT).show();
            return;
        }if (String.valueOf(visitorMobileno.charAt(0)).equals("0") ||
                    String.valueOf(visitorMobileno.charAt(0)).equals("1") ||
                    String.valueOf(visitorMobileno.charAt(0)).equals("2") ||
                    String.valueOf(visitorMobileno.charAt(0)).equals("3") ||
                    String.valueOf(visitorMobileno.charAt(0)).equals("4") ||
                    String.valueOf(visitorMobileno.charAt(0)).equals("5") ||
                    String.valueOf(visitorMobileno.charAt(0)).equals("6")) {
                Toasty.error(getApplicationContext(), getResources().getString(R.string.entervalidmobileno), Toasty.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(visitiorAadharno)) {
            Toasty.error(getApplicationContext(), "enter visitor's aadhar number", Toasty.LENGTH_SHORT).show();
            return;
        }
            try{
                profile_file.isFile();
                RequestBody requestFileProfile =RequestBody.create(MediaType.parse("*/*"), profile_file);
                cusprofilepic = MultipartBody.Part.createFormData("cus_profile", profile_file.getName(), requestFileProfile);
            }catch (NullPointerException e){
                // cus_pic = cus_pic.replace("http://swack.in/swack/ProfileCus/","");
                cus_pic = cus_pic.replace("http://swack.in/swack/UploadDocs/","");
            }
            break;

        case R.id.profile_aadhar_fornt:
            adhar_font_boolean = true;
            aadhar_back_boolean = false;
            selectImage();
            break;

        case R.id.profile_aadhar_back:
            adhar_font_boolean = false;
            aadhar_back_boolean = true;
            selectImage();
            break;
    }

    qrScan.initiateScan();

}



}