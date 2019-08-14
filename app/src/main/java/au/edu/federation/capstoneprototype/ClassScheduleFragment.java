package au.edu.federation.capstoneprototype;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class ClassScheduleFragment extends Fragment {

    private TextView dateview;
    public LocalDateTime s;
    private Context _context;
    private Button openDatePickerButton;
    ArrayList<CalItem> arrayList;
    CalendarList adapter;
    CalItem newCal;


    public ArrayList<CalItem> sss;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    // TODO Proper variable names @lcopsey
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        arrayList = new ArrayList<CalItem>();
        adapter = new CalendarList(getContext(), arrayList);
        ListView a = getView().findViewById(R.id.cal_dynamic);
        a.setAdapter(adapter);
        newCal = null;
        sss = new ArrayList<CalItem>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CalItem qq = new CalItem("Bone Removal 101", "N. Hall", "N534L", "", LocalDateTime.now());
            sss.add(qq);
            CalItem ww = new CalItem("Bone Removal 101", "N. Hall", "N534L", "", LocalDateTime.now().plusDays(1));
            sss.add(ww);

            CalItem ee = new CalItem("Bone Removal 101", "N. Hall", "N534L", "", LocalDateTime.now());
            sss.add(ee);
            CalItem rr = new CalItem("Bone Removal 101", "N. Hall", "N534L", "", LocalDateTime.now().plusDays(2));
            sss.add(rr);
        }

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newCal = new CalItem("Bone Removal 101", "N. Hall", "N-298L", "9:30 - 14/08/2019", LocalDateTime.now());
        }

        adapter.add(newCal);*/
        dateview = view.findViewById(R.id.datetemp);
        _context = this.getContext();
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Class Schedule");

        openDatePickerButton = view.findViewById(R.id.btnitem);
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
                        adapter.clear();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            for (int i = 0; i < sss.size(); i++) {

                                if (sss.get(i).ClassDateTime.getDayOfMonth() == dayOfMonth) {
                                    adapter.add(sss.get(i));
                                }
                            }
                            if (dayOfMonth == LocalDateTime.now().getDayOfMonth()) {


                            }
                        }

                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });


    }

}
