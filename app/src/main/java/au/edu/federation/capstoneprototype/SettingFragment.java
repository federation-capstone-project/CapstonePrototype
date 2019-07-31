package au.edu.federation.capstoneprototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;


public class SettingFragment extends Fragment {
    SharedPreferences prefs;
    EditText student_id;
    CheckBox bluetooth_auto;
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
        student_id = view.findViewById(R.id.et_student_id);
        bluetooth_auto = view.findViewById(R.id.cb_bluetooth_auto);
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

    }
}