package au.edu.federation.capstoneprototype;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ClassScheduleFragment extends Fragment {

    private TextView dateview;

    private Context _context;
    private Button openDatePickerButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_class_schedule, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        dateview = view.findViewById(R.id.datetemp);
        _context = this.getContext();
                super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Class Schedule");
        openDatePickerButton  = view.findViewById(R.id.button);
        openDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(_context, new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateview.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                   }
               },year,month,dayOfMonth);
                datePickerDialog.show();
            }
        });


    }

}
