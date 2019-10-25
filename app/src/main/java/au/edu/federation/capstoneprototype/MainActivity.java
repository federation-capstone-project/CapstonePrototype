package au.edu.federation.capstoneprototype;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import au.edu.federation.capstoneprototype.Base.CalItem;
import au.edu.federation.capstoneprototype.Base.Class;
import au.edu.federation.capstoneprototype.Base.ClassOffline;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences prefs;
    public Boolean canConnect;
    public Boolean noInternet;
    public static MainActivity instance;
    public boolean open;
    OffineDatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        instance = this;
        prefs = this.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        db = new OffineDatabaseHandler(this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        prefs = this.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        TextView student_name = headerView.findViewById(R.id.tv_student_name);
        TextView student_email = headerView.findViewById(R.id.tv_student_email);
        student_name.setText(prefs.getString("student_name", "Test Student"));
        student_email.setText(prefs.getString("student_email", "student@test.com"));
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_class);
        OfflineClassesCheck();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new SendfeedbackJob().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Fragment fragment = null;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            fragment = new SettingFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_class:
                fragment = new ClassFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingFragment();
                break;
            case R.id.nav_schedule:
                fragment = new ClassScheduleFragment();
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                break;
            case R.id.nav_details:
                fragment = new DetailFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void OfflineClassesCheck(){
        List<ClassOffline> classes = db.getAll();

        for (ClassOffline cn : classes) {
            //postRequest(cn.getStudent_id(), cn.getClass_id(), cn.getPresent(), cn.getManual());
            Log.d("anus", cn.getStudent_id() + " " + cn.getClass_id() + " " + cn.getPresent() + " " + cn.getManual());
        }
    }
    /**
     * Handles the communication with the Django framework
     *
     * @param student  the student's id
     * @param event    the class id
     * @param attended whether the student attended
     * @param manual   whether the student attendance was manually added
     *                 TODO OnFailure saves the event to the local database
     */

    public void postRequest(final String student, final String event, final String attended, final String manual) {
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://capstone.blny.me/studentevent/";
        OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("student", student);
            postdata.put("event", event);
            postdata.put("attended", attended);
            postdata.put("manual", manual);
            Log.d(getPackageName(), postdata.toString());
            Log.d(getPackageName(), event);
        } catch (JSONException e) {
            Log.d(getPackageName(), e.getMessage());
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json")
                .header("Authorization", "Token " + prefs.getString("student_token", ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage();
                Log.d(getPackageName(), mMessage);
                Log.d(getPackageName(), "OnFailure");
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String mMessage = response.body().string();
                Log.d(getPackageName(), mMessage);
                Log.d(getPackageName(), "OnResponse");
            }
        });
    }

    private class SendfeedbackJob extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean[] params) {
            Log.d(getPackageName(), "Start Check");
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
                noInternet = false;
                try {
                    URL myUrl = new URL("https://capstone.blny.me");
                    URLConnection connection = myUrl.openConnection();
                    connection.setConnectTimeout(500);
                    connection.connect();
                    Log.d(getPackageName(), "Connection to server");
                    prefs.edit().putBoolean("connected", true).apply();
                    canConnect = true;
                    return true;
                } catch (Exception e) {
                    Log.e("e", e.toString());
                    canConnect = false;
                    return false;
                }


            } else {
                noInternet = true;
                canConnect = false;
                prefs.edit().putBoolean("connected", false).apply();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean message) {
            //process message
            CreateDialog();
        }
    }

    public void CreateDialog() {
        if (noInternet && !open) {
            open = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Connection!");
            builder.setMessage("Please turn on you WIFI or Mobile Datat!");
            builder.setCancelable(true);
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    open = false;
                    Intent in = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(in);

                }
            });
            builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    open = false;
                    Toast.makeText(getApplicationContext(), "All Classes will be uploaded on next ", Toast.LENGTH_LONG).show();
                }
            });

            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                        dialog.dismiss();
                        open = false;
                    }
                    return true;
                }
            });
            builder.show();
        }

        if (!noInternet && !open) {
            if (!canConnect) {
                open = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("No Connection!");
                builder.setMessage("App could not communicate with server.");
                builder.setCancelable(true);
                builder.setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        open = false;
                        Toast.makeText(getApplicationContext(), "All check in's will be uploaded on next connection", Toast.LENGTH_LONG).show();

                    }
                });
                builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        open = false;
                        finishAndRemoveTask();
                        System.exit(0);
                    }
                });

                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            finish();
                            dialog.dismiss();
                            open = false;
                        }
                        return true;
                    }
                });
                builder.show();
            }
        }
    }
}

