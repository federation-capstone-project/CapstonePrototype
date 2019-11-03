package au.edu.federation.capstoneprototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import au.edu.federation.capstoneprototype.Base.Class;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SettingFragment extends Fragment {
    SharedPreferences prefs;
    CheckBox bluetooth_auto;
    Button sync_classes;
    DatabaseHandler db;
    Button notification_settings_button,logout_button;
    Context context;
    TextView manufacturer_textview,model_textview,phone_name_textview,db_connection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Settings");
        db = new DatabaseHandler(getContext());
        bluetooth_auto = view.findViewById(R.id.cb_bluetooth_auto);
        sync_classes = view.findViewById(R.id.btn_redownload_data);
        bluetooth_auto.setChecked(prefs.getBoolean("bluetooth_auto", true));

        context = getContext();
        manufacturer_textview = view.findViewById(R.id.tv_manu);
        model_textview = view.findViewById(R.id.tv_model);
        phone_name_textview = view.findViewById(R.id.tv_phone_name);
        model_textview.setText(Build.MODEL);
        phone_name_textview.setText(Settings.Secure.getString(context.getContentResolver(), "bluetooth_name"));
        manufacturer_textview.setText(Build.MANUFACTURER);
        logout_button = view.findViewById(R.id.btn_logout);
        notification_settings_button = view.findViewById(R.id.btn_android_notification);
        bluetooth_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (bluetooth_auto.isChecked()) {
                    prefs.edit().putBoolean("bluetooth_auto", true).apply();
                } else {
                    prefs.edit().putBoolean("bluetooth_auto", false).apply();
                }

            }
        });

        sync_classes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getContext().deleteDatabase("studentClasses");
                getStudentClasses();
                getStudentInfo();
            }
        });
        notification_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", context.getPackageName());
                    intent.putExtra("app_uid", context.getApplicationInfo().uid);
                } else {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                }
                context.startActivity(intent);
            }
        });
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.deleteDatabase("studentClasses");
                prefs.edit().clear().apply();
                Intent myIntent = new Intent(getContext(), LoginActivity.class);
                startActivity(myIntent);
                getActivity().finish();
            }
        });

    }
    /**
     * Handles the communication with the Django framework with fetching a students classes
     *
     */
    public void getStudentClasses() {
        String url = getString(R.string.base_url) + "/myevents/?format=json";
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
                Log.d(getActivity().getPackageName(), mMessage);
                Log.d(getActivity().getPackageName(), "OnFailure");
                //call.cancel();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body().string();
                Log.d(getActivity().getPackageName(), mMessage);
                Log.d(getActivity().getPackageName(), "OnResponse");
                try {
                    JSONArray jsonObject = new JSONArray(mMessage);
                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject object = jsonObject.getJSONObject(i);
                        Log.d(getActivity().getPackageName(), object.getString("event_title"));
                        db.addClass(new Class(object.getInt("id"), object.getString("course_code"), object.getString("event_title"), object.getInt("event_clinician"), object.getString("clinician_name"), object.getString("event_location"), object.getString("clinician_mac"), object.getString("event_starttime"), object.getString("event_starttime"), object.getString("event_finishtime"), "false", "false"));
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Class Sync Complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                    db.close();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }
    /**
     * Handles the communication with the Django framework with fetching a students details
     *
     */
    public void getStudentInfo() {
        String url = getString(R.string.base_url) + "/myinfo/?format=json";
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
                Log.d(getActivity().getPackageName(), mMessage);
                Log.d(getActivity().getPackageName(), "OnFailure");
                //call.cancel();
                //db_connection.setText("Datebase Connection: False");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mMessage = response.body().string();
                Log.d(getActivity().getPackageName(), mMessage);
                Log.d(getActivity().getPackageName(), "OnResponse");
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
                            Toast.makeText(getContext(), "Info Sync Complete", Toast.LENGTH_SHORT).show();
                        }
                    });
                    db.close();
                    //db_connection.setText("Datebase Connection: True");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

}