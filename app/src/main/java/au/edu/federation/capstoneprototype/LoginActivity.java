package au.edu.federation.capstoneprototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import au.edu.federation.capstoneprototype.Base.Class;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences prefs;
    DatabaseHandler db;
    EditText etUsername, etPassword;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseHandler(this);
        prefs = this.getSharedPreferences("prefs",Context.MODE_PRIVATE);

        if(prefs.getBoolean("logged_in", false)){
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(etUsername.getText().toString()) || !TextUtils.isEmpty(etPassword.getText().toString())){
                    getStudentToken(etUsername.getText().toString(), etPassword.getText().toString());
                }else {
                    Toast.makeText(LoginActivity.this, "Please enter a Username and Password!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void getStudentToken(String un, String pw) {
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://capstone.blny.me/api-token-auth/";
        OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("username", un);
            postdata.put("password", pw);
        } catch (JSONException e) {
            Log.d(getPackageName(), e.getMessage());
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json")
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
                try {
                    JSONObject jsonObject = new JSONObject(mMessage);
                        if(jsonObject.has("non_field_errors")){
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Invalid Username and Password", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }else if (jsonObject.has("token")){
                            prefs.edit().putString("student_token", jsonObject.getString("token")).apply();
                            Log.d(getPackageName(), jsonObject.getString("token"));
                            prefs.edit().putBoolean("logged_in", true).apply();
                        }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Info Sync Complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                    db.close();
                    getStudentInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public void getStudentClasses() {
        String url = String.format("https://capstone.blny.me/myevents/?format=json", prefs.getString("student_id", "69"));
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
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
                try {
                    JSONArray jsonObject = new JSONArray(mMessage);
                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject object = jsonObject.getJSONObject(i);
                        Log.d(getPackageName(), object.getString("event_title"));
                        db.addClass(new Class(object.getInt("id"), object.getString("course_code"), object.getString("event_title"), object.getInt("event_clinician"), object.getString("clinician_name"), object.getString("event_location"), object.getString("clinician_mac"), object.getString("event_starttime"), object.getString("event_starttime"), object.getString("event_finishtime"), "false", "false"));
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Class Sync Complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                    db.close();
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(myIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void getStudentInfo() {
        String url = "https://capstone.blny.me/myinfo/?format=json";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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
                try {
                    JSONArray jsonObject = new JSONArray(mMessage);
                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject object = jsonObject.getJSONObject(i);
                        prefs.edit().putString("student_id", object.getString("student_id")).apply();
                        prefs.edit().putString("student_name", object.getString("student_name")).apply();
                        prefs.edit().putString("student_email", object.getString("student_email")).apply();
                        prefs.edit().putString("student_percentage", object.getString("student_percent_string")).apply();
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Info Sync Complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                    db.close();
                    getStudentClasses();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
