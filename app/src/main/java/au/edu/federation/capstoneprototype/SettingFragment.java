package au.edu.federation.capstoneprototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpHeaders;


public class SettingFragment extends Fragment {
    SharedPreferences prefs;
    EditText student_id;
    CheckBox bluetooth_auto;
    Button sync_classes;
    DatabaseHandler db;
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
        student_id = view.findViewById(R.id.et_student_id);
        bluetooth_auto = view.findViewById(R.id.cb_bluetooth_auto);
        sync_classes = view.findViewById(R.id.btn_sync_classes);
        student_id.setText(prefs.getString("student_id", "69"));
        bluetooth_auto.setChecked(prefs.getBoolean("bluetooth_auto", true));
        student_id.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                prefs.edit().putString("student_id", s.toString()).apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
        bluetooth_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(bluetooth_auto.isChecked()) {
                    prefs.edit().putBoolean("bluetooth_auto", true).apply();
                }else{
                    prefs.edit().putBoolean("bluetooth_auto", false).apply();
                }

            }
        });

        sync_classes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getContext().deleteDatabase("studentClasses");
                getStudentClasses();
            }
        });
    }
    public void getStudentClasses() {
        String url = String.format("https://capstone.blny.me/myevents/%s/?format=json", prefs.getString("student_id", "69" ));
        OkHttpClient client = new OkHttpClient();

        String credentials = Credentials.basic("administrator", "PotatoPancake1");
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("Authorization", credentials)
                //.header("Authorization", "Token f89c13e25cd9d21a9d99d061d8e05e1cf5cff569")
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
                        db.addClass(new Class(object.getInt("id"),object.getString("course_code"), object.getString("event_title"), object.getInt("event_clinician"), object.getString("clinician_name"), object.getString("event_location"), object.getString("clinician_mac"), object.getString("event_starttime"), object.getString("event_starttime"), object.getString("event_finishtime"), "false"));
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

}