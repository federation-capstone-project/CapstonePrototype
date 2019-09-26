package au.edu.federation.capstoneprototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DetailFragment extends Fragment {
    SharedPreferences prefs;
    TextView student_id, student_name, student_email, student_percentage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("My Details");
        student_id = view.findViewById(R.id.tv_student_id);
        student_name = view.findViewById(R.id.tv_student_name);
        student_email = view.findViewById(R.id.tv_student_email);
        student_percentage = view.findViewById(R.id.tv_student_percentage);
        student_id.setText(prefs.getString("student_id", "1"));
        student_name.setText(prefs.getString("student_name", "Test Student"));
        student_email.setText(prefs.getString("student_email", "student@test.com"));
        student_percentage.setText(prefs.getString("student_percentage", "99%"));

    }

}